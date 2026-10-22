package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/** Tests mark commands through parsing, filtering and persistent storage. */
public class MarkIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private final Model model = new ModelManager();
    private final Person amy = new PersonBuilder().withName("Amy Tan").withSquadName("U12")
            .withRemark("Left footed").build();
    private final Person ben = new PersonBuilder().withName("Ben Lee").withSquadName("U12").build();
    private final Person cal = new PersonBuilder().withName("Cal Lim").withSquadName("U14").build();
    private JsonAddressBookStorage storage;
    private Logic logic;

    @BeforeEach
    public void setUp() throws Exception {
        model.addPerson(amy);
        model.addPerson(ben);
        model.addPerson(cal);
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("players.json"));
        storage.saveAddressBook(model.getAddressBook());
        logic = createLogic(storage);
    }

    @Test
    public void execute_allStatuses_saveAndReloadWithoutLosingOtherFields() throws Exception {
        logic.execute("filter sn/U12");
        logic.execute("mark 1 2 fee/PAID");
        logic.execute("mark 1 2 consent/received");
        logic.execute("mark 1 2 injury/injured");
        logic.execute("mark 1 2 av/false");

        Person updatedAmy = markedPlayer(amy);
        Person updatedBen = markedPlayer(ben);
        List<Person> expected = List.of(updatedAmy, updatedBen, cal);
        assertEquals(expected, model.getAddressBook().getPersonList());
        assertEquals(List.of(updatedAmy, updatedBen), model.getFilteredPersonList());
        ReadOnlyAddressBook reloaded = storage.readAddressBook().orElseThrow();
        assertEquals(expected, reloaded.getPersonList());
        assertEquals(amy.getRemark(), reloaded.getPersonList().getFirst().getRemark());

        logic.execute("mark 1 2 available/true");
        logic.execute("mark 1 2 injury/fit");
        logic.execute("mark 1 2 fee/unpaid");
        logic.execute("mark 1 2 consent/pending");
        assertEquals(List.of(amy, ben, cal), storage.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void execute_shrinkingAvailabilityFilter_savesOriginalTargets() throws Exception {
        logic.execute("filter sn/U12 av/true");

        logic.execute("mark 1 2 av/false");

        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(List.of(new PersonBuilder(amy).withAvailability("unavailable").build(),
                new PersonBuilder(ben).withAvailability("unavailable").build(), cal),
                storage.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void execute_invalidInput_preservesModelFilterAndFile() throws Exception {
        logic.execute("filter sn/U12");
        String savedData = Files.readString(storage.getAddressBookFilePath());

        assertThrows(CommandException.class, () -> logic.execute("mark 1 3 fee/paid"));
        assertThrows(ParseException.class, () -> logic.execute("mark 1 fee/paid consent/received"));
        assertThrows(CommandException.class, () -> logic.execute("mark 1 injury/unknown"));
        assertThrows(ParseException.class, () -> logic.execute("mark 1 wrong/paid"));

        assertEquals(List.of(amy, ben, cal), model.getAddressBook().getPersonList());
        assertEquals(List.of(amy, ben), model.getFilteredPersonList());
        assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_mixedIndexErrors_reportsFirstAndPreservesFile() throws Exception {
        logic.execute("filter sn/U12");
        String savedData = Files.readString(storage.getAddressBookFilePath());

        CommandException boundsError = assertThrows(CommandException.class, () ->
                logic.execute("mark 1 4 0 av/true"));
        assertEquals(String.format(MarkCommand.MESSAGE_INVALID_INDEX, 4, 2), boundsError.getMessage());
        CommandException zeroError = assertThrows(CommandException.class, () ->
                logic.execute("mark 1 0 4 av/true"));
        assertEquals(Messages.MESSAGE_INDEX_BELOW_ONE, zeroError.getMessage());

        assertEquals(List.of(amy, ben, cal), model.getAddressBook().getPersonList());
        assertEquals(List.of(amy, ben), model.getFilteredPersonList());
        assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_saveFailure_restoresAllPlayersAndFilter() throws Exception {
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(storage.getAddressBookFilePath()) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Disk unavailable");
            }
        };
        logic = createLogic(failingStorage);
        logic.execute("filter sn/U12 av/true");

        CommandException exception = assertThrows(CommandException.class, () ->
                logic.execute("mark 1 2 av/false"));

        assertEquals("Could not save data due to the following error: Disk unavailable", exception.getMessage());
        assertEquals(List.of(amy, ben, cal), model.getAddressBook().getPersonList());
        assertEquals(List.of(amy, ben), model.getFilteredPersonList());
        assertEquals(List.of(amy, ben, cal), storage.readAddressBook().orElseThrow().getPersonList());
    }

    private Person markedPlayer(Person original) {
        return new PersonBuilder(original).withAvailability("unavailable").withInjuryStatus("injured")
                .withFeeStatus("paid").withConsentStatus("received").build();
    }

    private Logic createLogic(JsonAddressBookStorage addressBookStorage) {
        return new LogicManager(model, new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }
}
