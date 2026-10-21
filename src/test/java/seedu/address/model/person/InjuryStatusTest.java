package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class InjuryStatusTest {
    @Test
    public void constructor_unicodeCaseVariants_storesCanonicalReloadableValues() {
        String[][] cases = {
            {"F\u0130T", "fit"},
            {"f\u0131t", "fit"},
            {"\u0130NJURED", "injured"},
            {"\u0131njured", "injured"}
        };
        for (String[] entry : cases) {
            assertTrue(InjuryStatus.isValidInjuryStatus(entry[0]));
            InjuryStatus status = new InjuryStatus(entry[0]);
            assertEquals(entry[1], status.value);
            assertEquals(new InjuryStatus(entry[1]), status);
            assertEquals(new InjuryStatus(entry[1]).hashCode(), status.hashCode());
            assertTrue(InjuryStatus.isValidInjuryStatus(status.value));
            assertEquals(status, new InjuryStatus(status.value));
        }
    }

    @Test
    public void constructor_validValues_normalizesStatus() {
        for (String value : new String[] {"fit", "injured"}) {
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
        for (String value : new String[] {"", " \t", "unknown", "un\u212Anown", "recovering", "true", "in jured"}) {
            assertFalse(InjuryStatus.isValidInjuryStatus(value));
            assertThrows(IllegalArgumentException.class, () -> new InjuryStatus(value));
        }
        assertTrue(InjuryStatus.isValidInjuryStatus(" INJURED "));
    }
}
