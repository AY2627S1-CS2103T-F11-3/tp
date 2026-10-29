package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.DeleteCommandParser.MESSAGE_EMPTY_NAME;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Availability;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.MatchesFilterCriteriaPredicate;
import seedu.address.model.person.Name;
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
    public void execute_editInvalidIndex_reportsIndexBeforeFieldsWithoutSaving() throws Exception {
        model.setAddressBook(getTypicalAddressBook());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        storage.saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(storage.getAddressBookFilePath());

        for (String fields : List.of("", " n/123@", " n/Alex n/Bo", " injury/unknown",
                " n/Valid Name injury/unknown")) {
            assertCommandException("edit 99" + fields, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        assertCommandException("edit 1", EditCommand.MESSAGE_NOT_EDITED);
        assertCommandException("edit 1 n/123@", Name.MESSAGE_CONSTRAINTS);
        assertCommandException("edit 1 n/Alex n/Bo",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertCommandException("edit 1 n/Valid Name injury/unknown", InjuryStatus.MESSAGE_CONSTRAINTS);
        assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_editInvalidFilteredIndex_reportsIndexBeforeFields() {
        model.setAddressBook(getTypicalAddressBook());
        Person visiblePlayer = model.getAddressBook().getPersonList().getFirst();
        model.updateFilteredPersonList(person -> person.equals(visiblePlayer));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(visiblePlayer));

        assertCommandFailure("edit 2", CommandException.class,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, expectedModel);
        assertCommandFailure("edit 2 n/123@", CommandException.class,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, expectedModel);
        assertCommandFailure("edit 1 n/123@", CommandException.class, Name.MESSAGE_CONSTRAINTS, expectedModel);
        assertEquals(List.of(visiblePlayer), model.getFilteredPersonList());
    }

    @Test
    public void execute_editEmptyList_reportsIndexBeforeFields() {
        assertCommandException("edit 1", Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        assertCommandException("edit 1 n/123@", Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_asciiInjuryInput_savesAndRejectsUnicodeWithoutWriting() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        logic.execute("add n/John Doe sn/U12 pos/Defender injury/FiT");
        logic.execute("edit 1 injury/iNjUrEd");
        Person injured = model.getAddressBook().getPersonList().getFirst();
        assertEquals("injured", injured.getInjuryStatus().value);
        assertEquals(List.of(injured), storage.readAddressBook().orElseThrow().getPersonList());
        Path file = temporaryFolder.resolve("addressBook.json");
        String before = Files.readString(file);
        for (String value : new String[] {"F\u0130T", "f\u0131t", "\u0130NJURED", "\u0131njured",
            "f\u0456t", "\uff46\uff49\uff54"}) {
            assertCommandException("edit 1 injury/" + value, InjuryStatus.MESSAGE_CONSTRAINTS);
            assertParseException("add n/Other Player sn/U12 pos/Defender injury/" + value,
                    InjuryStatus.MESSAGE_CONSTRAINTS);
            assertEquals(List.of(injured), model.getAddressBook().getPersonList());
            assertEquals(before, Files.readString(file));
        }
        logic.execute("edit 1 injury/fIt");
        assertEquals("fit", storage.readAddressBook().orElseThrow().getPersonList().getFirst().getInjuryStatus().value);
    }

    @Test
    public void execute_injuryCommands_saveUpdatesAndRejectInvalidInputWithoutWriting() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        Person player = new PersonBuilder().withName("John Doe").withSquadName("U12").withPosition("Defender")
                .withInjuryStatus("injured").build();
        logic.execute("add n/John Doe sn/U12 pos/Defender injury/INJURED");
        assertEquals(List.of(player), storage.readAddressBook().orElseThrow().getPersonList());

        Person updated = new PersonBuilder(player).withInjuryStatus("fit").withAvailability("unavailable").build();
        logic.execute("edit 1 injury/fit av/false");
        assertEquals(List.of(updated), model.getAddressBook().getPersonList());
        assertEquals(List.of(updated), storage.readAddressBook().orElseThrow().getPersonList());

        Path file = temporaryFolder.resolve("addressBook.json");
        String before = Files.readString(file);
        assertCommandException("edit 1 injury/", InjuryStatus.MESSAGE_CONSTRAINTS);
        assertCommandException("edit 1 injury/recovering", InjuryStatus.MESSAGE_CONSTRAINTS);
        assertCommandException("edit 1 injury/unknown", InjuryStatus.MESSAGE_CONSTRAINTS);
        assertParseException("add n/Other Player sn/U12 pos/Defender injury/unknown",
                InjuryStatus.MESSAGE_CONSTRAINTS);
        assertCommandException("edit 1 injury/fit injury/injured",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INJURY_FULL));
        assertParseException("add n/Other Player sn/U12 pos/Defender injury/invalid", InjuryStatus.MESSAGE_CONSTRAINTS);
        assertCommandException("edit 2 injury/injured", Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        assertCommandException("add n/John Doe sn/U12 pos/Defender injury/fit",
                "John Doe is already a registered player!");
        assertEquals(before, Files.readString(file));
        assertEquals(List.of(updated), model.getAddressBook().getPersonList());

        logic.execute("edit 1 injury/fit");
        Person reset = new PersonBuilder(updated).withInjuryStatus("fit").build();
        assertEquals(List.of(reset), storage.readAddressBook().orElseThrow().getPersonList());
        logic.execute("edit 1 injury/injured");
        Person injured = new PersonBuilder(updated).withInjuryStatus("injured").build();
        logic.execute("edit 1 av/true");
        assertEquals(List.of(new PersonBuilder(injured).withAvailability("available").build()),
                storage.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void execute_addPlayer_savesOptionalDetailsAndRejectsDuplicate() throws Exception {
        Person player = new PersonBuilder().withName("John Doe").withSquadName("Soccer Stars")
                .withPosition("Goalkeeper").withGuardianName("Jane Doe").withGuardianContact("81234567")
                .withAvailability("unavailable").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(player);
        expectedModel.commitAddressBook();
        String command = "add name/John Doe squad/Soccer Stars position/goal-keeper"
                + " guardian/Jane Doe contact/8123-4567 available/f a l s e";
        assertCommandSuccess(command, "Player John Doe added!", expectedModel);
        JsonAddressBookStorage savedStorage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(new AddressBook(expectedModel.getAddressBook()), savedStorage.readAddressBook().orElseThrow());
        assertCommandException("add n/John Doe sn/Soccer Stars pos/Goalkeeper",
                "John Doe is already a registered player!");
        assertEquals(new AddressBook(expectedModel.getAddressBook()), savedStorage.readAddressBook().orElseThrow());
        assertParseException("add n/Invalid Contact sn/Stars pos/Defender gc/1234567",
                seedu.address.model.person.GuardianContact.MESSAGE_CONSTRAINTS);
        assertEquals(new AddressBook(expectedModel.getAddressBook()), savedStorage.readAddressBook().orElseThrow());

        Person minimal = new PersonBuilder().withName("Mike Ox").withSquadName("Football Fellas")
                .withPosition("Striker").build();
        expectedModel.addPerson(minimal);
        expectedModel.commitAddressBook();
        assertCommandSuccess("add n/Mike Ox sn/Football Fellas pos/Striker", "Player Mike Ox added!", expectedModel);
        assertEquals(new AddressBook(expectedModel.getAddressBook()), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_editAvailability_savesUpdatedPlayerAndFilterUsesNewValue() throws Exception {
        Person player = new PersonBuilder().withName("John Doe").withSquadName("Soccer Stars")
                .withPosition("Goalkeeper").withAvailability("available").build();
        model.addPerson(player);
        Person updatedPlayer = new PersonBuilder(player).withAvailability("unavailable").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(player, updatedPlayer);
        expectedModel.commitAddressBook();

        assertCommandSuccess("edit 1 av/false",
                String.format("Player %s updated!", updatedPlayer.getName()), expectedModel);

        JsonAddressBookStorage savedStorage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(new AddressBook(expectedModel.getAddressBook()), savedStorage.readAddressBook().orElseThrow());

        expectedModel.updateFilteredPersonList(person ->
                person.getAvailability().equals(new Availability("unavailable")));
        assertCommandSuccess("filter av/false", Messages.MESSAGE_PLAYER_LISTED_OVERVIEW,
                expectedModel);
    }

    @Test
    public void execute_editPlayerProfileFields_savesUpdatedPlayer() throws Exception {
        Person player = new PersonBuilder().withName("John Doe").withSquadName("Soccer Stars")
                .withPosition("Goalkeeper").withGuardianName("Jane Doe").withGuardianContact("81234567").build();
        model.addPerson(player);
        Person updatedPlayer = new PersonBuilder(player).withSquadName("Football Fellas").withPosition("Defender")
                .withGuardianName("Mary Doe").withGuardianContact("87654321").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(player, updatedPlayer);
        expectedModel.commitAddressBook();

        assertCommandSuccess("edit 1 sn/Football Fellas pos/Defender guardian/Mary Doe contact/8765-4321",
                String.format("Player %s updated!", updatedPlayer.getName()), expectedModel);

        JsonAddressBookStorage savedStorage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(new AddressBook(expectedModel.getAddressBook()), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_overviewWithUnwritableStorage_succeedsAndPreservesFilter() throws Exception {
        Person player = new PersonBuilder().withSquadName("Alpha").build();
        model.addPerson(player);
        model.updateFilteredPersonList(person -> false);
        JsonAddressBookStorage failingStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("overview.json")) {
                    @Override
                    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                        throw DUMMY_AD_EXCEPTION;
                    }
                };
        logic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("overviewPrefs.json"))));

        CommandResult result = logic.execute("overview");

        assertEquals("Club overview: 1 squad, 1 player.\nAvailable: 1 | Unavailable: 0\n\nSquads:"
                + "\nAlpha: 1 total | 1 available | 0 unavailable", result.getFeedbackToUser());
        assertEquals(List.of(player), model.getAddressBook().getPersonList());
        assertEquals(List.of(), model.getFilteredPersonList());
        assertFalse(result.isExit());
        assertFalse(result.isShowHelp());
    }

    @Test
    public void execute_overviewAfterCommands_usesUpdatedDataAndPreservesDeleteIndexes() throws Exception {
        logic.execute("add n/Alice sn/Alpha pos/Defender");
        logic.execute("add n/Bob sn/Beta pos/Striker av/false");
        logic.execute("filter sn/Beta");
        List<Person> displayedPlayers = List.copyOf(model.getFilteredPersonList());
        assertEquals("Club overview: 2 squads, 2 players.\nAvailable: 1 | Unavailable: 1\n\nSquads:"
                + "\nAlpha: 1 total | 1 available | 0 unavailable"
                + "\nBeta: 1 total | 0 available | 1 unavailable (No players available)",
                logic.execute("overview").getFeedbackToUser());
        assertEquals(displayedPlayers, model.getFilteredPersonList());

        logic.execute("edit 1 av/true");
        assertEquals("Club overview: 2 squads, 2 players.\nAvailable: 2 | Unavailable: 0\n\nSquads:"
                + "\nAlpha: 1 total | 1 available | 0 unavailable"
                + "\nBeta: 1 total | 1 available | 0 unavailable", logic.execute("overview").getFeedbackToUser());
        logic.execute("filter sn/Beta");
        logic.execute("overview");
        logic.execute("delete 1");
        assertEquals("Club overview: 1 squad, 1 player.\nAvailable: 1 | Unavailable: 0\n\nSquads:"
                + "\nAlpha: 1 total | 1 available | 0 unavailable", logic.execute("overview").getFeedbackToUser());
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
        model.setAddressBook(getTypicalAddressBook());
        String listCommand = ListCommand.COMMAND_WORD;

        // Build the expected output based on the players in model
        StringBuilder expectedMessage = new StringBuilder();
        List<Person> list = model.getFilteredPersonList();
        for (int i = 0; i < list.size(); i++) {
            expectedMessage.append(i + 1)
                    .append(". ")
                    .append(list.get(i).toListString());
            if (i < list.size() - 1) {
                expectedMessage.append("\n");
            }
        }

        assertCommandSuccess(listCommand, expectedMessage.toString(), model);
    }

    @Test
    public void execute_deleteMultipleIndices_savesRemainingPlayers() throws Exception {
        model.setAddressBook(getTypicalAddressBook());
        List<Person> originalPersons = List.copyOf(model.getAddressBook().getPersonList());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(originalPersons.get(0));
        expectedModel.commitAddressBook();
        expectedModel.deletePerson(originalPersons.get(2));
        expectedModel.commitAddressBook();
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS, 2,
                "1. Alice Pauline (Unassigned, Unassigned)\n2. Carl Kurz (Unassigned, Unassigned)");

        assertCommandSuccess("delete 1 3", expectedMessage, expectedModel);

        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(new AddressBook(expectedModel.getAddressBook()), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_deleteMultipleNames_savesRemainingPlayers() throws Exception {
        model.setAddressBook(getTypicalAddressBook());
        List<Person> originalPersons = List.copyOf(model.getAddressBook().getPersonList());
        Person first = originalPersons.get(0);
        Person third = originalPersons.get(2);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(first);
        expectedModel.commitAddressBook();
        expectedModel.deletePerson(third);
        expectedModel.commitAddressBook();
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS, 2,
                "1. Alice Pauline (Unassigned, Unassigned)\n2. Carl Kurz (Unassigned, Unassigned)");

        assertCommandSuccess("delete n/" + first.getName().fullName + ", " + third.getName().fullName,
                expectedMessage, expectedModel);

        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(new AddressBook(expectedModel.getAddressBook()), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_deleteWithMissingOrEmptyName_preservesModelAndSavedData() throws Exception {
        model.setAddressBook(getTypicalAddressBook());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        savedStorage.saveAddressBook(model.getAddressBook());
        String validName = model.getAddressBook().getPersonList().getFirst().getName().fullName;

        assertCommandException("delete n/" + validName + ", Missing Player",
                String.format(DeleteCommand.MESSAGE_NAME_NOT_FOUND, "Missing Player"));
        assertParseException("delete n/" + validName + ",",
                MESSAGE_EMPTY_NAME);

        assertEquals(expectedModel, model);
        assertEquals(new AddressBook(expectedModel.getAddressBook()), savedStorage.readAddressBook().orElseThrow());
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

        assertThrows(CommandException.class, () -> logic.execute("delete n/" + validName + ", John Doe"));

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
        assertCommandException("delete 1 a 3 4 testing",
                "Invalid indices: 'a', 'testing'. Enter whole-number indices from 1 to "
                        + model.getFilteredPersonList().size() + ". No players deleted.");
        assertCommandException("delete a b c",
                "Invalid indices: 'a', 'b', 'c'. Enter whole-number indices from 1 to "
                        + model.getFilteredPersonList().size() + ". No players deleted.");

        assertEquals(expectedModel, model);
        assertEquals(new AddressBook(expectedModel.getAddressBook()), savedStorage.readAddressBook().orElseThrow());
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
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")) {
                @Override
                public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                    throw DUMMY_IO_EXCEPTION;
                }
            };
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        logic = new LogicManager(model, new StorageManager(addressBookStorage, userPrefsStorage));

        // TODO: Repeated... maybe create a helper method?
        StringBuilder expectedMessage = new StringBuilder();
        List<Person> list = expectedModel.getFilteredPersonList();
        for (int i = 0; i < list.size(); i++) {
            expectedMessage.append(i + 1)
                    .append(". ")
                    .append(list.get(i).toListString());
            if (i < list.size() - 1) {
                expectedMessage.append("\n");
            }
        }

        assertCommandSuccess(ListCommand.COMMAND_WORD, expectedMessage.toString(), expectedModel);
    }

    @Test
    public void execute_filterWithFailingStorage_succeeds() throws Exception {
        model.setAddressBook(getTypicalAddressBook());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        MatchesFilterCriteriaPredicate predicate = new MatchesFilterCriteriaPredicate(
                Optional.empty(),
                Optional.of(new Availability(Availability.AVAILABLE)),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
        expectedModel.updateFilteredPersonList(predicate);
        JsonAddressBookStorage addressBookStorage = new
                JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw DUMMY_IO_EXCEPTION;
            }
        };
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        logic = new LogicManager(model, new StorageManager(addressBookStorage, userPrefsStorage));

        assertCommandSuccess("filter available/TRUE",
                Messages.getPlayerListSummary(model.getAddressBook().getPersonList().size(),
                        expectedModel.getFilteredPersonList().size()),
                expectedModel);
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
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person personToDelete = model.getAddressBook().getPersonList().getFirst();
        model.updateFilteredPersonList(person -> person.equals(personToDelete));

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);

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
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }
}
