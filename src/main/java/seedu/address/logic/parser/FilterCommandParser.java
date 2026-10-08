package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

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
    public static final String MESSAGE_DUPLICATE_SQUAD = "Error! Parameter /squad cannot be specified more than once!";
    public static final String MESSAGE_DUPLICATE_AVAILABILITY =
            "Error! Parameter /availability cannot be specified more than once!";

    private static final String LONG_SQUAD_PREFIX = "/squad";
    private static final String LONG_AVAILABILITY_PREFIX = "/availability";
    private static final String SHORT_SQUAD_PREFIX = "sn/";
    private static final String SHORT_AVAILABILITY_PREFIX = "av/";
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
            switch (prefix.value()) {
                case LONG_SQUAD_PREFIX, SHORT_SQUAD_PREFIX -> {
                    if (squadValue != null) {
                        throw new ParseException(MESSAGE_DUPLICATE_SQUAD);
                    }
                    squadValue = value;
                }
                case LONG_AVAILABILITY_PREFIX, SHORT_AVAILABILITY_PREFIX -> {
                    if (availabilityValue != null) {
                        throw new ParseException(MESSAGE_DUPLICATE_AVAILABILITY);
                    }
                    availabilityValue = value;
                }
                default -> throw invalidFormat();
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
        return prefix.equals(LONG_SQUAD_PREFIX) || prefix.equals(LONG_AVAILABILITY_PREFIX)
                || prefix.equals(SHORT_SQUAD_PREFIX) || prefix.equals(SHORT_AVAILABILITY_PREFIX);
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
