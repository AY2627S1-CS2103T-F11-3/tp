package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INDEX_BELOW_ONE;
import static seedu.address.logic.Messages.MESSAGE_INDEX_TOO_LARGE;
import static seedu.address.logic.Messages.MESSAGE_MISSING_INDEX;
import static seedu.address.logic.Messages.MESSAGE_NON_NUMERIC_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.DeleteCommandParser.MESSAGE_EMPTY_NAME;
import static seedu.address.logic.parser.DeleteCommandParser.MESSAGE_MISSING_NAME;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;

/**
 * Tests delete argument validation and command creation.
 */
public class DeleteCommandParserTest {

    private final DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_surroundingWhitespace_returnsDeleteCommand() {
        assertParseSuccess(parser, " \t1\r\n ", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_leadingZeroes_returnsDeleteCommand() {
        assertParseSuccess(parser, "01", new DeleteCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "00000000000000000000001", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_maximumInteger_returnsDeleteCommand() {
        assertParseSuccess(parser, "2147483647", new DeleteCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_MISSING_INDEX);
        assertParseFailure(parser, " \t\r\n ", MESSAGE_MISSING_INDEX);
    }

    @Test
    public void parse_nonNumericIndex_throwsParseException() {
        assertParseFailure(parser, "abc", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "1a", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "1.5", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "1e2", MESSAGE_NON_NUMERIC_INDEX);
    }

    @Test
    public void parse_signedIndex_throwsParseException() {
        assertParseFailure(parser, "-1", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "+1", MESSAGE_NON_NUMERIC_INDEX);
    }

    @Test
    public void parse_zeroIndex_throwsParseException() {
        assertParseFailure(parser, "0", MESSAGE_INDEX_BELOW_ONE);
        assertParseFailure(parser, " 000 ", MESSAGE_INDEX_BELOW_ONE);
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        assertParseFailure(parser, "1 abc", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "1 /squad Team", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "1,2", MESSAGE_NON_NUMERIC_INDEX);
    }

    @Test
    public void parse_multipleIndices_returnsDeleteCommand() {
        DeleteCommand expectedCommand = new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
        assertParseSuccess(parser, "1 2", expectedCommand);
        assertParseSuccess(parser, " 01   02 ", expectedCommand);
        assertParseSuccess(parser, "1\t2", expectedCommand);
        assertParseSuccess(parser, "1\n2", expectedCommand);
    }

    @Test
    public void parse_repeatedIndices_returnsDeleteCommand() {
        assertParseSuccess(parser, "1 01", new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON)));
    }

    @Test
    public void parse_multipleInvalidIndices_reportsFirstError() {
        assertParseFailure(parser, "0 abc", MESSAGE_INDEX_BELOW_ONE);
        assertParseFailure(parser, "abc 0", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "1 0 abc", MESSAGE_INDEX_BELOW_ONE);
        assertParseFailure(parser, "1 2147483648 abc", MESSAGE_INDEX_TOO_LARGE);
    }

    @Test
    public void parse_integerOverflow_throwsParseException() {
        assertParseFailure(parser, "2147483648", MESSAGE_INDEX_TOO_LARGE);
        assertParseFailure(parser, "999999999999999999999999999999", MESSAGE_INDEX_TOO_LARGE);
    }

    @Test
    public void parse_singleName_returnsDeleteCommand() {
        assertParseSuccess(parser, "/name John Doe", DeleteCommand.forNames(List.of("John Doe")));
    }

    @Test
    public void parse_multipleNames_returnsDeleteCommand() {
        assertParseSuccess(parser, " /NAME\t John Doe ,  Amy Tan  ",
                DeleteCommand.forNames(List.of("John Doe", "Amy Tan")));
    }

    @Test
    public void parse_nameWithSymbols_preservesName() {
        assertParseSuccess(parser, "/name Arun s/o Kumar, Anne-Marie O'Neill",
                DeleteCommand.forNames(List.of("Arun s/o Kumar", "Anne-Marie O'Neill")));
        assertParseSuccess(parser, "/name Player /name Example",
                DeleteCommand.forNames(List.of("Player /name Example")));
    }

    @Test
    public void parse_numericName_returnsNameSelector() {
        assertParseSuccess(parser, "/name 17", DeleteCommand.forNames(List.of("17")));
    }

    @Test
    public void parse_missingName_throwsParseException() {
        assertParseFailure(parser, "/name", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, "/name \t ", MESSAGE_MISSING_NAME);
    }

    @Test
    public void parse_emptyNameEntry_throwsParseException() {
        assertParseFailure(parser, "/name ,John Doe", MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, "/name John Doe,", MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, "/name John Doe, ,Amy Tan", MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, "/name ,", MESSAGE_EMPTY_NAME);
    }

    @Test
    public void parse_mixedIndicesAndNamePrefix_throwsParseException() {
        assertParseFailure(parser, "1 /name John Doe", MESSAGE_NON_NUMERIC_INDEX);
    }

    @Test
    public void parse_namePrefixWithoutBoundary_throwsParseException() {
        assertParseFailure(parser, "/names John Doe", MESSAGE_NON_NUMERIC_INDEX);
        assertParseFailure(parser, "/nameJohn Doe", MESSAGE_NON_NUMERIC_INDEX);
    }
}
