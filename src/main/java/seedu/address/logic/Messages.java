package seedu.address.logic;

import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONSENT_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME_FULL;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The player index provided is invalid.";
    public static final String MESSAGE_MISSING_INDEX = "Error! Index must be specified!";
    public static final String MESSAGE_NON_NUMERIC_INDEX =
            "Invalid index entered! Index should only contain numerical characters!";
    public static final String MESSAGE_INDEX_BELOW_ONE = "Invalid index entered! Index should be at least 1!";
    public static final String MESSAGE_INDEX_TOO_LARGE = "Invalid index entered! Index is too large!";
    public static final String MESSAGE_PLAYER_LISTED_OVERVIEW = "1 player listed!";
    public static final String MESSAGE_PLAYERS_LISTED_OVERVIEW = "%1$d players listed!";
    public static final String MESSAGE_NO_REGISTERED_PLAYERS =
            "No players registered. Add your first player, for example:\n"
            + "add n/Amy Tan sn/U12 pos/Defender";
    public static final String MESSAGE_NO_MATCHING_PLAYERS =
            "No matching players found. Change your search or filter, or use list to show all players.";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";



    // Name
    public static final String MESSAGE_MISSING_NAME = String.format(
            "Error! Name must be specified (using %s or %s)!",
            PREFIX_NAME_FULL.getPrefix(),
            PREFIX_NAME.getPrefix());
    public static final String MESSAGE_EMPTY_NAME = "Invalid name list! Specify a name between each comma!";

    // Squad
    public static final String MESSAGE_MISSING_SQUAD = String.format(
            "Error! Squad name must be specified (using %s or %s)!",
            PREFIX_SQUADNAME_FULL.getPrefix(),
            PREFIX_SQUADNAME.getPrefix());
    public static final String MESSAGE_DUPLICATE_SQUAD =
            "Error! Parameter " + PREFIX_SQUADNAME_FULL + " cannot be specified more than once!";

    // Position
    public static final String MESSAGE_MISSING_POSITION = String.format(
            "Error! Position must be specified (using %s or %s)!",
            PREFIX_POSITION_FULL.getPrefix(),
            PREFIX_POSITION.getPrefix());
    public static final String MESSAGE_DUPLICATE_POSITION =
            "Error! Parameter " + PREFIX_POSITION_FULL + " cannot be specified more than once!";

    // Availability
    public static final String MESSAGE_MISSING_AVAILABILITY = "Error! Availability must be specified!";
    public static final String MESSAGE_INVALID_AVAILABILITY =
            "Invalid availability entered! Availability should either be true or false!";
    public static final String MESSAGE_DUPLICATE_AVAILABILITY =
            "Error! Parameter " + PREFIX_AVAILABILITY_FULL + " cannot be specified more than once!";

    // Consent
    public static final String MESSAGE_MISSING_CONSENT =
            "Error! Consent status must be specified!";
    public static final String MESSAGE_INVALID_CONSENT =
            "Invalid consent status entered! Consent status should either be received or pending!";
    public static final String MESSAGE_DUPLICATE_CONSENT =
            "Error! Parameter " + PREFIX_CONSENT_FULL + " cannot be specified more than once!";

    // Injury
    public static final String MESSAGE_MISSING_INJURY =
            "Error! Injury status must be specified!";
    public static final String MESSAGE_INVALID_INJURY =
            "Invalid injury status entered! Injury status should either be fit or injured!";
    public static final String MESSAGE_DUPLICATE_INJURY =
            "Error! Parameter " + PREFIX_INJURY_FULL + " cannot be specified more than once!";

    // Fee
    public static final String MESSAGE_MISSING_FEE =
            "Error! Fee status must be specified!";
    public static final String MESSAGE_INVALID_FEE =
            "Invalid fee status entered! Fee status should either be paid or unpaid!";
    public static final String MESSAGE_DUPLICATE_FEE =
            "Error! Parameter " + PREFIX_FEE_FULL + " cannot be specified more than once!";

    // Mark
    public static final String MESSAGE_ONE_STATUS = "Specify exactly one status per mark command.\n"
            + MarkCommand.MESSAGE_USAGE;

    // Remark
    public static final String MESSAGE_MISSING_REMARK = "Error! Remark must not be blank!";

    // Filter (Criterion)
    public static final String MESSAGE_MISSING_CRITERION = "Error! At least one filter criterion must be specified!";

    /** Returns list feedback with a next step when the club or current results are empty. */
    public static String getPlayerListSummary(int totalPlayers, int displayedPlayers) {
        if (totalPlayers == 0) {
            return MESSAGE_NO_REGISTERED_PLAYERS;
        }
        if (displayedPlayers == 0) {
            return MESSAGE_NO_MATCHING_PLAYERS;
        }
        return displayedPlayers == 1 ? MESSAGE_PLAYER_LISTED_OVERVIEW
                : String.format(MESSAGE_PLAYERS_LISTED_OVERVIEW, displayedPlayers);
    }

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats player details to help distinguish players with similar names.
     */
    public static String formatPlayerDetails(Person person) {
        return String.format("%s; Squad: %s; Position: %s; Guardian: %s; Contact: %s; Availability: %s; Remark: %s",
                format(person), person.getSquadName(), person.getPosition(),
                person.getGuardianName() == null ? "Not provided" : person.getGuardianName(),
                person.getGuardianContact() == null ? "Not provided" : person.getGuardianContact(),
                person.getAvailability(), person.getRemark());
    }

}
