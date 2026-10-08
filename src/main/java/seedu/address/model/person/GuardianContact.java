package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a player's guardian's contact number.
 */
public class GuardianContact {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid telephone number entered! Telephone number should be 8 digits long and contain only digits!";
    public static final String VALIDATION_REGEX = "[0-9]{8}";

    public final String value;

    /**
     * Constructs a {@code GuardianContact}.
     */
    public GuardianContact(String guardianContact) {
        requireNonNull(guardianContact);
        checkArgument(isValidGuardianContact(guardianContact), MESSAGE_CONSTRAINTS);
        value = normalize(guardianContact);
    }

    /** Returns whether the contact normalizes to exactly eight digits. */
    public static boolean isValidGuardianContact(String test) {
        requireNonNull(test);
        return normalize(test).matches(VALIDATION_REGEX);
    }

    private static String normalize(String contact) {
        return contact.replaceAll("[\\s-]", "");
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
