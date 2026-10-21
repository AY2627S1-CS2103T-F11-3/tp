package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INDEX_BELOW_ONE;
import static seedu.address.logic.Messages.MESSAGE_INDEX_TOO_LARGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_INDEX;
import static seedu.address.logic.Messages.MESSAGE_NON_NUMERIC_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.model.person.ConsentStatus;
import seedu.address.model.person.FeeStatus;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.PlayerStatusField;

/** Tests status selection, domain validation and bulk index syntax. */
public class MarkCommandParserTest {
    private final MarkCommandParser parser = new MarkCommandParser();
    private final List<Index> indexes = List.of(Index.fromOneBased(1), Index.fromOneBased(3));

    @Test
    public void parse_eachStatusAndValue_returnsNormalizedCommand() {
        assertStatus("av/true", PlayerStatusField.AVAILABILITY, "available");
        assertStatus("available/FALSE", PlayerStatusField.AVAILABILITY, "unavailable");
        assertStatus("injury/FIT", PlayerStatusField.INJURY, "fit");
        assertStatus("injury/injured", PlayerStatusField.INJURY, "injured");
        assertStatus("fee/PAID", PlayerStatusField.FEE, "paid");
        assertStatus("fee/unpaid", PlayerStatusField.FEE, "unpaid");
        assertStatus("consent/RECEIVED", PlayerStatusField.CONSENT, "received");
        assertStatus("consent/pending", PlayerStatusField.CONSENT, "pending");
    }

    @Test
    public void parse_whitespaceAndLeadingZeroes_returnsCommand() {
        assertParseSuccess(parser, " \t01\t03\tfee/ PAID  ",
                new MarkCommand(indexes, PlayerStatusField.FEE, "paid"));
    }

    @Test
    public void parse_singleAndRepeatedIndexes_returnsCommand() {
        assertParseSuccess(parser, "1 fee/paid",
                new MarkCommand(List.of(Index.fromOneBased(1)), PlayerStatusField.FEE, "paid"));
        assertParseSuccess(parser, "1 1 fee/paid", new MarkCommand(
                List.of(Index.fromOneBased(1), Index.fromOneBased(1)), PlayerStatusField.FEE, "paid"));
    }

    @Test
    public void parse_missingOrMultipleStatuses_showsSingleStatusGuidance() {
        for (String input : List.of("", "1 3", "1 fee/paid consent/received", "1 fee/paid fee/paid",
                "1 av/true available/false", "1 fee/paid unknown/value")) {
            assertParseFailure(parser, input, MarkCommandParser.MESSAGE_ONE_STATUS);
        }
    }

    @Test
    public void parse_invalidIndexes_showsSpecificCause() {
        assertParseFailure(parser, "fee/paid", MESSAGE_MISSING_INDEX);
        assertParseFailure(parser, "0 fee/paid", MESSAGE_INDEX_BELOW_ONE);
        assertParseFailure(parser, "1 -2 fee/paid", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "1,3 fee/paid", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "1 abc fee/paid", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "999999999999 fee/paid", MESSAGE_INDEX_TOO_LARGE);
    }

    @Test
    public void parse_unknownPrefixOrMissingSeparator_showsUsage() {
        for (String input : List.of("1 paid/true", "1 FEE/paid", "1fee/paid", "1 /fee paid")) {
            assertParseFailure(parser, input,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_invalidOrMissingValues_usesDomainConstraints() {
        assertParseFailure(parser, "1 av/", MarkCommandParser.MESSAGE_INVALID_AVAILABILITY);
        assertParseFailure(parser, "1 av/available", MarkCommandParser.MESSAGE_INVALID_AVAILABILITY);
        assertParseFailure(parser, "1 av/t rue", MarkCommandParser.MESSAGE_INVALID_AVAILABILITY);
        for (String value : List.of("", "yes", "paid extra", "pa\u0131d")) {
            assertParseFailure(parser, "1 fee/" + value, FeeStatus.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, "1 injury/", InjuryStatus.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 injury/f\u0131t", InjuryStatus.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 consent/", ConsentStatus.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 consent/true", ConsentStatus.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 consent/rece\u0131ved", ConsentStatus.MESSAGE_CONSTRAINTS);
    }

    private void assertStatus(String argument, PlayerStatusField field, String value) {
        assertParseSuccess(parser, "1 3 " + argument, new MarkCommand(indexes, field, value));
    }
}
