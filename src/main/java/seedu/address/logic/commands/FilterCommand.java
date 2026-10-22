package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONSENT_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY_FULL;
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

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Displays players matching squad and/or status criteria.\n"
            + "Parameters: [" + PREFIX_SQUADNAME_FULL + "SQUAD_NAME] ["
            + PREFIX_AVAILABILITY_FULL + "true|false] ["
            + PREFIX_INJURY_FULL + "fit|injured] ["
            + PREFIX_FEE_FULL + "paid|unpaid] ["
            + PREFIX_CONSENT_FULL + "received|pending] (at least one criterion is required)\n"
            + "Aliases: " + PREFIX_SQUADNAME + " for " + PREFIX_SQUADNAME_FULL + ", "
            + PREFIX_AVAILABILITY + " for " + PREFIX_AVAILABILITY_FULL + "\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_SQUADNAME_FULL + "Soccer Stars "
            + PREFIX_AVAILABILITY_FULL + "true "
            + PREFIX_INJURY_FULL + "injured "
            + PREFIX_FEE_FULL + "unpaid";

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
        assert model != null : "Model cannot be null when executing FilterCommand";
        assert predicate != null : "Predicate cannot be null when executing FilterCommand";
        model.updateFilteredPersonList(predicate);
        return new CommandResult(Messages.getPlayerListSummary(model.getAddressBook().getPersonList().size(),
                model.getFilteredPersonList().size()));
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
