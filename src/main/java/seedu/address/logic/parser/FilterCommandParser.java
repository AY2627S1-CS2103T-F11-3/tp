package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_LEGACY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONSENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONSENT_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY_FULL;
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

    public static final String MESSAGE_MISSING_AVAILABILITY = "Error! Availability must be specified!";
    public static final String MESSAGE_INVALID_AVAILABILITY =
            "Invalid availability entered! Availability should be true, false, available, or unavailable!";
    public static final String MESSAGE_DUPLICATE_AVAILABILITY =
            "Error! Parameter " + PREFIX_AVAILABILITY_FULL + " cannot be specified more than once!";

    public static final String MESSAGE_MISSING_CONSENT =
            "Error! Consent status must be specified!";
    public static final String MESSAGE_INVALID_CONSENT =
            "Invalid consent status entered! Consent status should either be received or pending!";
    public static final String MESSAGE_DUPLICATE_CONSENT =
            "Error! Parameter " + PREFIX_CONSENT_FULL + " cannot be specified more than once!";

    public static final String MESSAGE_MISSING_INJURY =
            "Error! Injury status must be specified!";
    public static final String MESSAGE_INVALID_INJURY =
            "Invalid injury status entered! Injury status should either be fit or injured!";
    public static final String MESSAGE_DUPLICATE_INJURY =
            "Error! Parameter " + PREFIX_INJURY_FULL + " cannot be specified more than once!";

    public static final String MESSAGE_MISSING_FEE =
            "Error! Fee status must be specified!";
    public static final String MESSAGE_INVALID_FEE =
            "Invalid fee status entered! Fee status should either be paid or unpaid!";
    public static final String MESSAGE_DUPLICATE_FEE =
            "Error! Parameter " + PREFIX_FEE_FULL + " cannot be specified more than once!";

    public static final String MESSAGE_MISSING_SQUAD = "Error! Squad name must be specified!";
    public static final String MESSAGE_DUPLICATE_SQUAD =
            "Error! Parameter " + PREFIX_SQUADNAME_FULL + " cannot be specified more than once!";


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

        String availabilityValue = null;
        String consentValue = null;
        String injuryValue = null;
        String feeValue = null;
        String squadValue = null;
        for (int index = 0; index < prefixes.size(); index++) {
            PrefixMatch prefix = prefixes.get(index);
            int valueEnd = index + 1 < prefixes.size() ? prefixes.get(index + 1).start() : args.length();
            String value = args.substring(prefix.end(), valueEnd).strip();
            if (isAvailabilityPrefix(prefix.value())) {
                if (availabilityValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_AVAILABILITY);
                }
                availabilityValue = value;
            } else if (isInjuryPrefix(prefix.value())) {
                if (injuryValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_INJURY);
                }
                injuryValue = value;
            } else if (isFeePrefix(prefix.value())) {
                if (feeValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_FEE);
                }
                feeValue = value;
            } else if (isConsentPrefix(prefix.value())) {
                if (consentValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_CONSENT);
                }
                consentValue = value;
            } else if (isSquadPrefix(prefix.value())) {
                if (squadValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_SQUAD);
                }
                squadValue = value;
            } else {
                throw invalidFormat();
            }
        }

        return new FilterCommand(new MatchesFilterCriteriaPredicate(
                parseSquad(squadValue),
                parseAvailability(availabilityValue),
                parseInjury(injuryValue),
                parseFee(feeValue),
                parseConsent(consentValue)
        ));
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
        return isSquadPrefix(prefix)
                || isAvailabilityPrefix(prefix)
                || isInjuryPrefix(prefix)
                || isFeePrefix(prefix)
                || isConsentPrefix(prefix);
    }

    private boolean isAvailabilityPrefix(String prefix) {
        return prefix.equals(PREFIX_AVAILABILITY.getPrefix()) || prefix.equals(PREFIX_AVAILABILITY_FULL.getPrefix())
                || prefix.equals(PREFIX_AVAILABILITY_LEGACY.getPrefix());
    }

    private boolean isInjuryPrefix(String prefix) {
        return prefix.equals(PREFIX_INJURY.getPrefix()) || prefix.equals(PREFIX_INJURY_FULL.getPrefix());
    }

    private boolean isFeePrefix(String prefix) {
        return prefix.equals(PREFIX_FEE.getPrefix()) || prefix.equals(PREFIX_FEE_FULL.getPrefix());
    }

    private boolean isConsentPrefix(String prefix) {
        return prefix.equals(PREFIX_CONSENT.getPrefix()) || prefix.equals(PREFIX_CONSENT_FULL.getPrefix());
    }

    private boolean isSquadPrefix(String prefix) {
        return prefix.equals(PREFIX_SQUADNAME.getPrefix()) || prefix.equals(PREFIX_SQUADNAME_FULL.getPrefix());
    }

    private Optional<Availability> parseAvailability(String value) throws ParseException {
        if (value == null) {
            return Optional.empty();
        }
        if (value.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_AVAILABILITY);
        }
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("available")) {
            return Optional.of(new Availability(Availability.AVAILABLE));
        }
        if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("unavailable")) {
            return Optional.of(new Availability(Availability.UNAVAILABLE));
        }
        throw new ParseException(MESSAGE_INVALID_AVAILABILITY);
    }

    private Optional<String> parseConsent(String value) throws ParseException {
        if (value == null) {
            return Optional.empty();
        }
        if (value.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_CONSENT);
        }
        String normalized = value.toLowerCase();
        if (!normalized.equals("received") && !normalized.equals("pending")) {
            throw new ParseException(MESSAGE_INVALID_CONSENT);
        }
        return Optional.of(normalized);
    }

    private Optional<String> parseInjury(String value) throws ParseException {
        if (value == null) {
            return Optional.empty();
        }
        if (value.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_INJURY);
        }
        String normalized = value.toLowerCase();
        if (!normalized.equals("fit") && !normalized.equals("injured")) {
            throw new ParseException(MESSAGE_INVALID_INJURY);
        }
        return Optional.of(normalized);
    }

    private Optional<String> parseFee(String value) throws ParseException {
        if (value == null) {
            return Optional.empty();
        }
        if (value.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_FEE);
        }
        String normalized = value.toLowerCase();
        if (!normalized.equals("paid") && !normalized.equals("unpaid")) {
            throw new ParseException(MESSAGE_INVALID_FEE);
        }
        return Optional.of(normalized);
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

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    private record PrefixMatch(String value, int start, int end) {}
}
