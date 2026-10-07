package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a player's squad name.
 */
public class SquadName {

    public static final String MESSAGE_CONSTRAINTS = "Squad names should not be blank";
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String value;

    /**
     * Constructs a {@code SquadName}.
     */
    public SquadName(String squadName) {
        requireNonNull(squadName);
        checkArgument(isValidSquadName(squadName), MESSAGE_CONSTRAINTS);
        value = squadName;
    }

    public static boolean isValidSquadName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof SquadName otherSquadName && value.equals(otherSquadName.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
