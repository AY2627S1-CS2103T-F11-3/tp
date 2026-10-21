package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PlayerStatusField;

/**
 * Sets one status for players identified by indexes in the displayed list.
 */
public class MarkCommand extends Command {
    public static final String COMMAND_WORD = "mark";
    public static final String MESSAGE_EMPTY_LIST =
            "No players are currently displayed. Use list to show all players before marking a status.";
    public static final String MESSAGE_INVALID_INDEX =
            "Index %1$d is outside the displayed list. Choose an index from 1 to %2$d. No players updated.";

    private final List<Index> targetIndices;
    private final PlayerStatusField field;
    private final String value;

    /**
     * Creates a bulk status command using a domain value such as available, injured, paid or received.
     * Repeated indexes select a player only once. Status values are validated before any model updates.
     */
    public MarkCommand(List<Index> targetIndices, PlayerStatusField field, String value) {
        requireNonNull(targetIndices);
        checkArgument(!targetIndices.isEmpty(), "Supply at least one player index.");
        this.targetIndices = List.copyOf(targetIndices);
        this.field = requireNonNull(field);
        this.value = requireNonNull(value);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> targets = resolveTargets(model.getFilteredPersonList());
        List<Person> replacements = prepareReplacements(targets);
        List<Person> changed = new ArrayList<>();
        List<Person> unchanged = new ArrayList<>();
        for (int index = 0; index < targets.size(); index++) {
            Person target = targets.get(index);
            Person replacement = replacements.get(index);
            if (field.getValue(target).equals(field.getValue(replacement))) {
                unchanged.add(target);
            } else {
                model.setPerson(target, replacement);
                changed.add(target);
            }
        }
        return new CommandResult(formatFeedback(changed, unchanged, field.getValue(replacements.getFirst())));
    }

    /** Resolves all indexes before changes can shrink or reorder the displayed list. */
    private List<Person> resolveTargets(List<Person> displayedPlayers) throws CommandException {
        if (displayedPlayers.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }
        Set<Person> targets = new LinkedHashSet<>();
        for (Index index : targetIndices) {
            if (index.getZeroBased() >= displayedPlayers.size()) {
                throw new CommandException(String.format(MESSAGE_INVALID_INDEX,
                        index.getOneBased(), displayedPlayers.size()));
            }
            targets.add(displayedPlayers.get(index.getZeroBased()));
        }
        return List.copyOf(targets);
    }

    private List<Person> prepareReplacements(List<Person> targets) throws CommandException {
        try {
            return targets.stream().map(player -> field.withValue(player, value)).toList();
        } catch (IllegalArgumentException exception) {
            throw new CommandException(exception.getMessage(), exception);
        }
    }

    private String formatFeedback(List<Person> changed, List<Person> unchanged, String recordedValue) {
        assert !changed.isEmpty() || !unchanged.isEmpty() : "At least one validated player must be selected";
        String status = field.getDisplayName() + ": " + recordedValue;
        if (changed.isEmpty()) {
            return String.format("No changes needed. Already recorded %s (%d %s):\n%s", status,
                    unchanged.size(), unchanged.size() == 1 ? "player" : "players", formatNames(unchanged));
        }
        String message = String.format("Updated %s for %d %s:\n%s", status,
                changed.size(), changed.size() == 1 ? "player" : "players", formatNames(changed));
        if (!unchanged.isEmpty()) {
            message += String.format("\n\nAlready recorded %s (unchanged, %d %s):\n%s", status,
                    unchanged.size(), unchanged.size() == 1 ? "player" : "players", formatNames(unchanged));
        }
        return message;
    }

    private String formatNames(List<Person> players) {
        return players.stream().map(player -> player.getName().fullName).collect(Collectors.joining("\n"));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof MarkCommand otherCommand
                && targetIndices.equals(otherCommand.targetIndices)
                && field == otherCommand.field && value.equals(otherCommand.value));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndices", targetIndices)
                .add("field", field).add("value", value).toString();
    }
}
