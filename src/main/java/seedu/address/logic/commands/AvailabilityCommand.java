package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Availability;
import seedu.address.model.person.Person;

/**
 * Updates availability for players identified by indexes in the displayed list.
 */
public class AvailabilityCommand extends Command {

    public static final String COMMAND_WORD = "availability";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sets availability for players in the displayed list.\n"
            + "Parameters: INDEX [INDEX]... available/true|false\n"
            + "Alias: av/ for available/\n"
            + "Example: " + COMMAND_WORD + " 1 3 av/false";
    public static final String MESSAGE_NO_DISPLAYED_PLAYERS =
            "No players are currently displayed. Use list to show all players before updating availability.";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE =
            "Index %1$d is outside the displayed list. Choose an index from 1 to %2$d. No players updated.";

    private final List<Index> targetIndices;
    private final Availability availability;

    /**
     * Creates a command to set availability for one or more displayed players.
     * Repeated indexes identify the same player only once.
     */
    public AvailabilityCommand(List<Index> targetIndices, Availability availability) {
        requireNonNull(targetIndices);
        checkArgument(!targetIndices.isEmpty(), "Supply at least one player index.");
        this.targetIndices = List.copyOf(targetIndices);
        this.availability = requireNonNull(availability);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> targets = resolveTargets(model.getFilteredPersonList());
        List<Person> changed = targets.stream()
                .filter(player -> !player.getAvailability().equals(availability)).toList();
        List<Person> unchanged = targets.stream()
                .filter(player -> player.getAvailability().equals(availability)).toList();
        for (Person target : changed) {
            model.setPerson(target, withAvailability(target));
        }
        return new CommandResult(formatFeedback(changed, unchanged));
    }

    private String formatFeedback(List<Person> changed, List<Person> unchanged) {
        if (changed.isEmpty()) {
            String selection = unchanged.size() == 1 ? "The selected player is"
                    : String.format("All %d selected players are", unchanged.size());
            return String.format("No changes needed. %s already %s.\n%s",
                    selection, availability, formatNames(unchanged));
        }
        String message = String.format("Marked %d %s as %s:\n%s", changed.size(),
                changed.size() == 1 ? "player" : "players", availability, formatNames(changed));
        if (!unchanged.isEmpty()) {
            message += String.format("\n\nAlready %s (unchanged, %d %s):\n%s", availability,
                    unchanged.size(), unchanged.size() == 1 ? "player" : "players", formatNames(unchanged));
        }
        return message;
    }

    private String formatNames(List<Person> players) {
        return players.stream().map(player -> player.getName().fullName).collect(Collectors.joining("\n"));
    }

    /**
     * Resolves and validates every target before updates can change the filtered list.
     */
    private List<Person> resolveTargets(List<Person> displayedPlayers) throws CommandException {
        if (displayedPlayers.isEmpty()) {
            throw new CommandException(MESSAGE_NO_DISPLAYED_PLAYERS);
        }
        Set<Person> targets = new LinkedHashSet<>();
        for (Index index : targetIndices) {
            if (index.getZeroBased() >= displayedPlayers.size()) {
                throw new CommandException(String.format(MESSAGE_INDEX_OUT_OF_RANGE,
                        index.getOneBased(), displayedPlayers.size()));
            }
            targets.add(displayedPlayers.get(index.getZeroBased()));
        }
        return List.copyOf(targets);
    }

    private Person withAvailability(Person player) {
        return new Person(player.getName(), player.getTags(), player.getRemark(), player.getSquadName(),
                player.getPosition(), player.getGuardianName(), player.getGuardianContact(), availability);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof AvailabilityCommand otherCommand
                && targetIndices.equals(otherCommand.targetIndices)
                && availability.equals(otherCommand.availability));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndices", targetIndices)
                .add("availability", availability)
                .toString();
    }
}
