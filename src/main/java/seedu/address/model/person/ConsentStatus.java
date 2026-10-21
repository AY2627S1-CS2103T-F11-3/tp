package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a player's recorded consent status.
 */
public final class ConsentStatus {
    public static final String UNKNOWN = "unknown";
    public static final String RECEIVED = "received";
    public static final String PENDING = "pending";
    public static final String MESSAGE_CONSTRAINTS =
            "Consent status must be unknown, received or pending.";

    public final String value;

    /** Constructs a validated, normalized consent status. */
    public ConsentStatus(String consentStatus) {
        requireNonNull(consentStatus);
        checkArgument(isValidConsentStatus(consentStatus), MESSAGE_CONSTRAINTS);
        String normalized = consentStatus.strip();
        // Case-insensitive Unicode matches must still be stored as one of the canonical values.
        if (RECEIVED.equalsIgnoreCase(normalized)) {
            value = RECEIVED;
        } else if (PENDING.equalsIgnoreCase(normalized)) {
            value = PENDING;
        } else {
            value = UNKNOWN;
        }
    }

    /** Returns whether the supplied value is a supported consent status. */
    public static boolean isValidConsentStatus(String test) {
        requireNonNull(test);
        String normalized = test.strip();
        return UNKNOWN.equalsIgnoreCase(normalized)
                || RECEIVED.equalsIgnoreCase(normalized)
                || PENDING.equalsIgnoreCase(normalized);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof ConsentStatus otherStatus
                && value.equals(otherStatus.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
