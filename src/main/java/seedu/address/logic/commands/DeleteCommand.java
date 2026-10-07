package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes players identified using their displayed indices from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes players identified by their indices in the displayed player list.\n"
            + "Parameters: INDEX [INDEX]... (each must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 3";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";
    public static final String MESSAGE_DELETE_PERSONS_SUCCESS = "Deleted %1$d players: %2$s";

    private final List<Index> targetIndices;

    /**
     * Creates a command to delete the player at {@code targetIndex}.
     */
    public DeleteCommand(Index targetIndex) {
        this(List.of(targetIndex));
    }

    /**
     * Creates a command to delete players at the supplied indices. Repeated indices delete a player only once.
     */
    public DeleteCommand(List<Index> targetIndices) {
        requireNonNull(targetIndices);
        checkArgument(!targetIndices.isEmpty(), "At least one index must be supplied.");
        this.targetIndices = List.copyOf(targetIndices);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> personsToDelete = resolveTargets(model.getFilteredPersonList());
        personsToDelete.forEach(model::deletePerson);
        return new CommandResult(formatSuccess(personsToDelete));
    }

    /**
     * Resolves every index before any deletion can change the displayed list.
     */
    private List<Person> resolveTargets(List<Person> displayedPersons) throws CommandException {
        Set<Person> personsToDelete = new LinkedHashSet<>();
        for (Index index : targetIndices) {
            if (index.getZeroBased() >= displayedPersons.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personsToDelete.add(displayedPersons.get(index.getZeroBased()));
        }
        return List.copyOf(personsToDelete);
    }

    private String formatSuccess(List<Person> deletedPersons) {
        if (deletedPersons.size() == 1) {
            return String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(deletedPersons.getFirst()));
        }
        String names = deletedPersons.stream().map(person -> person.getName().fullName)
                .collect(Collectors.joining(", "));
        return String.format(MESSAGE_DELETE_PERSONS_SUCCESS, deletedPersons.size(), names);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetIndices.equals(otherDeleteCommand.targetIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndices", targetIndices)
                .toString();
    }
}
