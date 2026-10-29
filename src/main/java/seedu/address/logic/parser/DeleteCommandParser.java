package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_EMPTY_NAME;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_INDEX;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_FULL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteCommand object.
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public DeleteCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + trimmedArgs, PREFIX_NAME, PREFIX_NAME_FULL);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_NAME_FULL);
        if (argMultimap.getValue(PREFIX_NAME).isPresent() && argMultimap.getValue(PREFIX_NAME_FULL).isPresent()) {
            throw new ParseException(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_NAME_FULL));
        }
        Optional<String> nameValue = argMultimap.getValue(PREFIX_NAME).or(() -> argMultimap.getValue(PREFIX_NAME_FULL));
        if (nameValue.isPresent()) {
            if (!argMultimap.getPreamble().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
            }
            return DeleteCommand.forNames(parseNames(nameValue.get()));
        }
        return parseIndexCommand(trimmedArgs);
    }

    private List<String> parseNames(String args) throws ParseException {
        if (args.isBlank()) {
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

    private DeleteCommand parseIndexCommand(String args) throws ParseException {
        if (args.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_INDEX);
        }
        List<Index> indices = new ArrayList<>();
        List<String> invalidArguments = new ArrayList<>();
        for (String argument : args.split("\\s+")) {
            try {
                indices.add(ParserUtil.parsePlayerIndex(argument));
            } catch (ParseException exception) {
                invalidArguments.add(argument);
            }
        }
        return invalidArguments.isEmpty() ? new DeleteCommand(indices)
                : DeleteCommand.forInvalidIndices(invalidArguments);
    }

}
