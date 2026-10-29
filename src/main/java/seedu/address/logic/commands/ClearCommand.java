package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Clears all player profiles.\n"
            + "Parameters: none\n"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";


    @Override
    public CommandResult execute(Model model) {
        assert model != null : "Model must not be null when executing ClearCommand";
        requireNonNull(model);
        model.setAddressBook(new AddressBook());
        assert model.getAddressBook().getPersonList().isEmpty() : "Address book must be empty after clear";
        model.commitAddressBook();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
