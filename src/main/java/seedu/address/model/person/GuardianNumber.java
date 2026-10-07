package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a player's guardian's contact number.
 */
public class GuardianNumber {

    public static final String MESSAGE_CONSTRAINTS =
            "Guardian numbers should only contain digits, and should be at least 3 digits long";
    public static final String VALIDATION_REGEX = "\\d{3,}";

    public final String value;

    /**
     * Constructs a {@code GuardianNumber}.
     */
    public GuardianNumber(String guardianNumber) {
        requireNonNull(guardianNumber);
        checkArgument(isValidGuardianNumber(guardianNumber), MESSAGE_CONSTRAINTS);
        value = guardianNumber;
    }

    public static boolean isValidGuardianNumber(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof GuardianNumber otherGuardianNumber
                && value.equals(otherGuardianNumber.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
