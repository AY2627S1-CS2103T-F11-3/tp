package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INDEX_BELOW_ONE;
import static seedu.address.logic.Messages.MESSAGE_INDEX_TOO_LARGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_INDEX;
import static seedu.address.logic.Messages.MESSAGE_NON_NUMERIC_INDEX;
import static seedu.address.logic.parser.AvailabilityCommandParser.MESSAGE_INVALID_AVAILABILITY;
import static seedu.address.logic.parser.AvailabilityCommandParser.MESSAGE_MISSING_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AvailabilityCommand;
import seedu.address.model.person.Availability;

/**
 * Tests availability command syntax and actionable validation messages.
 */
public class AvailabilityCommandParserTest {
    private final AvailabilityCommandParser parser = new AvailabilityCommandParser();

    @Test
    public void parse_aliasesAndWhitespace_returnsCommand() {
        AvailabilityCommand expected = new AvailabilityCommand(
                List.of(Index.fromOneBased(1), Index.fromOneBased(3)),
                new Availability(Availability.UNAVAILABLE));
        assertParseSuccess(parser, "1 3 av/false", expected);
        assertParseSuccess(parser, " \t01   3\tavailable/ FALSE \n", expected);
    }

    @Test
    public void parse_singleAndRepeatedIndexes_returnsCommand() {
        assertParseSuccess(parser, "1 av/TRUE", new AvailabilityCommand(List.of(Index.fromOneBased(1)),
                new Availability(Availability.AVAILABLE)));
        assertParseSuccess(parser, "1 1 av/true", new AvailabilityCommand(
                List.of(Index.fromOneBased(1), Index.fromOneBased(1)), new Availability(Availability.AVAILABLE)));
    }

    @Test
    public void parse_missingAvailability_reportsRequiredValue() {
        for (String input : List.of("", "1 2", "1 av/", "1 available/ ")) {
            assertParseFailure(parser, input, MESSAGE_MISSING_AVAILABILITY);
        }
    }

    @Test
    public void parse_invalidAvailability_reportsAllowedValues() {
        for (String input : List.of("1 av/yes", "1 av/true extra", "1 av/t rue", "av/true 1")) {
            assertParseFailure(parser, input, MESSAGE_INVALID_AVAILABILITY);
        }
    }

    @Test
    public void parse_invalidIndexes_reportsSpecificCause() {
        assertParseFailure(parser, "av/true", MESSAGE_MISSING_INDEX);
        assertParseFailure(parser, "0 av/true", MESSAGE_INDEX_BELOW_ONE);
        assertParseFailure(parser, "1 -2 av/true", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "1 abc av/true", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "1,2 av/true", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "999999999999 av/true", MESSAGE_INDEX_TOO_LARGE);
    }

    @Test
    public void parse_duplicatePrefixes_rejectsAmbiguousValue() {
        assertParseFailure(parser, "1 av/true av/false",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_AVAILABILITY));
        assertParseFailure(parser, "1 available/true available/false",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_AVAILABILITY_FULL));
        assertParseFailure(parser, "1 av/true available/false",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_AVAILABILITY, PREFIX_AVAILABILITY_FULL));
    }

    @Test
    public void parse_unknownPrefixOrMissingSeparator_showsUsage() {
        for (String input : List.of("1 av/true squad/U12", "1 wrong/false", "1av/false", "1 /av false")) {
            assertParseFailure(parser, input,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AvailabilityCommand.MESSAGE_USAGE));
        }
    }
}
