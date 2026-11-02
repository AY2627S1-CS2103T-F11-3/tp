package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a player's guardian contact detail.
 */
public class GuardianContact {

    public static final String MESSAGE_CONSTRAINTS =
            "Guardian contact details must not be blank.";

    public final String value;

    /**
     * Constructs a {@code GuardianContact}.
     */
    public GuardianContact(String guardianContact) {
        requireNonNull(guardianContact);
        checkArgument(isValidGuardianContact(guardianContact), MESSAGE_CONSTRAINTS);
        value = guardianContact.strip();
    }

    /** Returns whether the contact contains non-whitespace text. */
    public static boolean isValidGuardianContact(String test) {
        requireNonNull(test);
        return !test.isBlank();
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
