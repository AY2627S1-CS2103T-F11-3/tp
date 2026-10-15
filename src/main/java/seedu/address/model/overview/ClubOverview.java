package seedu.address.model.overview;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import seedu.address.model.person.Availability;
import seedu.address.model.person.Person;

/**
 * Immutable snapshot of all registered squads and their availability.
 */
public record ClubOverview(List<SquadSummary> squads) {
    /**
     * Copies the squad list so later mutations cannot change this snapshot.
     */
    public ClubOverview {
        squads = List.copyOf(squads);
    }

    /**
     * Summarises registered players in squad-name order without changing their list.
     */
    public static ClubOverview fromPlayers(Collection<Person> players) {
        Map<String, SquadSummary> summaries = new TreeMap<>();
        for (Person player : players) {
            String name = player.getSquadName().value;
            SquadSummary previous = summaries.get(name);
            int total = previous == null ? 0 : previous.totalPlayers();
            int available = previous == null ? 0 : previous.availablePlayers();
            if (Availability.AVAILABLE.equals(player.getAvailability().value)) {
                available++;
            }
            summaries.put(name, new SquadSummary(name, total + 1, available));
        }
        return new ClubOverview(List.copyOf(summaries.values()));
    }

    public int getTotalPlayers() {
        return squads.stream().mapToInt(SquadSummary::totalPlayers).sum();
    }

    public int getAvailablePlayers() {
        return squads.stream().mapToInt(SquadSummary::availablePlayers).sum();
    }

    public int getUnavailablePlayers() {
        return getTotalPlayers() - getAvailablePlayers();
    }
}
