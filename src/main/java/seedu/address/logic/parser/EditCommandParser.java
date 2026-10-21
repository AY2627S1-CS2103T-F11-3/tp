package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANCONTACT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANCONTACT_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANNAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Availability;
import seedu.address.model.person.GuardianContact;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.Position;
import seedu.address.model.person.SquadName;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    public static final String MESSAGE_INVALID_AVAILABILITY =
            "Invalid availability entered! Availability should either be true or false!";

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_AVAILABILITY,
                        PREFIX_AVAILABILITY_FULL, PREFIX_SQUADNAME, PREFIX_SQUADNAME_FULL,
                        PREFIX_POSITION, PREFIX_POSITION_FULL, PREFIX_GUARDIANNAME, PREFIX_GUARDIANNAME_FULL,
                        PREFIX_GUARDIANCONTACT, PREFIX_GUARDIANCONTACT_FULL, PREFIX_INJURY_FULL, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_AVAILABILITY, PREFIX_AVAILABILITY_FULL,
                PREFIX_SQUADNAME, PREFIX_SQUADNAME_FULL, PREFIX_POSITION, PREFIX_POSITION_FULL,
                PREFIX_GUARDIANNAME, PREFIX_GUARDIANNAME_FULL, PREFIX_GUARDIANCONTACT, PREFIX_GUARDIANCONTACT_FULL,
                PREFIX_INJURY_FULL);
        verifyAliasesUsedOnce(argMultimap, PREFIX_AVAILABILITY, PREFIX_AVAILABILITY_FULL);
        verifyAliasesUsedOnce(argMultimap, PREFIX_SQUADNAME, PREFIX_SQUADNAME_FULL);
        verifyAliasesUsedOnce(argMultimap, PREFIX_POSITION, PREFIX_POSITION_FULL);
        verifyAliasesUsedOnce(argMultimap, PREFIX_GUARDIANNAME, PREFIX_GUARDIANNAME_FULL);
        verifyAliasesUsedOnce(argMultimap, PREFIX_GUARDIANCONTACT, PREFIX_GUARDIANCONTACT_FULL);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        parseSquadName(argMultimap).ifPresent(editPersonDescriptor::setSquadName);
        parsePosition(argMultimap).ifPresent(editPersonDescriptor::setPosition);
        parseGuardianName(argMultimap, editPersonDescriptor);
        parseGuardianContact(argMultimap, editPersonDescriptor);
        parseAvailability(argMultimap).ifPresent(editPersonDescriptor::setAvailability);
        parseValue(argMultimap.getValue(PREFIX_INJURY_FULL), InjuryStatus::new)
                .ifPresent(editPersonDescriptor::setInjuryStatus);
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    private void verifyAliasesUsedOnce(ArgumentMultimap argMultimap, Prefix shortPrefix, Prefix fullPrefix)
            throws ParseException {
        if (argMultimap.getValue(shortPrefix).isPresent() && argMultimap.getValue(fullPrefix).isPresent()) {
            throw new ParseException(Messages.getErrorMessageForDuplicatePrefixes(shortPrefix, fullPrefix));
        }
    }

    private Optional<String> getValue(ArgumentMultimap argMultimap, Prefix shortPrefix, Prefix fullPrefix) {
        return argMultimap.getValue(shortPrefix).or(() -> argMultimap.getValue(fullPrefix));
    }

    private Optional<SquadName> parseSquadName(ArgumentMultimap argMultimap) throws ParseException {
        return parseValue(getValue(argMultimap, PREFIX_SQUADNAME, PREFIX_SQUADNAME_FULL), SquadName::new);
    }

    private Optional<Position> parsePosition(ArgumentMultimap argMultimap) throws ParseException {
        Optional<Position> position = parseValue(
                getValue(argMultimap, PREFIX_POSITION, PREFIX_POSITION_FULL), Position::new);
        if (position.isPresent() && position.get().isUnassigned()) {
            throw new ParseException(Position.MESSAGE_CONSTRAINTS);
        }
        return position;
    }

    private void parseGuardianName(ArgumentMultimap argMultimap, EditPersonDescriptor descriptor)
            throws ParseException {
        Optional<String> value = getValue(argMultimap, PREFIX_GUARDIANNAME, PREFIX_GUARDIANNAME_FULL);
        if (value.isPresent()) {
            descriptor.setGuardianName(value.get().isEmpty() ? null
                    : parseValue(value, GuardianName::new).orElseThrow());
        }
    }

    private void parseGuardianContact(ArgumentMultimap argMultimap, EditPersonDescriptor descriptor)
            throws ParseException {
        Optional<String> value = getValue(argMultimap, PREFIX_GUARDIANCONTACT, PREFIX_GUARDIANCONTACT_FULL);
        if (value.isPresent()) {
            descriptor.setGuardianContact(value.get().isEmpty() ? null
                    : parseValue(value, GuardianContact::new).orElseThrow());
        }
    }

    private <T> Optional<T> parseValue(Optional<String> value, java.util.function.Function<String, T> constructor)
            throws ParseException {
        if (value.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(constructor.apply(value.get()));
        } catch (IllegalArgumentException exception) {
            throw new ParseException(exception.getMessage(), exception);
        }
    }

    private Optional<Availability> parseAvailability(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> availability = getValue(argMultimap, PREFIX_AVAILABILITY, PREFIX_AVAILABILITY_FULL);
        if (availability.isEmpty()) {
            return Optional.empty();
        }

        String normalizedAvailability = availability.get().replaceAll("\\s", "");
        if ("true".equalsIgnoreCase(normalizedAvailability)) {
            return Optional.of(new Availability(Availability.AVAILABLE));
        }
        if ("false".equalsIgnoreCase(normalizedAvailability)) {
            return Optional.of(new Availability(Availability.UNAVAILABLE));
        }
        throw new ParseException(MESSAGE_INVALID_AVAILABILITY);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contains only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
