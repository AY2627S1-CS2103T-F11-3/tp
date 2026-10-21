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
    public static final String MESSAGE_NO_DISPLAYED_PLAYERS =
            "No players are currently displayed. Use list to show all players before updating availability.";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE =
            "Index %1$d is outside the displayed list. Choose an index from 1 to %2$d. No players updated.";
    public static final String MESSAGE_SUCCESS = "Availability set to %1$s for %2$d player(s):\n%3$s";

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
        for (Person target : targets) {
            model.setPerson(target, withAvailability(target));
        }
        String names = targets.stream().map(player -> player.getName().fullName)
                .collect(Collectors.joining("\n"));
        return new CommandResult(String.format(MESSAGE_SUCCESS, availability, targets.size(), names));
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
