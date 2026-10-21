package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class FeeStatusTest {
    @Test
    public void constructor_unicodeLookalikes_rejectsInput() {
        for (String value : new String[] {"PA\u0130D", "pa\u0131d", "UNPA\u0130D",
            "unpa\u0131d", "p\u0430id", "\uff50\uff41\uff49\uff44", "pai\u0307d"}) {
            assertFalse(FeeStatus.isValidFeeStatus(value));
            assertThrows(IllegalArgumentException.class, () -> new FeeStatus(value));
        }
    }

    @Test
    public void constructor_supportedValues_normalizesAndCompares() {
        for (String value : new String[] {"paid", "unpaid"}) {
            FeeStatus status = new FeeStatus("  " + value.toUpperCase(Locale.ROOT) + "  ");
            assertEquals(value, status.value);
            assertEquals(value, status.toString());
            assertEquals(new FeeStatus(value), status);
            assertEquals(new FeeStatus(value).hashCode(), status.hashCode());
            assertTrue(status.equals(status));
            assertNotEquals(null, status);
            assertNotEquals(value, status);
        }
        assertNotEquals(new FeeStatus("paid"), new FeeStatus("unpaid"));
    }

    @Test
    public void constructor_invalidValues_rejectsInput() {
        assertThrows(NullPointerException.class, () -> new FeeStatus(null));
        assertThrows(NullPointerException.class, () -> FeeStatus.isValidFeeStatus(null));
        for (String value : new String[] {"", "  ", "unknown", "true", "other", "paid extra"}) {
            assertFalse(FeeStatus.isValidFeeStatus(value));
            assertThrows(IllegalArgumentException.class, () -> new FeeStatus(value));
        }
    }
}
