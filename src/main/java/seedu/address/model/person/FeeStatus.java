package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a player's recorded fee payment status.
 */
public final class FeeStatus {
    public static final String PAID = "paid";
    public static final String UNPAID = "unpaid";
    public static final String MESSAGE_CONSTRAINTS =
            "Fee payment status must be paid or unpaid.";

    public final String value;

    /** Constructs a validated, normalized fee payment status. */
    public FeeStatus(String feeStatus) {
        requireNonNull(feeStatus);
        checkArgument(isValidFeeStatus(feeStatus), MESSAGE_CONSTRAINTS);
        String normalized = feeStatus.strip();
        // Case-insensitive Unicode matches must still be stored as one of the canonical values.
        if (PAID.equalsIgnoreCase(normalized)) {
            value = PAID;
        } else {
            value = UNPAID;
        }
    }

    /** Returns whether the supplied value is a supported fee payment status. */
    public static boolean isValidFeeStatus(String test) {
        requireNonNull(test);
        String normalized = test.strip();
        return PAID.equalsIgnoreCase(normalized)
                || UNPAID.equalsIgnoreCase(normalized);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof FeeStatus otherStatus
                && value.equals(otherStatus.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
