package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a player's recorded injury status.
 */
public final class InjuryStatus {
    public static final String FIT = "fit";
    public static final String INJURED = "injured";
    public static final String MESSAGE_CONSTRAINTS =
            "Injury status must be fit or injured using ASCII letters.";

    public final String value;

    /** Constructs a validated, normalized injury status. */
    public InjuryStatus(String injuryStatus) {
        requireNonNull(injuryStatus);
        checkArgument(isValidInjuryStatus(injuryStatus), MESSAGE_CONSTRAINTS);
        value = injuryStatus.strip().toLowerCase(Locale.ROOT);
    }

    /** Returns whether the supplied value is a supported injury status. */
    public static boolean isValidInjuryStatus(String test) {
        requireNonNull(test);
        String normalized = test.strip();
        // Java's case-insensitive regex matching is ASCII-only without UNICODE_CASE.
        return normalized.matches("(?i:fit|injured)");
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
