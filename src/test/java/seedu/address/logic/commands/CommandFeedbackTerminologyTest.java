package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/** Verifies consistent product terminology across command feedback and usage messages. */
public class CommandFeedbackTerminologyTest {
    private static final Pattern LEGACY_TERMS = Pattern.compile("\\b(persons?|address\\s+book)\\b",
            Pattern.CASE_INSENSITIVE);

    @Test
    public void commandMessages_allExistingCommands_doNotUseLegacyTerminology() throws IllegalAccessException {
        List<Class<? extends Command>> commandTypes = List.of(AddCommand.class, ClearCommand.class,
                DeleteCommand.class, EditCommand.class, ExitCommand.class, FilterCommand.class,
                FindCommand.class, HelpCommand.class, ListCommand.class, MainCommand.class,
                MarkCommand.class, OverviewCommand.class, RedoCommand.class, RemarkCommand.class,
                SortCommand.class, UndoCommand.class);

        for (Class<? extends Command> type : commandTypes) {
            for (Field field : type.getFields()) {
                if (field.getType() != String.class || !Modifier.isStatic(field.getModifiers())
                        || !(field.getName().startsWith("MESSAGE_")
                        || field.getName().equals("SHOWING_HELP_MESSAGE"))) {
                    continue;
                }
                String message = (String) field.get(null);
                assertFalse(LEGACY_TERMS.matcher(message).find(), () ->
                        type.getSimpleName() + "." + field.getName() + ": " + message);
            }
        }
    }

    @Test
    public void changedFeedback_usesPlayersAndSquadLink() {
        assertEquals("All player profiles have been cleared!", ClearCommand.MESSAGE_SUCCESS);
        assertEquals("Exiting SquadLink ...", ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT);
        assertEquals("This player already exists in SquadLink.", EditCommand.MESSAGE_DUPLICATE_PERSON);
        assertEquals("Added remark to player: %1$s", RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS);
        assertEquals("Removed remark from player: %1$s", RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS);
    }
}
