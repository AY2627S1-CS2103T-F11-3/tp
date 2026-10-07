package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a player's guardian name.
 */
public class GuardianName {

    public static final String MESSAGE_CONSTRAINTS = "Guardian names should not be blank";
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String value;

    /**
     * Constructs a {@code GuardianName}.
     */
    public GuardianName(String guardianName) {
        requireNonNull(guardianName);
        checkArgument(isValidGuardianName(guardianName), MESSAGE_CONSTRAINTS);
        value = guardianName;
    }

    public static boolean isValidGuardianName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof GuardianName otherGuardianName
                && value.equals(otherGuardianName.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
