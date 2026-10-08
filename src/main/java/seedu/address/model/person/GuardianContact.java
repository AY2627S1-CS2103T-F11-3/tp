package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a player's guardian's contact number.
 */
public class GuardianContact {

    public static final String MESSAGE_CONSTRAINTS =
            "Guardian contacts should only contain digits, and should be at least 3 digits long";
    public static final String VALIDATION_REGEX = "\\d{3,}";

    public final String value;

    /**
     * Constructs a {@code GuardianContact}.
     */
    public GuardianContact(String guardianContact) {
        requireNonNull(guardianContact);
        checkArgument(isValidGuardianContact(guardianContact), MESSAGE_CONSTRAINTS);
        value = guardianContact;
    }

    public static boolean isValidGuardianContact(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof GuardianContact otherGuardianContact
                && value.equals(otherGuardianContact.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
