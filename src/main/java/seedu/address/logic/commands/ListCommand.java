package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_EMPTY_LIST = "No players found!";

    @Override
    public boolean isAddressBookMutating() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (lastShownList.isEmpty()) {
            return new CommandResult(MESSAGE_EMPTY_LIST);
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lastShownList.size(); i++) {
            builder.append(i + 1)
                    .append(". ")
                    .append(lastShownList.get(i).toListString());

            if (i < lastShownList.size() - 1) {
                builder.append("\n");
            }
        }

        return new CommandResult(builder.toString());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ListCommand); // instanceof handles nulls
    }
}
