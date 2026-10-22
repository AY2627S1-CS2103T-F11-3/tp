package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.SortField;

public class SortCommandParserTest {

    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_validArgs_returnsSortCommand() {
        assertParseSuccess(parser, "name", new SortCommand(SortField.NAME));
        assertParseSuccess(parser, "  SQUAD  ", new SortCommand(SortField.SQUAD));
        assertParseSuccess(parser, "position", new SortCommand(SortField.POSITION));
        assertParseSuccess(parser, "availability", new SortCommand(SortField.AVAILABILITY));
        assertParseSuccess(parser, "reset", new SortCommand(SortField.RESET));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "guardian", expectedMessage);
        assertParseFailure(parser, "name descending", expectedMessage);
    }
}
