package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME_FULL;

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
            + "Parameters: [" + PREFIX_SQUADNAME_FULL + "SQUAD_NAME] ["
            + PREFIX_AVAILABILITY_FULL + "true|false] (at least one criterion is required)\n"
            + "Aliases: " + PREFIX_SQUADNAME + " for " + PREFIX_SQUADNAME_FULL + ", "
            + PREFIX_AVAILABILITY + " for " + PREFIX_AVAILABILITY_FULL + "\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_SQUADNAME_FULL + "Soccer Stars "
            + PREFIX_AVAILABILITY_FULL + "true";
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
