package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GuardianNameTest {

    @Test
    public void constructor_invalidInput_throws() {
        assertThrows(NullPointerException.class, () -> new GuardianName(null));
        for (String invalid : new String[] {"", " \t\n ", "Jane/Doe", "Jane!", "Jane_Doe"}) {
            assertThrows(IllegalArgumentException.class, GuardianName.MESSAGE_CONSTRAINTS, ()
                -> new GuardianName(invalid));
        }
    }

    @Test
    public void isValidGuardianName() {
        assertThrows(NullPointerException.class, () -> GuardianName.isValidGuardianName(null));
        assertFalse(GuardianName.isValidGuardianName(" \t\r\n "));
        assertFalse(GuardianName.isValidGuardianName("Jane/Doe"));
        assertFalse(GuardianName.isValidGuardianName("Jane!"));
        assertTrue(GuardianName.isValidGuardianName(" \tJane-Anne \n Doe 2 "));
        assertTrue(GuardianName.isValidGuardianName("123"));
    }

    @Test
    public void constructor_whitespace_normalizesName() {
        GuardianName name = new GuardianName(" \tJane-Anne \r\n Doe 2 ");
        GuardianName normalized = new GuardianName("Jane-Anne Doe 2");
        assertEquals("Jane-Anne Doe 2", name.value);
        assertEquals("Jane-Anne Doe 2", name.toString());
        assertEquals(normalized, name);
        assertEquals(normalized.hashCode(), name.hashCode());
        assertFalse(name.equals(new GuardianName("jane-anne doe 2")));
        assertFalse(name.equals(null));
        assertFalse(name.equals("Jane-Anne Doe 2"));
    }
}
