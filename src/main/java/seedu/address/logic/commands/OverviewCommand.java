package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.TreeMap;

import seedu.address.model.Model;
import seedu.address.model.person.Availability;
import seedu.address.model.person.Person;

/**
 * Summarises squad sizes and player availability across the entire club.
 */
public class OverviewCommand extends Command {
    public static final String COMMAND_WORD = "overview";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows all squads and their player availability.\n"
            + "Parameters: none\nExample: " + COMMAND_WORD;
    public static final String MESSAGE_EMPTY = "Club overview: 0 squads, 0 players.\n"
            + "No players registered. Add players to see squad availability.";

    @Override
    public boolean isAddressBookMutating() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        Map<String, SquadCounts> squads = new TreeMap<>();
        int totalPlayers = 0;
        int availablePlayers = 0;
        for (Person player : model.getAddressBook().getPersonList()) {
            SquadCounts counts = squads.computeIfAbsent(player.getSquadName().value, unused -> new SquadCounts());
            counts.total++;
            totalPlayers++;
            if (Availability.AVAILABLE.equals(player.getAvailability().value)) {
                counts.available++;
                availablePlayers++;
            }
        }

        if (totalPlayers == 0) {
            return new CommandResult(MESSAGE_EMPTY);
        }

        StringBuilder overview = new StringBuilder(String.format(
                "Club overview: %d %s, %d %s.\nAvailable: %d | Unavailable: %d\n\nSquads:",
                squads.size(), squads.size() == 1 ? "squad" : "squads",
                totalPlayers, totalPlayers == 1 ? "player" : "players",
                availablePlayers, totalPlayers - availablePlayers));
        squads.forEach((name, counts) -> {
            overview.append(String.format("\n%s: %d total | %d available | %d unavailable",
                    name, counts.total, counts.available, counts.total - counts.available));
            if (counts.available == 0) {
                overview.append(" (No players available)");
            }
        });
        return new CommandResult(overview.toString());
    }

    /**
     * Counts registered and available players in one squad during an overview calculation.
     */
    private static class SquadCounts {
        private int total;
        private int available;
    }
}
