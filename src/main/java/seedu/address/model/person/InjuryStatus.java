package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a player's recorded injury status.
 */
public final class InjuryStatus {
    public static final String UNKNOWN = "unknown";
    public static final String FIT = "fit";
    public static final String INJURED = "injured";
    public static final String MESSAGE_CONSTRAINTS =
            "Injury status must be unknown, fit or injured.";

    public final String value;

    /** Constructs a validated, normalized injury status. */
    public InjuryStatus(String injuryStatus) {
        requireNonNull(injuryStatus);
        checkArgument(isValidInjuryStatus(injuryStatus), MESSAGE_CONSTRAINTS);
        String normalized = injuryStatus.strip();
        // Case-insensitive Unicode matches must still be stored as one of the canonical values.
        if (FIT.equalsIgnoreCase(normalized)) {
            value = FIT;
        } else if (INJURED.equalsIgnoreCase(normalized)) {
            value = INJURED;
        } else {
            value = UNKNOWN;
        }
    }

    /** Returns whether the supplied value is a supported injury status. */
    public static boolean isValidInjuryStatus(String test) {
        requireNonNull(test);
        String normalized = test.strip();
        return UNKNOWN.equalsIgnoreCase(normalized)
                || FIT.equalsIgnoreCase(normalized)
                || INJURED.equalsIgnoreCase(normalized);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof InjuryStatus otherStatus
                && value.equals(otherStatus.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
