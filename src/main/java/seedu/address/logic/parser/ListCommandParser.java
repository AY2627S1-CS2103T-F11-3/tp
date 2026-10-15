package seedu.address.logic.parser;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    public static final String MESSAGE_INVALID_LIST_COMMAND =
            "Invalid list command! The list command does not accept additional arguments!";

    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns a ListCommand object for execution.
     *
     * @throws ParseException if the user input contains any non-whitespace characters.
     */
    @Override
    public ListCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (!trimmedArgs.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_LIST_COMMAND);
        }
        return new ListCommand();
    }
}
