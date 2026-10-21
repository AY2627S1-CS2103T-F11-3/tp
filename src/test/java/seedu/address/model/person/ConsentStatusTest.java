package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class ConsentStatusTest {
    @Test
    public void constructor_supportedValues_normalizesAndCompares() {
        for (String value : new String[] {"received", "pending"}) {
            ConsentStatus status = new ConsentStatus("  " + value.toUpperCase(Locale.ROOT) + "  ");
            assertEquals(value, status.value);
            assertEquals(value, status.toString());
            assertEquals(new ConsentStatus(value), status);
            assertEquals(new ConsentStatus(value).hashCode(), status.hashCode());
            assertTrue(status.equals(status));
            assertNotEquals(null, status);
            assertNotEquals(value, status);
        }
        assertNotEquals(new ConsentStatus("received"), new ConsentStatus("pending"));
        assertEquals("received", new ConsentStatus("RECEİVED").value);
    }

    @Test
    public void constructor_invalidValues_rejectsInput() {
        assertThrows(NullPointerException.class, () -> new ConsentStatus(null));
        assertThrows(NullPointerException.class, () -> ConsentStatus.isValidConsentStatus(null));
        for (String value : new String[] {"", "  ", "unknown", "true", "other", "received extra"}) {
            assertFalse(ConsentStatus.isValidConsentStatus(value));
            assertThrows(IllegalArgumentException.class, () -> new ConsentStatus(value));
        }
    }
}
