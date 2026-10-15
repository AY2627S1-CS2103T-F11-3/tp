package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Returns to the main player list without changing its filter or indexes.
 */
public class MainCommand extends Command {
    public static final String COMMAND_WORD = "main";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Returns to the main player list, preserving the current filter and indexes.\n"
            + "Parameters: none\nExample: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "Main page displayed. Current filter and indexes are preserved.";

    @Override
    public boolean isAddressBookMutating() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
