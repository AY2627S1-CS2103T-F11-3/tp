package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_FULL;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes players identified by displayed indices or full names from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes players by displayed indices or full names.\n"
            + "Parameters: INDEX [INDEX]... or " + PREFIX_NAME + "NAME[, NAME]...\n"
            + "Use " + PREFIX_NAME_FULL + " as an alias for " + PREFIX_NAME + ".\n"
            + "Examples: " + COMMAND_WORD + " 1 3; " + COMMAND_WORD + " " + PREFIX_NAME + "John Doe, Amy Tan";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";
    public static final String MESSAGE_DELETE_PERSONS_SUCCESS = "Deleted %1$d players: %2$s";
    public static final String MESSAGE_NO_DISPLAYED_PLAYERS =
            "No players are currently displayed. Use list to show all players before deleting by index.";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE =
            "Index %1$d is outside the displayed list. Choose an index from 1 to %2$d.";
    public static final String MESSAGE_NAME_NOT_FOUND = "No player found with the full name: %1$s";
    public static final String MESSAGE_AMBIGUOUS_NAME =
            "Multiple players match the name: %1$s\n\n%2$s\n\n"
            + "No players deleted. Matching players are shown in the list.\n"
            + "Delete using index: delete INDEX [INDEX]... (e.g. delete 1).";

    private final List<Index> targetIndices;
    private final List<String> targetNames;

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
        this(targetIndices, List.of());
    }

    private DeleteCommand(List<Index> targetIndices, List<String> targetNames) {
        requireNonNull(targetIndices);
        requireNonNull(targetNames);
        checkArgument(targetIndices.isEmpty() != targetNames.isEmpty(), "Supply either indices or names.");
        checkArgument(targetNames.stream().allMatch(name -> !name.isBlank()), "Names must not be blank.");
        this.targetIndices = List.copyOf(targetIndices);
        this.targetNames = List.copyOf(targetNames);
    }

    /**
     * Creates a command to delete players by full name. Names are resolved against all registered players.
     */
    public static DeleteCommand forNames(List<String> names) {
        return new DeleteCommand(List.of(), names);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> personsToDelete = targetNames.isEmpty()
                ? resolveIndices(model.getFilteredPersonList())
                : resolveNames(model);
        personsToDelete.forEach(model::deletePerson);
        return new CommandResult(formatSuccess(personsToDelete));
    }

    /**
     * Resolves every index before any deletion can change the displayed list.
     */
    private List<Person> resolveIndices(List<Person> displayedPersons) throws CommandException {
        if (displayedPersons.isEmpty()) {
            throw new CommandException(MESSAGE_NO_DISPLAYED_PLAYERS);
        }

        Set<Person> personsToDelete = new LinkedHashSet<>();
        for (Index index : targetIndices) {
            if (index.getZeroBased() >= displayedPersons.size()) {
                throw new CommandException(String.format(MESSAGE_INDEX_OUT_OF_RANGE,
                        index.getOneBased(), displayedPersons.size()));
            }
            personsToDelete.add(displayedPersons.get(index.getZeroBased()));
        }
        return List.copyOf(personsToDelete);
    }

    private List<Person> resolveNames(Model model) throws CommandException {
        Set<Person> personsToDelete = new LinkedHashSet<>();
        for (String name : targetNames) {
            personsToDelete.add(findUniquePlayer(name, model));
        }
        return List.copyOf(personsToDelete);
    }

    private Person findUniquePlayer(String name, Model model) throws CommandException {
        String normalizedName = normalizeName(name);
        List<Person> matches = model.getAddressBook().getPersonList().stream()
                .filter(person -> normalizeName(person.getName().fullName).equalsIgnoreCase(normalizedName))
                .toList();
        if (matches.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NAME_NOT_FOUND, name));
        }
        if (matches.size() > 1) {
            model.updateFilteredPersonList(matches::contains);
            throw new CommandException(formatAmbiguousName(name, matches));
        }
        return matches.getFirst();
    }

    /**
     * Formats matches in displayed order so their numbers can be used directly by the next delete command.
     */
    private String formatAmbiguousName(String name, List<Person> matches) {
        String details = IntStream.range(0, matches.size())
                .mapToObj(index -> (index + 1) + ". " + Messages.formatPlayerDetails(matches.get(index)))
                .collect(Collectors.joining("\n"));
        return String.format(MESSAGE_AMBIGUOUS_NAME, name, details);
    }

    private String normalizeName(String name) {
        return name.strip().replaceAll("\\s+", " ");
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

        return targetIndices.equals(otherDeleteCommand.targetIndices)
                && targetNames.equals(otherDeleteCommand.targetNames);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndices", targetIndices)
                .add("targetNames", targetNames)
                .toString();
    }
}
