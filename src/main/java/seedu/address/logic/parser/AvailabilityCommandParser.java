package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AvailabilityCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Availability;

/**
 * Parses displayed player indexes and a required availability value.
 */
public class AvailabilityCommandParser implements Parser<AvailabilityCommand> {

    public static final String MESSAGE_MISSING_AVAILABILITY =
            "Specify availability using av/true or av/false.";
    public static final String MESSAGE_INVALID_AVAILABILITY =
            "Invalid availability. Use av/true or av/false.";
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?<!\\S)(\\S*/)");

    @Override
    public AvailabilityCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String normalizedArgs = " " + args.strip().replaceAll("\\s+", " ");
        rejectUnknownPrefixes(normalizedArgs);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(normalizedArgs,
                PREFIX_AVAILABILITY, PREFIX_AVAILABILITY_FULL);
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_AVAILABILITY, PREFIX_AVAILABILITY_FULL);
        if (arguments.getValue(PREFIX_AVAILABILITY).isPresent()
                && arguments.getValue(PREFIX_AVAILABILITY_FULL).isPresent()) {
            throw new ParseException(Messages.getErrorMessageForDuplicatePrefixes(
                    PREFIX_AVAILABILITY, PREFIX_AVAILABILITY_FULL));
        }
        String value = arguments.getValue(PREFIX_AVAILABILITY)
                .or(() -> arguments.getValue(PREFIX_AVAILABILITY_FULL))
                .orElseThrow(() -> new ParseException(MESSAGE_MISSING_AVAILABILITY));
        Availability availability = parseAvailability(value);
        List<Index> indexes = new ArrayList<>();
        for (String index : arguments.getPreamble().split("\\s+")) {
            indexes.add(ParserUtil.parsePlayerIndex(index));
        }
        return new AvailabilityCommand(indexes, availability);
    }

    private void rejectUnknownPrefixes(String args) throws ParseException {
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        while (matcher.find()) {
            String prefix = matcher.group();
            if (!prefix.equals(PREFIX_AVAILABILITY.getPrefix())
                    && !prefix.equals(PREFIX_AVAILABILITY_FULL.getPrefix())) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        AvailabilityCommand.MESSAGE_USAGE));
            }
        }
    }

    private Availability parseAvailability(String value) throws ParseException {
        if (value.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_AVAILABILITY);
        }
        if (value.equalsIgnoreCase("true")) {
            return new Availability(Availability.AVAILABLE);
        }
        if (value.equalsIgnoreCase("false")) {
            return new Availability(Availability.UNAVAILABLE);
        }
        throw new ParseException(MESSAGE_INVALID_AVAILABILITY);
    }
}
