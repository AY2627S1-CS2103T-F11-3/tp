package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddCommandParser;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.CliSyntax;
import seedu.address.logic.parser.DeleteCommandParser;
import seedu.address.logic.parser.EditCommandParser;
import seedu.address.logic.parser.FilterCommandParser;
import seedu.address.logic.parser.FindCommandParser;
import seedu.address.logic.parser.MarkCommandParser;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.RemarkCommandParser;
import seedu.address.logic.parser.SortCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Availability;
import seedu.address.model.person.ConsentStatus;
import seedu.address.model.person.FeeStatus;
import seedu.address.model.person.GuardianContact;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.Name;
import seedu.address.model.person.Position;
import seedu.address.model.person.SquadName;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Checks punctuation in error prose without adding punctuation to command examples or prefix lists. */
public class ErrorMessagePunctuationTest {
    @TempDir
    public Path temporaryFolder;

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parserErrorConstants_allProseEndsWithFullStops() throws IllegalAccessException {
        List<Class<?>> types = List.of(Messages.class, AddCommandParser.class, DeleteCommandParser.class,
                EditCommandParser.class, FilterCommandParser.class, FindCommandParser.class,
                MarkCommandParser.class, ParserUtil.class, RemarkCommandParser.class, SortCommandParser.class);
        List<String> nonSentenceMessages = List.of("MESSAGE_NO_REGISTERED_PLAYERS", "MESSAGE_DUPLICATE_FIELDS");
        for (Class<?> type : types) {
            for (Field field : type.getFields()) {
                if (field.getType() != String.class || !Modifier.isStatic(field.getModifiers())
                        || !field.getName().startsWith("MESSAGE_") || field.getName().endsWith("_LISTED_OVERVIEW")
                        || nonSentenceMessages.contains(field.getName())) {
                    continue;
                }
                String message = (String) field.get(null);
                assertErrorSentence(message.lines().findFirst().orElseThrow());
            }
        }
    }

    @Test
    public void fieldConstraints_endWithFullStops() {
        for (String message : List.of(Name.MESSAGE_CONSTRAINTS, SquadName.MESSAGE_CONSTRAINTS,
                GuardianName.MESSAGE_CONSTRAINTS, GuardianContact.MESSAGE_CONSTRAINTS,
                Availability.MESSAGE_CONSTRAINTS, Position.MESSAGE_CONSTRAINTS, InjuryStatus.MESSAGE_CONSTRAINTS,
                FeeStatus.MESSAGE_CONSTRAINTS, ConsentStatus.MESSAGE_CONSTRAINTS, Tag.MESSAGE_CONSTRAINTS)) {
            assertErrorSentence(message);
        }
    }

    @Test
    public void invalidCommands_errorProseEndsWithFullStops() {
        for (String input : List.of("add", "add n/Amy", "add n/Amy sn/U12",
                "add n/! sn/U12 pos/Defender", "edit", "edit 1 av/no", "delete", "delete n/",
                "delete n/Amy,", "filter", "filter available/", "filter av/maybe", "filter injury/no",
                "filter injury/fit injury/injured", "mark", "mark 1", "find", "sort nonsense",
                "list 1", "help 1", "exit 1", "undo 1", "redo 1", "main 1", "overview 1", "unrecognised",
                "mark -1 av/true", "mark 0 av/true", "mark 2147483648 av/true")) {
            ParseException exception = assertThrows(ParseException.class, () -> parser.parseCommand(input), input);
            assertErrorSentence(exception.getMessage().lines().findFirst().orElseThrow());
        }
    }

    @Test
    public void executionErrors_endWithFullStops() throws ParseException {
        for (String input : List.of("edit 1 n/Amy", "delete 1", "delete abc", "delete 0", "delete 2147483648",
                "mark 1 av/false", "remark 1 r/Test", "undo", "redo", "delete n/Missing", "delete n/Missing.")) {
            Command command = parser.parseCommand(input);
            CommandException exception = assertThrows(CommandException.class, () ->
                    command.execute(new ModelManager()), input);
            assertErrorSentence(exception.getMessage());
        }
    }

    @Test
    public void ambiguousDeletion_errorHeaderEndsWithFullStop() throws Exception {
        ModelManager model = new ModelManager();
        parser.parseCommand("add n/Amy sn/U12 pos/Defender").execute(model);
        parser.parseCommand("add n/Amy sn/U14 pos/Defender").execute(model);
        Command command = parser.parseCommand("delete n/Amy");
        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals("Multiple players match the name \"Amy\".",
                exception.getMessage().lines().findFirst().orElseThrow());
        assertErrorSentence(exception.getMessage().lines().findFirst().orElseThrow());
    }

    @Test
    public void missingDeletionName_preservesPunctuationInsideQuotedDetails() throws ParseException {
        Command command = parser.parseCommand("delete n/Missing!");
        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));
        assertEquals("No player found with the full name \"Missing!\".", exception.getMessage());
    }

    @Test
    public void saveErrors_punctuateProseWithoutChangingExceptionDetails() {
        for (String details : List.of("disk full", "disk full.", "path/with punctuation!")) {
            ModelManager model = new ModelManager();
            JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(
                    temporaryFolder.resolve("players.json")) {
                @Override
                public void saveAddressBook(ReadOnlyAddressBook book) throws IOException {
                    throw new IOException(details);
                }
            };
            LogicManager logic = new LogicManager(model, new StorageManager(failingStorage,
                    new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
            CommandException exception = assertThrows(CommandException.class, () -> logic.execute("clear"));
            assertErrorSentence(exception.getMessage().lines().findFirst().orElseThrow());
            assertEquals("Could not save data.\nError details: " + details, exception.getMessage());
        }
    }

    @Test
    public void permissionDenied_errorEndsWithFullStop() {
        ModelManager model = new ModelManager();
        Path dataFile = temporaryFolder.resolve("players.json");
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(dataFile) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook book) throws IOException {
                throw new AccessDeniedException(dataFile.toString());
            }
        };
        LogicManager logic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
        CommandException exception = assertThrows(CommandException.class, () -> logic.execute("clear"));
        assertErrorSentence(exception.getMessage());
    }

    @Test
    public void duplicatePlayer_errorEndsWithFullStop() throws Exception {
        ModelManager model = new ModelManager();
        Command command = parser.parseCommand("add n/Amy sn/U12 pos/Defender");
        command.execute(model);
        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));
        assertErrorSentence(exception.getMessage());
    }

    @Test
    public void invalidMarkStatus_errorProseEndsWithFullStops() throws Exception {
        ModelManager model = new ModelManager();
        parser.parseCommand("add n/Amy sn/U12 pos/Defender").execute(model);
        for (String input : List.of("mark 1 av/no", "mark 1 injury/no", "mark 1 fee/no", "mark 1 consent/no")) {
            Command command = parser.parseCommand(input);
            CommandException exception = assertThrows(CommandException.class, () -> command.execute(model), input);
            assertErrorSentence(exception.getMessage());
        }
    }

    @Test
    public void structuredErrorDetails_preservePrefixesAndCommandExamples() {
        String duplicate = Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME);
        assertTrue(duplicate.endsWith("n/"), duplicate);
        ParseException exception = assertThrows(ParseException.class, () -> parser.parseCommand("find"));
        assertTrue(exception.getMessage().endsWith("Example: find alice bob charlie"), exception.getMessage());
    }

    private void assertErrorSentence(String message) {
        assertTrue(message.endsWith("."), message);
        assertFalse(message.contains("!"), message);
        assertFalse(message.endsWith(".."), message);
    }
}
