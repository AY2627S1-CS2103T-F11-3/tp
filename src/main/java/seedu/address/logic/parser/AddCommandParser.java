package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANNAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Availability;
import seedu.address.model.person.GuardianContact;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Position;
import seedu.address.model.person.Remark;
import seedu.address.model.person.SquadName;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_NAME_FULL, PREFIX_TAG, PREFIX_SQUADNAME,
                        PREFIX_SQUADNAME_FULL, PREFIX_POSITION, PREFIX_POSITION_FULL, PREFIX_GUARDIANNAME,
                        PREFIX_GUARDIANNAME_FULL);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_NAME_FULL);
        List<String> nameValues = new ArrayList<>(argMultimap.getAllValues(PREFIX_NAME));
        nameValues.addAll(argMultimap.getAllValues(PREFIX_NAME_FULL));

        if (nameValues.size() > 1) {
            throw new ParseException(
                    Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_NAME_FULL));
        }

        if (nameValues.isEmpty()
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        Name name = ParserUtil.parseName(nameValues.getFirst());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Remark remark = new Remark(""); // add command does not allow adding remarks straight away
        SquadName squadName = new SquadName("Unassigned");
        Position position = new Position("Unassigned");
        GuardianName guardianName = new GuardianName("Not provided");
        GuardianContact guardianContact = new GuardianContact("000");
        Availability availability = new Availability(Availability.AVAILABLE);

        Person person = new Person(name, tagList, remark, squadName, position, guardianName,
                guardianContact, availability);

        return new AddCommand(person);
    }


}
