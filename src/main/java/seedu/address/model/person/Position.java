package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a player's football position.
 */
public class Position {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid position entered! Please choose from goalkeeper, defender, midfielder or striker!";

    /** The supported positions, plus the placeholder used by existing saved profiles. */
    public enum Role {
        GOALKEEPER, DEFENDER, MIDFIELDER, STRIKER, UNASSIGNED
    }

    public final String value;
    private final Role role;

    /**
     * Constructs a {@code Position}.
     */
    public Position(String position) {
        requireNonNull(position);
        checkArgument(isValidPosition(position), MESSAGE_CONSTRAINTS);
        role = Role.valueOf(normalize(position));
        value = role.name().charAt(0) + role.name().substring(1).toLowerCase(Locale.ROOT);
    }

    /** Returns whether the position is supported, including the legacy placeholder. */
    public static boolean isValidPosition(String test) {
        requireNonNull(test);
        try {
            Role.valueOf(normalize(test));
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /** Returns whether this is a legacy placeholder rather than a selectable position. */
    public boolean isUnassigned() {
        return role == Role.UNASSIGNED;
    }

    private static String normalize(String position) {
        return position.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Position otherPosition && value.equals(otherPosition.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
