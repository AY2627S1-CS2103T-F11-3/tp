package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GuardianContactTest {
    @Test
    public void constructor_validContact_preservesTextApartFromSurroundingWhitespace() {
        GuardianContact contact = new GuardianContact(" +65 (8123) 4567 (Mother) ");
        assertEquals("+65 (8123) 4567 (Mother)", contact.value);
        assertEquals("+65 (8123) 4567 (Mother)", contact.toString());
        assertEquals(new GuardianContact("+65 (8123) 4567 (Mother)"), contact);
        assertEquals(new GuardianContact("+65 (8123) 4567 (Mother)").hashCode(), contact.hashCode());
        assertTrue(GuardianContact.isValidGuardianContact("00000000"));
        assertTrue(GuardianContact.isValidGuardianContact("12025550123 ext. 4"));
        assertTrue(GuardianContact.isValidGuardianContact("+44 20 7946 0958 / +44 20 7946 0959"));
    }

    @Test
    public void constructor_invalidContact_throws() {
        assertThrows(NullPointerException.class, () -> new GuardianContact(null));
        assertThrows(NullPointerException.class, () -> GuardianContact.isValidGuardianContact(null));
        for (String value : new String[] {"", " \t"}) {
            assertFalse(GuardianContact.isValidGuardianContact(value));
            assertThrows(IllegalArgumentException.class, GuardianContact.MESSAGE_CONSTRAINTS, ()
                -> new GuardianContact(value));
        }
    }
}
