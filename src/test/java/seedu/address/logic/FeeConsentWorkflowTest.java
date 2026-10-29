package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/** Exercises fee and consent commands through validation, model updates, and real storage. */
public class FeeConsentWorkflowTest {
    @TempDir
    public Path directory;

    private LogicManager logic(ModelManager model, JsonAddressBookStorage storage) {
        return new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
    }

    @Test
    public void execute_statusCombinations_normalizesSavesAndResets() throws Exception {
        ModelManager model = new ModelManager();
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("players.json"));
        LogicManager logic = logic(model, storage);
        logic.execute("add n/Alex Tan sn/U12 pos/Defender injury/injured av/false");
        Person original = model.getAddressBook().getPersonList().getFirst();
        assertEquals("unpaid", original.getFeeStatus().value);
        assertEquals("pending", original.getConsentStatus().value);
        for (String fee : new String[] {"paid", "unpaid"}) {
            for (String consent : new String[] {"received", "pending"}) {
                logic.execute("edit 1 fee/ " + fee + " consent/ " + consent);
                Person expected = new PersonBuilder(original).withFeeStatus(fee).withConsentStatus(consent).build();
                assertEquals(List.of(expected), model.getAddressBook().getPersonList());
                assertEquals(List.of(expected), storage.readAddressBook().orElseThrow().getPersonList());
            }
        }
        logic.execute("edit 1 fee/ PaId ");
        logic.execute("edit 1 consent/ ReCeIvEd ");
        Person recorded = new PersonBuilder(original).withFeeStatus("paid").withConsentStatus("received").build();
        assertEquals(List.of(recorded), storage.readAddressBook().orElseThrow().getPersonList());
        logic.execute("edit 1 fee/unpaid consent/pending");
        assertEquals(List.of(original), storage.readAddressBook().orElseThrow().getPersonList());
        logic.execute("add n/Other Player sn/U14 pos/Striker fee/ UNPAID consent/ PENDING");
        Person added = model.getAddressBook().getPersonList().get(1);
        assertEquals("unpaid", added.getFeeStatus().value);
        assertEquals("pending", added.getConsentStatus().value);
        logic.execute("add n/Fee Only sn/U14 pos/Striker fee/paid");
        logic.execute("add n/Consent Only sn/U14 pos/Striker consent/received");
        assertEquals("pending", model.getAddressBook().getPersonList().get(2).getConsentStatus().value);
        assertEquals("unpaid", model.getAddressBook().getPersonList().get(3).getFeeStatus().value);
    }

    @Test
    public void execute_unicodeStatusLookalikes_rejectsWithoutMutationOrWriting() throws Exception {
        ModelManager model = new ModelManager();
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("players.json"));
        LogicManager logic = logic(model, storage);
        logic.execute("add n/Alex Tan sn/U12 pos/Defender fee/paid consent/received");
        List<Person> before = List.copyOf(model.getAddressBook().getPersonList());
        String json = Files.readString(storage.getAddressBookFilePath());
        for (String fields : new String[] {"fee/PA\u0130D", "fee/pa\u0131d", "fee/UNPA\u0130D", "fee/unpa\u0131d",
            "fee/p\u0430id", "fee/\uff50\uff41\uff49\uff44", "consent/RECE\u0130VED", "consent/rece\u0131ved",
            "consent/PEND\u0130NG", "consent/pend\u0131ng", "consent/rec\u0435ived", "consent/\uff50ending"}) {
            assertThrows(CommandException.class, () -> logic.execute("edit 1 " + fields));
            assertThrows(ParseException.class, () ->
                    logic.execute("add n/Other Player sn/U12 pos/Defender " + fields));
            assertEquals(before, model.getAddressBook().getPersonList());
            assertEquals(json, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void execute_invalidInput_doesNotMutateModelOrWriteFile() throws Exception {
        ModelManager model = new ModelManager();
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("players.json"));
        LogicManager logic = logic(model, storage);
        logic.execute("add n/Alex Tan sn/U12 pos/Defender fee/paid consent/received");
        List<Person> before = List.copyOf(model.getAddressBook().getPersonList());
        String json = Files.readString(storage.getAddressBookFilePath());
        for (String field : new String[] {"fee/", "consent/"}) {
            for (String value : new String[] {"", "  ", "unknown", "true", "invalid", "pa id"}) {
                assertThrows(CommandException.class, () -> logic.execute("edit 1 " + field + value));
                assertThrows(ParseException.class, () ->
                        logic.execute("add n/Other Player sn/U12 pos/Defender " + field + value));
            }
        }
        for (String fields : new String[] {"fee/paid fee/unpaid", "consent/received consent/pending",
            "fee/unpaid consent/invalid", "fee/invalid consent/pending"}) {
            assertThrows(CommandException.class, () -> logic.execute("edit 1 " + fields));
            assertThrows(ParseException.class, () ->
                    logic.execute("add n/Other Player sn/U12 pos/Defender " + fields));
        }
        assertThrows(CommandException.class, () -> logic.execute("edit 2 fee/paid"));
        assertThrows(CommandException.class, () ->
                logic.execute("add n/Alex Tan sn/U12 pos/Defender fee/unpaid consent/pending"));
        assertEquals(before, model.getAddressBook().getPersonList());
        assertEquals(json, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_filteredTargetAndUnrelatedEdits_preserveStatusesAndProfile() throws Exception {
        ModelManager model = new ModelManager();
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("players.json"));
        LogicManager logic = logic(model, storage);
        logic.execute("add n/First Player sn/U14 pos/Striker");
        logic.execute("add n/Alex Tan sn/U12 pos/Defender injury/injured av/false g/Guardian gc/81234567 t/captain");
        Person original = model.getAddressBook().getPersonList().get(1);
        logic.execute("filter sn/U12");
        logic.execute("edit 1 fee/paid consent/received");
        Person recorded = new PersonBuilder(original).withFeeStatus("paid").withConsentStatus("received").build();
        assertEquals(recorded, model.getAddressBook().getPersonList().get(1));
        assertEquals("unpaid", model.getAddressBook().getPersonList().getFirst().getFeeStatus().value);
        for (String edit : new String[] {"n/New Name", "av/true", "injury/fit", "g/", "gc/", "t/leader"}) {
            logic.execute("edit 2 " + edit);
            Person updated = model.getAddressBook().getPersonList().get(1);
            assertEquals(recorded.getFeeStatus(), updated.getFeeStatus());
            assertEquals(recorded.getConsentStatus(), updated.getConsentStatus());
        }
        logic.execute("remark 2 r/Left footed");
        Person updated = storage.readAddressBook().orElseThrow().getPersonList().get(1);
        assertEquals(recorded.getFeeStatus(), updated.getFeeStatus());
        assertEquals(recorded.getConsentStatus(), updated.getConsentStatus());
    }

    @Test
    public void execute_failedSave_restoresStatusData() throws Exception {
        ModelManager model = new ModelManager();
        Person original = new PersonBuilder().withFeeStatus("paid").withConsentStatus("received").build();
        model.addPerson(original);
        model.commitAddressBook();
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("players.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Simulated failure");
            }
        };
        LogicManager logic = logic(model, storage);
        assertThrows(CommandException.class, () -> logic.execute("edit 1 fee/unpaid consent/pending"));
        assertEquals(List.of(original), model.getAddressBook().getPersonList());
        assertThrows(CommandException.class, () ->
                logic.execute("add n/Other Player sn/U12 pos/Defender fee/paid consent/received"));
        assertEquals(List.of(original), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_statusEdit_logsFieldPresenceWithoutPlayerDetails() throws Exception {
        Logger logger = LogsCenter.getLogger(EditCommand.class);
        Level previousLevel = logger.getLevel();
        List<String> messages = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                messages.add(record.getMessage());
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        };
        logger.addHandler(handler);
        logger.setLevel(Level.FINE);
        try {
            ModelManager model = new ModelManager();
            model.addPerson(new PersonBuilder().build());
            LogicManager logic = logic(model, new JsonAddressBookStorage(directory.resolve("players.json")));
            logic.execute("edit 1 fee/paid");
            logic.execute("edit 1 consent/received");
            assertThrows(CommandException.class, () -> logic.execute("edit 2 fee/paid"));
            assertEquals(3, messages.size());
            assertTrue(messages.get(0).contains("fee field supplied=true, consent field supplied=false"));
            assertTrue(messages.get(1).contains("fee field supplied=false, consent field supplied=true"));
            assertTrue(messages.get(2).contains("out of range"));
            assertFalse(String.join(" ", messages).contains("Amy Bee"));
            assertFalse(String.join(" ", messages).contains("received"));
        } finally {
            logger.removeHandler(handler);
            logger.setLevel(previousLevel);
        }
    }
}
