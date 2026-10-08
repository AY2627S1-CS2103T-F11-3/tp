package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
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

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a player profile.\nParameters: "
            + PREFIX_NAME_FULL + "NAME " + PREFIX_SQUADNAME_FULL + "SQUAD " + PREFIX_POSITION_FULL + "POSITION "
            + "[" + PREFIX_GUARDIANNAME_FULL + "GUARDIAN] [" + PREFIX_GUARDIANCONTACT_FULL + "CONTACT] "
            + "[" + PREFIX_AVAILABILITY_FULL + "true/false] [" + PREFIX_TAG + "TAG]...\n"
            + "Aliases: " + PREFIX_NAME + ", " + PREFIX_SQUADNAME + ", " + PREFIX_POSITION + ", "
            + PREFIX_GUARDIANNAME + ", " + PREFIX_GUARDIANCONTACT + ", " + PREFIX_AVAILABILITY + ". "
            + "Specify each field only once, except tags.\n"
            + "Example: add name/John Doe squad/Soccer Stars position/Goalkeeper";

    public static final String MESSAGE_SUCCESS = "Player %1$s added!";
    public static final String MESSAGE_DUPLICATE_PERSON = "%1$s is already a registered player!";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_PERSON, toAdd.getName()));
        }

        model.addPerson(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
