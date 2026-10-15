package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.overview.ClubOverview;
import seedu.address.model.overview.SquadSummary;

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
        ClubOverview summary = ClubOverview.fromPlayers(model.getAddressBook().getPersonList());
        int totalPlayers = summary.getTotalPlayers();
        int availablePlayers = summary.getAvailablePlayers();
        if (totalPlayers == 0) {
            return new CommandResult(MESSAGE_EMPTY, summary);
        }

        StringBuilder overview = new StringBuilder(String.format(
                "Club overview: %d %s, %d %s.\nAvailable: %d | Unavailable: %d\n\nSquads:",
                summary.squads().size(), summary.squads().size() == 1 ? "squad" : "squads",
                totalPlayers, totalPlayers == 1 ? "player" : "players",
                availablePlayers, totalPlayers - availablePlayers));
        for (SquadSummary squad : summary.squads()) {
            overview.append(String.format("\n%s: %d total | %d available | %d unavailable",
                    squad.squadName(), squad.totalPlayers(), squad.availablePlayers(), squad.getUnavailablePlayers()));
            if (squad.availablePlayers() == 0) {
                overview.append(" (No players available)");
            }
        }
        return new CommandResult(overview.toString(), summary);
    }
}
