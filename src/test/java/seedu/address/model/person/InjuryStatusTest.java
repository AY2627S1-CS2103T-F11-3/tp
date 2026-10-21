package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class InjuryStatusTest {
    @Test
    public void constructor_validValues_normalizesStatus() {
        for (String value : new String[] {"unknown", "fit", "injured"}) {
            InjuryStatus status = new InjuryStatus(" \t" + value.toUpperCase(Locale.ROOT) + "\n");
            assertEquals(value, status.value);
            assertEquals(value, status.toString());
            assertEquals(new InjuryStatus(value), status);
            assertEquals(new InjuryStatus(value).hashCode(), status.hashCode());
        }
        assertFalse(new InjuryStatus("fit").equals(new InjuryStatus("injured")));
        assertFalse(new InjuryStatus("fit").equals(null));
        assertFalse(new InjuryStatus("fit").equals("fit"));
    }

    @Test
    public void constructor_invalidValues_throws() {
        assertThrows(NullPointerException.class, () -> new InjuryStatus(null));
        assertThrows(NullPointerException.class, () -> InjuryStatus.isValidInjuryStatus(null));
        for (String value : new String[] {"", " \t", "recovering", "true", "in jured"}) {
            assertFalse(InjuryStatus.isValidInjuryStatus(value));
            assertThrows(IllegalArgumentException.class, () -> new InjuryStatus(value));
        }
        assertTrue(InjuryStatus.isValidInjuryStatus(" INJURED "));
    }
}
