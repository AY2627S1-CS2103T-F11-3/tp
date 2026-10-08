package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME_FULL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Availability;
import seedu.address.model.person.MatchesFilterCriteriaPredicate;
import seedu.address.model.person.SquadName;

/**
 * Parses input arguments and creates a {@link FilterCommand}.
 */
public class FilterCommandParser implements Parser<FilterCommand> {
    public static final String MESSAGE_MISSING_CRITERION = "Error! At least one filter criterion must be specified!";
    public static final String MESSAGE_MISSING_SQUAD = "Error! Squad name must be specified!";
    public static final String MESSAGE_MISSING_AVAILABILITY = "Error! Availability must be specified!";
    public static final String MESSAGE_INVALID_AVAILABILITY =
            "Invalid availability entered! Availability should either be true or false!";
    public static final String MESSAGE_DUPLICATE_SQUAD =
            "Error! Parameter " + PREFIX_SQUADNAME_FULL + " cannot be specified more than once!";
    public static final String MESSAGE_DUPLICATE_AVAILABILITY =
            "Error! Parameter " + PREFIX_AVAILABILITY_FULL + " cannot be specified more than once!";

    private static final Pattern PREFIX_TOKEN_PATTERN = Pattern.compile("(?<!\\S)(?<prefix>/\\S+|\\S+/)");

    @Override
    public FilterCommand parse(String args) throws ParseException {
        List<PrefixMatch> prefixes = findPrefixes(args);
        if (prefixes.isEmpty()) {
            if (args.isBlank()) {
                throw new ParseException(MESSAGE_MISSING_CRITERION);
            }
            throw invalidFormat();
        }
        if (!args.substring(0, prefixes.getFirst().start()).isBlank()) {
            throw invalidFormat();
        }

        String squadValue = null;
        String availabilityValue = null;
        for (int index = 0; index < prefixes.size(); index++) {
            PrefixMatch prefix = prefixes.get(index);
            int valueEnd = index + 1 < prefixes.size() ? prefixes.get(index + 1).start() : args.length();
            String value = args.substring(prefix.end(), valueEnd).strip();
            if (isSquadPrefix(prefix.value())) {
                if (squadValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_SQUAD);
                }
                squadValue = value;
            } else if (isAvailabilityPrefix(prefix.value())) {
                if (availabilityValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_AVAILABILITY);
                }
                availabilityValue = value;
            } else {
                throw invalidFormat();
            }
        }

        return new FilterCommand(new MatchesFilterCriteriaPredicate(parseSquad(squadValue),
                parseAvailability(availabilityValue)));
    }

    private List<PrefixMatch> findPrefixes(String args) throws ParseException {
        List<PrefixMatch> prefixes = new ArrayList<>();
        Matcher matcher = PREFIX_TOKEN_PATTERN.matcher(args);
        while (matcher.find()) {
            String prefix = matcher.group("prefix");
            if (!isSupportedPrefix(prefix)) {
                throw invalidFormat();
            }
            prefixes.add(new PrefixMatch(prefix, matcher.start(), matcher.end()));
        }
        return prefixes;
    }

    private boolean isSupportedPrefix(String prefix) {
        return isSquadPrefix(prefix) || isAvailabilityPrefix(prefix);
    }

    private boolean isSquadPrefix(String prefix) {
        return prefix.equals(PREFIX_SQUADNAME.getPrefix()) || prefix.equals(PREFIX_SQUADNAME_FULL.getPrefix());
    }

    private boolean isAvailabilityPrefix(String prefix) {
        return prefix.equals(PREFIX_AVAILABILITY.getPrefix()) || prefix.equals(PREFIX_AVAILABILITY_FULL.getPrefix());
    }

    private Optional<SquadName> parseSquad(String value) throws ParseException {
        if (value == null) {
            return Optional.empty();
        }
        if (value.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_SQUAD);
        }
        try {
            return Optional.of(new SquadName(value));
        } catch (IllegalArgumentException exception) {
            throw new ParseException(SquadName.MESSAGE_CONSTRAINTS, exception);
        }
    }

    private Optional<Availability> parseAvailability(String value) throws ParseException {
        if (value == null) {
            return Optional.empty();
        }
        if (value.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_AVAILABILITY);
        }
        if (value.equalsIgnoreCase("true")) {
            return Optional.of(new Availability(Availability.AVAILABLE));
        }
        if (value.equalsIgnoreCase("false")) {
            return Optional.of(new Availability(Availability.UNAVAILABLE));
        }
        throw new ParseException(MESSAGE_INVALID_AVAILABILITY);
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    private record PrefixMatch(String value, int start, int end) {}
}
