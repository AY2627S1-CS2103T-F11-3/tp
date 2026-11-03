package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_FULL;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.DeleteCommandParser.MESSAGE_EMPTY_NAME;
import static seedu.address.logic.parser.DeleteCommandParser.MESSAGE_MISSING_NAME;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
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
    public void parse_nonNumericIndex_defersInvalidArguments() {
        assertParseSuccess(parser, "abc",
                DeleteCommand.forInvalidIndices(List.of("abc")));
        assertParseSuccess(parser, "1a",
                DeleteCommand.forInvalidIndices(List.of("1a")));
        assertParseSuccess(parser, "1.5",
                DeleteCommand.forInvalidIndices(List.of("1.5")));
        assertParseSuccess(parser, "1e2",
                DeleteCommand.forInvalidIndices(List.of("1e2")));
    }

    @Test
    public void parse_signedIndex_defersInvalidArguments() {
        assertParseSuccess(parser, "-1",
                DeleteCommand.forInvalidIndices(List.of("-1")));
        assertParseSuccess(parser, "+1",
                DeleteCommand.forInvalidIndices(List.of("+1")));
    }

    @Test
    public void parse_zeroIndex_defersInvalidArguments() {
        assertParseSuccess(parser, "0",
                DeleteCommand.forInvalidIndices(List.of("0")));
        assertParseSuccess(parser, " 000 ",
                DeleteCommand.forInvalidIndices(List.of("000")));
    }

    @Test
    public void parse_extraArguments_defersInvalidArguments() {
        assertParseSuccess(parser, "1 abc",
                DeleteCommand.forInvalidIndices(List.of("abc")));
        assertParseSuccess(parser, "1 /squad Team",
                DeleteCommand.forInvalidIndices(List.of("/squad", "Team")));
        assertParseSuccess(parser, "1,2",
                DeleteCommand.forInvalidIndices(List.of("1,2")));
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
    public void parse_multipleInvalidIndices_collectsInvalidArguments() {
        assertParseSuccess(parser, "0 abc",
                DeleteCommand.forInvalidIndices(List.of("0", "abc")));
        assertParseSuccess(parser, "abc 0",
                DeleteCommand.forInvalidIndices(List.of("abc", "0")));
        assertParseSuccess(parser, "1 0 abc",
                DeleteCommand.forInvalidIndices(List.of("0", "abc")));
        assertParseSuccess(parser, "1 2147483648 abc",
                DeleteCommand.forInvalidIndices(List.of("2147483648", "abc")));
    }

    @Test
    public void parse_invalidBatchArgument_collectsInvalidArguments() {
        assertParseSuccess(parser, "2 abc 4",
                DeleteCommand.forInvalidIndices(List.of("abc")));
        assertParseSuccess(parser, "2\tabc\t4",
                DeleteCommand.forInvalidIndices(List.of("abc")));
        assertParseSuccess(parser, "2 4 abc",
                DeleteCommand.forInvalidIndices(List.of("abc")));
        assertParseSuccess(parser, "2 abc def",
                DeleteCommand.forInvalidIndices(List.of("abc", "def")));
    }

    @Test
    public void parse_integerOverflow_defersInvalidArguments() {
        assertParseSuccess(parser, "2147483648",
                DeleteCommand.forInvalidIndices(List.of("2147483648")));
        assertParseSuccess(parser, "999999999999999999999999999999",
                DeleteCommand.forInvalidIndices(List.of("999999999999999999999999999999")));
    }

    @Test
    public void parse_singleName_returnsDeleteCommand() {
        assertParseSuccess(parser, "n/John Doe", DeleteCommand.forNames(List.of("John Doe")));
        assertParseSuccess(parser, "name/John Doe", DeleteCommand.forNames(List.of("John Doe")));
        assertParseSuccess(parser, " \tn/ John Doe ", DeleteCommand.forNames(List.of("John Doe")));
    }

    @Test
    public void parse_multipleNames_returnsDeleteCommand() {
        assertParseSuccess(parser, " name/\t John Doe ,  Amy Tan  ",
                DeleteCommand.forNames(List.of("John Doe", "Amy Tan")));
    }

    @Test
    public void parse_nameWithSymbols_preservesName() {
        assertParseSuccess(parser, "n/Arun s/o Kumar, Anne-Marie O'Neill",
                DeleteCommand.forNames(List.of("Arun s/o Kumar", "Anne-Marie O'Neill")));
        assertParseSuccess(parser, "n/Player /name Example",
                DeleteCommand.forNames(List.of("Player /name Example")));
    }

    @Test
    public void parse_numericName_returnsNameSelector() {
        assertParseSuccess(parser, "n/17", DeleteCommand.forNames(List.of("17")));
    }

    @Test
    public void parse_missingName_throwsParseException() {
        assertParseFailure(parser, "n/", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, "name/", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, "n/\t ", MESSAGE_MISSING_NAME);
    }

    @Test
    public void parse_emptyNameEntry_throwsParseException() {
        assertParseFailure(parser, "n/,John Doe", MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, "n/John Doe,", MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, "n/John Doe, ,Amy Tan", MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, "n/,", MESSAGE_EMPTY_NAME);
    }

    @Test
    public void parse_mixedIndicesAndNamePrefix_throwsParseException() {
        assertParseFailure(parser, "1 n/John Doe",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "1 name/John Doe",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_namePrefixWithoutBoundary_defersInvalidArguments() {
        assertParseSuccess(parser, "/names John Doe",
                DeleteCommand.forInvalidIndices(List.of("/names", "John", "Doe")));
        assertParseSuccess(parser, "/nameJohn Doe",
                DeleteCommand.forInvalidIndices(List.of("/nameJohn", "Doe")));
        assertParseSuccess(parser, "1n/John Doe",
                DeleteCommand.forInvalidIndices(List.of("1n/John", "Doe")));
    }

    @Test
    public void parse_repeatedNamePrefixes_throwsParseException() {
        assertParseFailure(parser, "n/John Doe n/Amy Tan",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, "name/John Doe name/Amy Tan",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME_FULL));
        assertParseFailure(parser, "n/John Doe name/Amy Tan",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_NAME_FULL));
        assertParseFailure(parser, "name/John Doe n/Amy Tan",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_NAME_FULL));
    }

    @Test
    public void parse_unsupportedNamePrefixes_defersInvalidArguments() {
        assertParseSuccess(parser, "/name John Doe",
                DeleteCommand.forInvalidIndices(List.of("/name", "John", "Doe")));
    }

    @Test
    public void parse_caseInsensitiveNamePrefix_returnsDeleteCommand() {
        assertParseSuccess(parser, "N/John Doe",
                DeleteCommand.forNames(List.of("John Doe")));
        assertParseSuccess(parser, "NAME/John Doe",
                DeleteCommand.forNames(List.of("John Doe")));
    }
}
