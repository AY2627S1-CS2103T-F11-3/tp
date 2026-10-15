package seedu.address.model.overview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests immutable overview snapshots and their derived totals.
 */
public class ClubOverviewTest {
    @Test
    public void fromPlayers_mixedSquads_sortsAndCounts() {
        List<Person> players = List.of(new PersonBuilder().withName("Bob").withSquadName("Beta")
                .withAvailability("unavailable").build(),
                new PersonBuilder().withName("Alice").withSquadName("Alpha").build());
        ClubOverview overview = ClubOverview.fromPlayers(players);
        assertEquals(List.of(new SquadSummary("Alpha", 1, 1), new SquadSummary("Beta", 1, 0)), overview.squads());
        assertEquals(2, overview.getTotalPlayers());
        assertEquals(1, overview.getAvailablePlayers());
        assertEquals(1, overview.getUnavailablePlayers());
    }

    @Test
    public void constructor_mutableList_takesImmutableSnapshot() {
        List<SquadSummary> squads = new ArrayList<>(List.of(new SquadSummary("Alpha", 2, 1)));
        ClubOverview overview = new ClubOverview(squads);
        squads.clear();
        assertEquals(2, overview.getTotalPlayers());
        assertThrows(UnsupportedOperationException.class, () -> overview.squads().clear());
    }

    @Test
    public void constructor_invalidCounts_rejectsSummary() {
        assertThrows(IllegalArgumentException.class, () -> new SquadSummary("Alpha", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new SquadSummary("Alpha", 1, -1));
        assertThrows(IllegalArgumentException.class, () -> new SquadSummary("Alpha", 1, 2));
        assertThrows(NullPointerException.class, () -> new SquadSummary(null, 1, 0));
    }

    @Test
    public void fromPlayers_emptyList_hasZeroTotals() {
        ClubOverview overview = ClubOverview.fromPlayers(List.of());
        assertEquals(List.of(), overview.squads());
        assertEquals(0, overview.getTotalPlayers());
        assertEquals(0, overview.getAvailablePlayers());
        assertEquals(0, overview.getUnavailablePlayers());
    }
}
