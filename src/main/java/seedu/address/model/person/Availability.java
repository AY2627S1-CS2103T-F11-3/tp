package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents whether a player is available for selection.
 */
public class Availability {

    public static final String AVAILABLE = "available";
    public static final String UNAVAILABLE = "unavailable";
    public static final String MESSAGE_CONSTRAINTS = "Availability must be either 'available' or 'unavailable'";

    public final String value;

    /**
     * Constructs an {@code Availability} value.
     */
    public Availability(String availability) {
        requireNonNull(availability);
        checkArgument(isValidAvailability(availability), MESSAGE_CONSTRAINTS);
        value = availability.toLowerCase(Locale.ROOT);
    }

    public static boolean isValidAvailability(String test) {
        return AVAILABLE.equalsIgnoreCase(test) || UNAVAILABLE.equalsIgnoreCase(test);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Availability otherAvailability
                && value.equals(otherAvailability.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
