package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static seedu.address.logic.Messages.MESSAGE_INDEX_BELOW_ONE;
import static seedu.address.logic.Messages.MESSAGE_INDEX_TOO_LARGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_INDEX;
import static seedu.address.logic.Messages.MESSAGE_NON_NUMERIC_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.ConsentStatus;
import seedu.address.model.person.FeeStatus;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.PlayerStatusField;
import seedu.address.testutil.PersonBuilder;

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
        assertStatus("inj/INJURED", PlayerStatusField.INJURY, "injured");
        assertStatus("f/paid", PlayerStatusField.FEE, "paid");
        assertStatus("c/received", PlayerStatusField.CONSENT, "received");
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
                "1 av/true available/false", "1 fee/paid unknown/value",
                "1 inj/fit injury/injured", "1 f/paid f/unpaid", "1 c/pending fee/paid")) {
            assertParseFailure(parser, input, MarkCommandParser.MESSAGE_ONE_STATUS);
        }
    }

    @Test
    public void parse_invalidIndexes_showsSpecificCause() {
        assertParseFailure(parser, "fee/paid", MESSAGE_MISSING_INDEX);
        assertParseFailure(parser, "0 fee/paid", MESSAGE_INDEX_BELOW_ONE);
        assertDeferredFailure("1 -2 fee/paid", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "1,3 fee/paid", MESSAGE_NON_NUMERIC_INDEX);
        assertDeferredFailure("1 abc fee/paid", MESSAGE_NON_NUMERIC_INDEX);
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
        assertDeferredFailure("1 av/", MarkCommandParser.MESSAGE_INVALID_AVAILABILITY);
        assertDeferredFailure("1 av/available", MarkCommandParser.MESSAGE_INVALID_AVAILABILITY);
        assertDeferredFailure("1 av/t rue", MarkCommandParser.MESSAGE_INVALID_AVAILABILITY);
        for (String value : List.of("", "yes", "paid extra", "pa\u0131d")) {
            assertDeferredFailure("1 fee/" + value, FeeStatus.MESSAGE_CONSTRAINTS);
        }
        assertDeferredFailure("1 injury/", InjuryStatus.MESSAGE_CONSTRAINTS);
        assertDeferredFailure("1 injury/f\u0131t", InjuryStatus.MESSAGE_CONSTRAINTS);
        assertDeferredFailure("1 consent/", ConsentStatus.MESSAGE_CONSTRAINTS);
        assertDeferredFailure("1 consent/true", ConsentStatus.MESSAGE_CONSTRAINTS);
        assertDeferredFailure("1 consent/rece\u0131ved", ConsentStatus.MESSAGE_CONSTRAINTS);
        // Empty and invalid values represent the two failure partitions for each new alias.
        for (String value : List.of("", "unknown")) {
            assertDeferredFailure("1 inj/" + value, InjuryStatus.MESSAGE_CONSTRAINTS);
            assertDeferredFailure("1 f/" + value, FeeStatus.MESSAGE_CONSTRAINTS);
            assertDeferredFailure("1 c/" + value, ConsentStatus.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void execute_outOfBoundsBeforeInvalidIndex_reportsEarlierBoundsError() {
        for (String invalid : List.of("0", "-2", "abc", "999999999999")) {
            assertDeferredFailure("1 4 " + invalid + " av/true",
                    String.format(MarkCommand.MESSAGE_INVALID_INDEX, 4, 2));
        }
    }

    @Test
    public void execute_invalidIndexBeforeOutOfBounds_reportsEarlierSyntaxError() {
        assertDeferredFailure("1 0 4 av/true", MESSAGE_INDEX_BELOW_ONE);
        assertDeferredFailure("1 abc 4 av/true", MESSAGE_NON_NUMERIC_INDEX);
        assertDeferredFailure("1 999999999999 4 av/true", MESSAGE_INDEX_TOO_LARGE);
        assertParseFailure(parser, "0 4 av/true", MESSAGE_INDEX_BELOW_ONE);
    }

    @Test
    public void execute_outOfBoundsBeforeInvalidValue_reportsBoundsError() {
        assertDeferredFailure("1 4 fee/unknown", String.format(MarkCommand.MESSAGE_INVALID_INDEX, 4, 2));
        assertDeferredFailure("1 0 fee/unknown", MESSAGE_INDEX_BELOW_ONE);
    }

    @Test
    public void execute_filteredList_checksDisplayedBoundsBeforeLaterError() {
        Model model = createModel();
        model.updateFilteredPersonList(player -> player.getName().fullName.equals("Amy Tan"));

        MarkCommand command = assertDoesNotThrow(() -> parser.parse("1 2 0 fee/paid"));

        assertCommandFailure(command, model, String.format(MarkCommand.MESSAGE_INVALID_INDEX, 2, 1));
    }

    @Test
    public void execute_emptyListWithLaterError_showsRecoveryGuidance() {
        MarkCommand command = assertDoesNotThrow(() -> parser.parse("1 0 fee/paid"));
        assertCommandFailure(command, new ModelManager(), MarkCommand.MESSAGE_EMPTY_LIST);
    }

    private void assertDeferredFailure(String input, String expectedMessage) {
        MarkCommand command = assertDoesNotThrow(() -> parser.parse(input));
        assertCommandFailure(command, createModel(), expectedMessage);
    }

    private Model createModel() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Amy Tan").build());
        model.addPerson(new PersonBuilder().withName("Ben Lee").build());
        return model;
    }

    private void assertStatus(String argument, PlayerStatusField field, String value) {
        assertParseSuccess(parser, "1 3 " + argument, new MarkCommand(indexes, field, value));
    }
}
