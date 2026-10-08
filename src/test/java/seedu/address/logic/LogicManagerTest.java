package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_NON_NUMERIC_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.DeleteCommandParser.MESSAGE_EMPTY_NAME;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, DeleteCommand.MESSAGE_NO_DISPLAYED_PLAYERS);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_deleteMultipleIndices_savesRemainingPlayers() throws Exception {
        model.setAddressBook(getTypicalAddressBook());
        List<Person> originalPersons = List.copyOf(model.getAddressBook().getPersonList());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(originalPersons.get(0));
        expectedModel.deletePerson(originalPersons.get(2));
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS, 2,
                originalPersons.get(0).getName().fullName + ", " + originalPersons.get(2).getName().fullName);

        assertCommandSuccess("delete 1 3", expectedMessage, expectedModel);

        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(expectedModel.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_deleteMultipleNames_savesRemainingPlayers() throws Exception {
        model.setAddressBook(getTypicalAddressBook());
        List<Person> originalPersons = List.copyOf(model.getAddressBook().getPersonList());
        Person first = originalPersons.get(0);
        Person third = originalPersons.get(2);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(first);
        expectedModel.deletePerson(third);
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS, 2,
                first.getName().fullName + ", " + third.getName().fullName);

        assertCommandSuccess("delete /name " + first.getName().fullName + ", " + third.getName().fullName,
                expectedMessage, expectedModel);

        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(expectedModel.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_deleteWithMissingOrEmptyName_preservesModelAndSavedData() throws Exception {
        model.setAddressBook(getTypicalAddressBook());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        savedStorage.saveAddressBook(model.getAddressBook());
        String validName = model.getAddressBook().getPersonList().getFirst().getName().fullName;

        assertCommandException("delete /name " + validName + ", Missing Player",
                String.format(DeleteCommand.MESSAGE_NAME_NOT_FOUND, "Missing Player"));
        assertParseException("delete /name " + validName + ",",
                MESSAGE_EMPTY_NAME);

        assertEquals(expectedModel, model);
        assertEquals(expectedModel.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_ambiguousNameThenIndex_showsMatchesAndSavesOnlySelectedDeletion() throws Exception {
        model.setAddressBook(getTypicalAddressBook());
        Person firstMatch = new PersonBuilder().withName("John Doe").withSquadName("Squad A").build();
        Person secondMatch = new PersonBuilder().withName("john doe").withSquadName("Squad B").build();
        model.addPerson(firstMatch);
        model.addPerson(secondMatch);
        model.updateFilteredPersonList(person -> person.equals(firstMatch));
        List<Person> originalPersons = List.copyOf(model.getAddressBook().getPersonList());
        String validName = originalPersons.getFirst().getName().fullName;
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        savedStorage.saveAddressBook(model.getAddressBook());

        assertThrows(CommandException.class, () -> logic.execute("delete /name " + validName + ", John Doe"));

        assertEquals(originalPersons, model.getAddressBook().getPersonList());
        assertEquals(originalPersons, savedStorage.readAddressBook().orElseThrow().getPersonList());
        assertEquals(List.of(firstMatch, secondMatch), model.getFilteredPersonList());

        logic.execute("delete 2");

        List<Person> expectedPersons = new ArrayList<>(originalPersons);
        expectedPersons.remove(secondMatch);
        assertEquals(expectedPersons, model.getAddressBook().getPersonList());
        assertEquals(expectedPersons, savedStorage.readAddressBook().orElseThrow().getPersonList());
        assertEquals(List.of(firstMatch), model.getFilteredPersonList());
    }

    @Test
    public void execute_deleteWithInvalidArgument_preservesModelAndSavedData() throws Exception {
        model.setAddressBook(getTypicalAddressBook());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        savedStorage.saveAddressBook(model.getAddressBook());

        assertCommandException("delete 1 999", String.format(DeleteCommand.MESSAGE_INDEX_OUT_OF_RANGE,
                999, model.getFilteredPersonList().size()));
        assertParseException("delete 1 abc", MESSAGE_NON_NUMERIC_INDEX);

        assertEquals(expectedModel, model);
        assertEquals(expectedModel.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_readOnlyCommandWithFailingStorage_succeeds() throws Exception {
        model.setAddressBook(getTypicalAddressBook());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw DUMMY_IO_EXCEPTION;
            }
        };
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        logic = new LogicManager(model, new StorageManager(addressBookStorage, userPrefsStorage));

        assertCommandSuccess(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        model.setAddressBook(getTypicalAddressBook());
        Person personToDelete = model.getAddressBook().getPersonList().getFirst();
        model.updateFilteredPersonList(person -> person.equals(personToDelete));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(personToDelete));
        List<Person> expectedFilteredPersons = List.copyOf(model.getFilteredPersonList());
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        assertCommandFailure("delete 1", CommandException.class, expectedMessage, expectedModel);
        assertEquals(expectedFilteredPersons, model.getFilteredPersonList());
    }
}
