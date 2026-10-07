package seedu.address.logic.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteCommand object.
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    public static final String MESSAGE_MISSING_NAME = "Error! Specify at least one player name after /name!";
    public static final String MESSAGE_EMPTY_NAME = "Invalid name list! Specify a name between each comma!";

    private static final Pattern NAME_ARGUMENTS = Pattern.compile("/name(?:\\s+(.*))?",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public DeleteCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        Matcher nameMatcher = NAME_ARGUMENTS.matcher(trimmedArgs);
        if (nameMatcher.matches()) {
            return DeleteCommand.forNames(parseNames(nameMatcher.group(1)));
        }
        return new DeleteCommand(parseIndices(trimmedArgs));
    }

    private List<String> parseNames(String args) throws ParseException {
        if (args == null || args.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_NAME);
        }
        List<String> names = new ArrayList<>();
        for (String value : args.split(",", -1)) {
            String name = value.strip();
            if (name.isEmpty()) {
                throw new ParseException(MESSAGE_EMPTY_NAME);
            }
            names.add(name);
        }
        return names;
    }

    private List<Index> parseIndices(String args) throws ParseException {
        List<Index> indices = new ArrayList<>();
        for (String argument : args.split("\\s+")) {
            indices.add(ParserUtil.parsePlayerIndex(argument));
        }
        return indices;
    }

}
