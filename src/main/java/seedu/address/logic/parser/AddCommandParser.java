package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANCONTACT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANCONTACT_FULL;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/** Parses the add-player arguments, validating supplied fields from left to right. */
public class AddCommandParser implements Parser<AddCommand> {
    public static final String MESSAGE_MISSING_NAME = "Error! Name must be specified!";
    public static final String MESSAGE_MISSING_SQUAD = "Error! Squad name must be specified!";
    public static final String MESSAGE_MISSING_POSITION = "Error! Position must be specified!";
    public static final String MESSAGE_INVALID_AVAILABILITY =
            "Invalid availability entered! Availability should either be true or false!";

    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?<!\\S)(?<prefix>[^\\s/]+/|/\\S+)");

    private enum Field {
        NAME, SQUAD, POSITION, GUARDIAN, CONTACT, AVAILABILITY, TAG
    }

    @Override
    public AddCommand parse(String args) throws ParseException {
        List<Argument> arguments = tokenize(args);
        requireField(arguments, Field.NAME, MESSAGE_MISSING_NAME);
        requireField(arguments, Field.SQUAD, MESSAGE_MISSING_SQUAD);
        requireField(arguments, Field.POSITION, MESSAGE_MISSING_POSITION);

        Name name = null;
        SquadName squad = null;
        Position position = null;
        GuardianName guardian = null;
        GuardianContact contact = null;
        Availability availability = new Availability(Availability.AVAILABLE);
        Set<Tag> tags = new HashSet<>();
        Set<Field> seen = new HashSet<>();
        for (Argument argument : arguments) {
            Field field = fieldFor(argument.prefix());
            if (field == null) {
                throw invalidFormat();
            }
            if (field != Field.TAG && !seen.add(field)) {
                throw new ParseException(Messages.getErrorMessageForDuplicatePrefixes(aliasesFor(field)));
            }
            String value = argument.value();
            try {
                switch (field) {
                    case NAME:
                        name = ParserUtil.parseName(value);
                        break;
                    case SQUAD:
                        squad = new SquadName(value);
                        break;
                    case POSITION:
                        position = new Position(value);
                        if (position.isUnassigned()) {
                            throw new ParseException(Position.MESSAGE_CONSTRAINTS);
                        }
                        break;
                    case GUARDIAN:
                        guardian = new GuardianName(value);
                        break;
                    case CONTACT:
                        contact = new GuardianContact(value);
                        break;
                    case AVAILABILITY:
                        availability = parseAvailability(value);
                        break;
                    case TAG:
                        tags.add(ParserUtil.parseTag(value));
                        break;
                    default:
                        throw new AssertionError("Unknown add field");
                }
            } catch (IllegalArgumentException exception) {
                throw new ParseException(exception.getMessage(), exception);
            }
        }
        return new AddCommand(new Person(name, tags, new Remark(""), squad, position, guardian, contact, availability));
    }

    private List<Argument> tokenize(String args) throws ParseException {
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        List<Argument> arguments = new ArrayList<>();
        String prefix = null;
        int valueStart = 0;
        while (matcher.find()) {
            if (prefix == null) {
                if (!args.substring(0, matcher.start()).isBlank()) {
                    throw invalidFormat();
                }
            } else {
                arguments.add(new Argument(prefix, args.substring(valueStart, matcher.start()).strip()));
            }
            prefix = matcher.group("prefix");
            valueStart = matcher.end();
        }
        if (prefix != null) {
            arguments.add(new Argument(prefix, args.substring(valueStart).strip()));
        } else if (!args.isBlank()) {
            throw invalidFormat();
        }
        return arguments;
    }

    private void requireField(List<Argument> arguments, Field field, String message) throws ParseException {
        if (arguments.stream().noneMatch(argument -> fieldFor(argument.prefix()) == field)) {
            throw new ParseException(message);
        }
    }

    private Field fieldFor(String prefix) {
        for (Field field : Field.values()) {
            for (Prefix alias : aliasesFor(field)) {
                if (alias.getPrefix().equals(prefix)) {
                    return field;
                }
            }
        }
        return null;
    }

    private Prefix[] aliasesFor(Field field) {
        return switch (field) {
            case NAME -> new Prefix[] {PREFIX_NAME, PREFIX_NAME_FULL};
            case SQUAD -> new Prefix[] {PREFIX_SQUADNAME, PREFIX_SQUADNAME_FULL};
            case POSITION -> new Prefix[] {PREFIX_POSITION, PREFIX_POSITION_FULL};
            case GUARDIAN -> new Prefix[] {PREFIX_GUARDIANNAME, PREFIX_GUARDIANNAME_FULL};
            case CONTACT -> new Prefix[] {PREFIX_GUARDIANCONTACT, PREFIX_GUARDIANCONTACT_FULL};
            case AVAILABILITY -> new Prefix[] {PREFIX_AVAILABILITY, PREFIX_AVAILABILITY_FULL};
            case TAG -> new Prefix[] {PREFIX_TAG};
        };
    }

    private Availability parseAvailability(String value) throws ParseException {
        String normalized = value.replaceAll("\\s", "");
        if ("true".equalsIgnoreCase(normalized)) {
            return new Availability(Availability.AVAILABLE);
        }
        if ("false".equalsIgnoreCase(normalized)) {
            return new Availability(Availability.UNAVAILABLE);
        }
        throw new ParseException(MESSAGE_INVALID_AVAILABILITY);
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    private record Argument(String prefix, String value) {}
}
