package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GuardianContactTest {
    @Test
    public void constructor_validContact_normalizesValue() {
        GuardianContact contact = new GuardianContact(" 81-23 \t45\n67 ");
        assertEquals("81234567", contact.value);
        assertEquals("81234567", contact.toString());
        assertEquals(new GuardianContact("81234567"), contact);
        assertEquals(new GuardianContact("81234567").hashCode(), contact.hashCode());
        assertTrue(GuardianContact.isValidGuardianContact("00000000"));
    }

    @Test
    public void constructor_invalidContact_throws() {
        assertThrows(NullPointerException.class, () -> new GuardianContact(null));
        assertThrows(NullPointerException.class, () -> GuardianContact.isValidGuardianContact(null));
        for (String value : new String[] {"", " \t", "000", "1234567", "123456789", "1234abcd", "+81234567"}) {
            assertFalse(GuardianContact.isValidGuardianContact(value));
            assertThrows(IllegalArgumentException.class, GuardianContact.MESSAGE_CONSTRAINTS, ()
                -> new GuardianContact(value));
        }
    }
}
