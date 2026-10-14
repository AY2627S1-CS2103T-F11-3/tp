package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_validArgs_returnsListCommand() {
        // Empty string
        assertParseSuccess(parser, "", new ListCommand());

        // Leading/trailing whitespace only
        assertParseSuccess(parser, " ", new ListCommand());
        assertParseSuccess(parser, "\t  \n", new ListCommand());
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Words, numbers, symbols
        assertParseFailure(parser, "abc", ListCommandParser.MESSAGE_INVALID_LIST_COMMAND);
        assertParseFailure(parser, " 123 ", ListCommandParser.MESSAGE_INVALID_LIST_COMMAND);
        assertParseFailure(parser, " /sq", ListCommandParser.MESSAGE_INVALID_LIST_COMMAND);
    }
}
