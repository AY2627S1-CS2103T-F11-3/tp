package seedu.address.model.overview;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Immutable player counts for one squad.
 */
public record SquadSummary(String squadName, int totalPlayers, int availablePlayers) {
    /**
     * Creates a summary with valid, non-empty squad counts.
     */
    public SquadSummary {
        requireNonNull(squadName);
        checkArgument(totalPlayers > 0 && availablePlayers >= 0 && availablePlayers <= totalPlayers,
                "Squad counts must satisfy 0 <= available <= total, with total > 0");
    }

    public int getUnavailablePlayers() {
        return totalPlayers - availablePlayers;
    }
}
