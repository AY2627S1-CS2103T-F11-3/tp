package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.model.Model;

/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows a list of all players and their squad names, positions and availability.\n"
            + "Parameters: none\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_FILTERS_CLEARED = "Showing all %d %s. Search and filters cleared.";

    @Override
    public boolean isAddressBookMutating() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        assert model != null : "Model must not be null when executing ListCommand";
        requireNonNull(model);

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        int playerCount = model.getFilteredPersonList().size();
        return new CommandResult(String.format(MESSAGE_FILTERS_CLEARED, playerCount,
                playerCount == 1 ? "player" : "players"));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ListCommand); // instanceof handles nulls
    }
}
