package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SquadNameTest {

    @Test
    public void constructor_invalidInput_throws() {
        assertThrows(NullPointerException.class, () -> new SquadName(null));
        for (String invalid : new String[] {"", " \t\n ", "Squad/A", "Stars!", "Team_1"}) {
            assertThrows(IllegalArgumentException.class, SquadName.MESSAGE_CONSTRAINTS, () -> new SquadName(invalid));
        }
    }

    @Test
    public void isValidSquadName() {
        assertThrows(NullPointerException.class, () -> SquadName.isValidSquadName(null));
        assertFalse(SquadName.isValidSquadName(" \t\r\n "));
        assertFalse(SquadName.isValidSquadName("Squad/A"));
        assertFalse(SquadName.isValidSquadName("Stars!"));
        assertTrue(SquadName.isValidSquadName(" \tU12-A \n Stars 2 "));
        assertTrue(SquadName.isValidSquadName("123"));
    }

    @Test
    public void constructor_whitespace_normalizesName() {
        SquadName name = new SquadName(" \tU12-A \r\n Stars 2 ");
        SquadName normalized = new SquadName("U12-A Stars 2");
        assertEquals("U12-A Stars 2", name.value);
        assertEquals("U12-A Stars 2", name.toString());
        assertEquals(normalized, name);
        assertEquals(normalized.hashCode(), name.hashCode());
        assertFalse(name.equals(new SquadName("u12-a stars 2")));
        assertFalse(name.equals(null));
        assertFalse(name.equals("U12-A Stars 2"));
    }
}
