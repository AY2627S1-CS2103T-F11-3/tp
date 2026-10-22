package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Identifies a player status that can be changed independently of other profile fields.
 */
public enum PlayerStatusField {
    AVAILABILITY("availability"),
    INJURY("injury status"),
    FEE("fee status"),
    CONSENT("consent status");

    private final String displayName;

    PlayerStatusField(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Returns the recorded value of this status for the given player. */
    public String getValue(Person player) {
        requireNonNull(player);
        return switch (this) {
            case AVAILABILITY -> player.getAvailability().value;
            case INJURY -> player.getInjuryStatus().value;
            case FEE -> player.getFeeStatus().value;
            case CONSENT -> player.getConsentStatus().value;
        };
    }

    /**
     * Returns a copy of the player with only this status changed.
     * Accepts domain values, such as available/unavailable, rather than CLI boolean aliases.
     *
     * @throws IllegalArgumentException if the value is invalid for this status.
     * @throws NullPointerException if the player or value is null.
     */
    public Person withValue(Person player, String value) {
        requireNonNull(player);
        requireNonNull(value);
        Availability availability = this == AVAILABILITY ? new Availability(value) : player.getAvailability();
        InjuryStatus injury = this == INJURY ? new InjuryStatus(value) : player.getInjuryStatus();
        FeeStatus fee = this == FEE ? new FeeStatus(value) : player.getFeeStatus();
        ConsentStatus consent = this == CONSENT ? new ConsentStatus(value) : player.getConsentStatus();
        return new Person(player.getName(), player.getTags(), player.getRemark(), player.getSquadName(),
                player.getPosition(), player.getGuardianName(), player.getGuardianContact(),
                availability, injury, fee, consent);
    }
}
