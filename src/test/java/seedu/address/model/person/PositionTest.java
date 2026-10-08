package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PositionTest {
    @Test
    public void constructor_supportedPositions_normalizesValue() {
        for (String value : new String[] {"Goalkeeper", "Defender", "Midfielder", "Striker"}) {
            assertEquals(value, new Position(value.toUpperCase()).value);
        }
        Position normalized = new Position(" g O a L - K e e p E r \t");
        assertEquals(new Position("Goalkeeper"), normalized);
        assertEquals(new Position("Goalkeeper").hashCode(), normalized.hashCode());
        assertEquals("Goalkeeper", normalized.toString());
    }

    @Test
    public void constructor_invalidPositions_throws() {
        assertThrows(NullPointerException.class, () -> new Position(null));
        for (String value : new String[] {"", " \t", "Left Striker", "Forward", "striker,defender", "Striker!"}) {
            assertFalse(Position.isValidPosition(value));
            assertThrows(IllegalArgumentException.class, Position.MESSAGE_CONSTRAINTS, () -> new Position(value));
        }
    }

    @Test
    public void legacyPlaceholder_isUnassigned() {
        assertTrue(new Position("Unassigned").isUnassigned());
        assertFalse(new Position("Striker").isUnassigned());
    }
}
