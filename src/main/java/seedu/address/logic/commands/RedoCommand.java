package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Reverts the {@code model}'s address book to its previously undone state.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Redoes the previous undo command.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_REDO_SUCCESS = "Redid the last undo!";
    public static final String MESSAGE_NO_COMMAND_TO_REDO = "There is no undo to redo.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.canRedoAddressBook()) {
            throw new CommandException(MESSAGE_NO_COMMAND_TO_REDO);
        }

        model.redoAddressBook();
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_REDO_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof RedoCommand);
    }
}
