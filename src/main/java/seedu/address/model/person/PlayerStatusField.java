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
        Availability availability = player.getAvailability();
        InjuryStatus injury = player.getInjuryStatus();
        FeeStatus fee = player.getFeeStatus();
        ConsentStatus consent = player.getConsentStatus();
        switch (this) {
            case AVAILABILITY:
                availability = new Availability(value);
                break;
            case INJURY:
                injury = new InjuryStatus(value);
                break;
            case FEE:
                fee = new FeeStatus(value);
                break;
            case CONSENT:
                consent = new ConsentStatus(value);
                break;
            default:
                throw new AssertionError("Unhandled player status: " + this);
        }
        return new Person(player.getName(), player.getTags(), player.getRemark(), player.getSquadName(),
                player.getPosition(), player.getGuardianName(), player.getGuardianContact(),
                availability, injury, fee, consent);
    }
}
