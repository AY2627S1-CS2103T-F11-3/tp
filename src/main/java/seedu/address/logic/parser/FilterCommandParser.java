package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONSENT_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANCONTACT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANCONTACT_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANNAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Availability;
import seedu.address.model.person.GuardianContact;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.MatchesFilterCriteriaPredicate;
import seedu.address.model.person.Position;
import seedu.address.model.person.SquadName;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a {@link FilterCommand}.
 */
public class FilterCommandParser implements Parser<FilterCommand> {
    public static final String MESSAGE_MISSING_CRITERION = "Error! At least one filter criterion must be specified!";

    public static final String MESSAGE_MISSING_AVAILABILITY = "Error! Availability must be specified!";
    public static final String MESSAGE_INVALID_AVAILABILITY =
            "Invalid availability entered! Availability should either be true or false!";
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
    public static final String MESSAGE_MISSING_REMARK = "Error! Remark keyword must be specified!";
    public static final String MESSAGE_DUPLICATE_POSITION = "Error! Parameter " + PREFIX_POSITION_FULL
            + " cannot be specified more than once!";
    public static final String MESSAGE_DUPLICATE_GUARDIAN = "Error! Parameter " + PREFIX_GUARDIANNAME_FULL
            + " cannot be specified more than once!";
    public static final String MESSAGE_DUPLICATE_CONTACT = "Error! Parameter " + PREFIX_GUARDIANCONTACT_FULL
            + " cannot be specified more than once!";
    public static final String MESSAGE_DUPLICATE_TAG = "Error! Parameter " + PREFIX_TAG
            + " cannot be specified more than once!";
    public static final String MESSAGE_DUPLICATE_REMARK = "Error! Parameter " + PREFIX_REMARK
            + " cannot be specified more than once!";


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
        String positionValue = null;
        String guardianValue = null;
        String contactValue = null;
        String tagValue = null;
        String remarkValue = null;
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
            } else if (isPositionPrefix(prefix.value())) {
                if (positionValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_POSITION);
                }
                positionValue = value;
            } else if (isGuardianPrefix(prefix.value())) {
                if (guardianValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_GUARDIAN);
                }
                guardianValue = value;
            } else if (isContactPrefix(prefix.value())) {
                if (contactValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_CONTACT);
                }
                contactValue = value;
            } else if (isTagPrefix(prefix.value())) {
                if (tagValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_TAG);
                }
                tagValue = value;
            } else if (isRemarkPrefix(prefix.value())) {
                if (remarkValue != null) {
                    throw new ParseException(MESSAGE_DUPLICATE_REMARK);
                }
                remarkValue = value;
            } else {
                throw invalidFormat();
            }
        }

        return new FilterCommand(new MatchesFilterCriteriaPredicate(
                parseSquad(squadValue),
                parseAvailability(availabilityValue),
                parseInjury(injuryValue),
                parseFee(feeValue),
                parseConsent(consentValue),
                parsePosition(positionValue),
                parseGuardianName(guardianValue),
                parseProfileValue(contactValue, GuardianContact::new),
                parseTag(tagValue),
                parseRemark(remarkValue)
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
                || isConsentPrefix(prefix)
                || isPositionPrefix(prefix)
                || isGuardianPrefix(prefix)
                || isContactPrefix(prefix)
                || isTagPrefix(prefix)
                || isRemarkPrefix(prefix);
    }

    private boolean isAvailabilityPrefix(String prefix) {
        return prefix.equals(PREFIX_AVAILABILITY.getPrefix()) || prefix.equals(PREFIX_AVAILABILITY_FULL.getPrefix());
    }

    private boolean isInjuryPrefix(String prefix) {
        return prefix.equals(PREFIX_INJURY_FULL.getPrefix());
    }

    private boolean isFeePrefix(String prefix) {
        return prefix.equals(PREFIX_FEE_FULL.getPrefix());
    }

    private boolean isConsentPrefix(String prefix) {
        return prefix.equals(PREFIX_CONSENT_FULL.getPrefix());
    }

    private boolean isSquadPrefix(String prefix) {
        return prefix.equals(PREFIX_SQUADNAME.getPrefix()) || prefix.equals(PREFIX_SQUADNAME_FULL.getPrefix());
    }

    private boolean isPositionPrefix(String prefix) {
        return prefix.equals(PREFIX_POSITION.getPrefix()) || prefix.equals(PREFIX_POSITION_FULL.getPrefix());
    }

    private boolean isGuardianPrefix(String prefix) {
        return prefix.equals(PREFIX_GUARDIANNAME.getPrefix()) || prefix.equals(PREFIX_GUARDIANNAME_FULL.getPrefix());
    }

    private boolean isContactPrefix(String prefix) {
        return prefix.equals(PREFIX_GUARDIANCONTACT.getPrefix())
                || prefix.equals(PREFIX_GUARDIANCONTACT_FULL.getPrefix());
    }

    private boolean isTagPrefix(String prefix) {
        return prefix.equals(PREFIX_TAG.getPrefix());
    }

    private boolean isRemarkPrefix(String prefix) {
        return prefix.equals(PREFIX_REMARK.getPrefix());
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

    private Optional<Tag> parseTag(String value) throws ParseException {
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(ParserUtil.parseTag(value));
        } catch (IllegalArgumentException exception) {
            throw new ParseException(exception.getMessage(), exception);
        }
    }

    private Optional<Position> parsePosition(String value) throws ParseException {
        Optional<Position> position = parseProfileValue(value, Position::new);
        if (position.isPresent() && position.get().isUnassigned()) {
            throw new ParseException(Position.MESSAGE_CONSTRAINTS);
        }
        return position;
    }

    private Optional<GuardianName> parseGuardianName(String value) throws ParseException {
        if ("unassigned".equalsIgnoreCase(value)) {
            throw new ParseException(GuardianName.MESSAGE_CONSTRAINTS);
        }
        return parseProfileValue(value, GuardianName::new);
    }

    private Optional<String> parseRemark(String value) throws ParseException {
        if (value == null) {
            return Optional.empty();
        }
        if (value.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_REMARK);
        }
        return Optional.of(value);
    }

    private <T> Optional<T> parseProfileValue(String value, java.util.function.Function<String, T> constructor)
            throws ParseException {
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(constructor.apply(value));
        } catch (IllegalArgumentException exception) {
            throw new ParseException(exception.getMessage(), exception);
        }
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    private record PrefixMatch(String value, int start, int end) {}
}
