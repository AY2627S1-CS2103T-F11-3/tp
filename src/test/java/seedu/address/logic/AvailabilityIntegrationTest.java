package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

/**
 * Verifies availability commands through parsing, filtering and persistent storage.
 */
public class AvailabilityIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private final Model model = new ModelManager();
    private final Person amy = new PersonBuilder().withName("Amy Tan").withSquadName("U12")
            .withRemark("Left footed").build();
    private final Person ben = new PersonBuilder().withName("Ben Tan").withSquadName("U12").build();
    private final Person cal = new PersonBuilder().withName("Cal Lee").withSquadName("U14").build();
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
    public void execute_filteredBulkUpdate_savesAndReloadsAllChanges() throws Exception {
        logic.execute("filter sn/U12 av/true");

        logic.execute("availability 1 2 av/false");

        List<Person> expected = List.of(new PersonBuilder(amy).withAvailability("unavailable").build(),
                new PersonBuilder(ben).withAvailability("unavailable").build(), cal);
        assertEquals(expected, model.getAddressBook().getPersonList());
        assertEquals(List.of(), model.getFilteredPersonList());
        ReadOnlyAddressBook reloaded = storage.readAddressBook().orElseThrow();
        assertEquals(expected, reloaded.getPersonList());
        assertEquals(amy.getRemark(), reloaded.getPersonList().getFirst().getRemark());

        logic.execute("list");
        logic.execute("availability 1 2 available/TRUE");
        assertEquals(List.of(amy, ben, cal), storage.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void execute_invalidCommand_preservesModelFilterAndSavedData() throws Exception {
        logic.execute("filter sn/U12");

        assertThrows(CommandException.class, () -> logic.execute("availability 1 3 av/false"));
        assertThrows(ParseException.class, () -> logic.execute("availability 1 av/false av/true"));
        assertThrows(ParseException.class, () -> logic.execute("availability 1 wrong/false"));

        assertEquals(List.of(amy, ben, cal), model.getAddressBook().getPersonList());
        assertEquals(List.of(amy, ben), model.getFilteredPersonList());
        assertEquals(List.of(amy, ben, cal), storage.readAddressBook().orElseThrow().getPersonList());
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
                logic.execute("availability 1 2 av/false"));

        assertEquals("Could not save data due to the following error: Disk unavailable", exception.getMessage());
        assertEquals(List.of(amy, ben, cal), model.getAddressBook().getPersonList());
        assertEquals(List.of(amy, ben), model.getFilteredPersonList());
        assertEquals(List.of(amy, ben, cal), storage.readAddressBook().orElseThrow().getPersonList());
    }

    private Logic createLogic(JsonAddressBookStorage addressBookStorage) {
        return new LogicManager(model, new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }
}
