package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONSENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONSENT_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY_FULL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Availability;
import seedu.address.model.person.ConsentStatus;
import seedu.address.model.person.FeeStatus;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.PlayerStatusField;

/** Parses displayed indexes followed by exactly one player status and value. */
public class MarkCommandParser implements Parser<MarkCommand> {
    public static final String MESSAGE_ONE_STATUS = "Specify exactly one status per mark command.\n"
            + MarkCommand.MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_AVAILABILITY =
            "Availability must be true or false. Use av/true or av/false.";
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?<!\\S)(\\S*/)");
    private static final Map<String, PlayerStatusField> STATUS_PREFIXES = Map.of(
            PREFIX_AVAILABILITY.getPrefix(), PlayerStatusField.AVAILABILITY,
            PREFIX_AVAILABILITY_FULL.getPrefix(), PlayerStatusField.AVAILABILITY,
            PREFIX_INJURY.getPrefix(), PlayerStatusField.INJURY,
            PREFIX_INJURY_FULL.getPrefix(), PlayerStatusField.INJURY,
            PREFIX_FEE.getPrefix(), PlayerStatusField.FEE,
            PREFIX_FEE_FULL.getPrefix(), PlayerStatusField.FEE,
            PREFIX_CONSENT.getPrefix(), PlayerStatusField.CONSENT,
            PREFIX_CONSENT_FULL.getPrefix(), PlayerStatusField.CONSENT);

    @Override
    public MarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        if (!matcher.find()) {
            throw new ParseException(MESSAGE_ONE_STATUS);
        }
        String prefix = matcher.group();
        PlayerStatusField field = STATUS_PREFIXES.get(prefix);
        if (field == null) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
        }
        String indexText = args.substring(0, matcher.start()).strip();
        String value = args.substring(matcher.end()).strip();
        if (matcher.find()) {
            throw new ParseException(MESSAGE_ONE_STATUS);
        }
        return parseTargetsAndValue(indexText, field, value);
    }

    private MarkCommand parseTargetsAndValue(String text, PlayerStatusField field, String value) throws ParseException {
        List<Index> indexes = new ArrayList<>();
        try {
            for (String index : text.split("\\s+")) {
                indexes.add(ParserUtil.parsePlayerIndex(index));
            }
            return new MarkCommand(indexes, field, parseValue(field, value));
        } catch (ParseException exception) {
            if (indexes.isEmpty()) {
                throw exception;
            }
            // Bounds checks need the model, so earlier indexes must be resolved during execution first.
            return MarkCommand.withDeferredError(indexes, field, exception.getMessage());
        }
    }

    private String parseValue(PlayerStatusField field, String value) throws ParseException {
        try {
            return switch (field) {
                case AVAILABILITY -> parseAvailability(value);
                case INJURY -> new InjuryStatus(value).value;
                case FEE -> new FeeStatus(value).value;
                case CONSENT -> new ConsentStatus(value).value;
            };
        } catch (IllegalArgumentException exception) {
            throw new ParseException(exception.getMessage(), exception);
        }
    }

    private String parseAvailability(String value) throws ParseException {
        if (value.equalsIgnoreCase("true")) {
            return Availability.AVAILABLE;
        }
        if (value.equalsIgnoreCase("false")) {
            return Availability.UNAVAILABLE;
        }
        throw new ParseException(MESSAGE_INVALID_AVAILABILITY);
    }
}
