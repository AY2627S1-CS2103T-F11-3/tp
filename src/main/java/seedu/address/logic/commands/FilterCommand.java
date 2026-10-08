package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.MatchesFilterCriteriaPredicate;

/**
 * Displays persons matching the supplied filter criteria.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Displays players matching squad and/or availability.\n"
            + "Parameters: /squad SQUAD_NAME [/availability true|false]\n"
            + "Example: " + COMMAND_WORD + " /squad Soccer Stars /availability true";
    public static final String MESSAGE_NO_MATCHING_PLAYERS = "No matching players found!";

    private final MatchesFilterCriteriaPredicate predicate;

    public FilterCommand(MatchesFilterCriteriaPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public boolean isAddressBookMutating() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int numberOfMatches = model.getFilteredPersonList().size();
        String message = numberOfMatches == 0 ? MESSAGE_NO_MATCHING_PLAYERS
                : String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, numberOfMatches);
        return new CommandResult(message);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof FilterCommand otherFilterCommand
                && predicate.equals(otherFilterCommand.predicate));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
