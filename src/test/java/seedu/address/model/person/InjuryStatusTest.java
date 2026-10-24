package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class InjuryStatusTest {
    @Test
    public void constructor_unicodeLookalikes_rejectsInput() {
        for (String value : new String[] {"F\u0130T", "f\u0131t", "\u0130NJURED", "\u0131njured",
            "f\u0456t", "\uff46\uff49\uff54", "fi\u0307t"}) {
            assertFalse(InjuryStatus.isValidInjuryStatus(value));
            assertThrows(IllegalArgumentException.class, () -> new InjuryStatus(value));
        }
    }

    @Test
    public void constructor_validValues_normalizesStatus() {
        for (String value : new String[] {"fit", "FiT", "injured", "iNjUrEd"}) {
            InjuryStatus status = new InjuryStatus(" \t" + value.toUpperCase(Locale.ROOT) + "\n");
            assertEquals(value.toLowerCase(Locale.ROOT), status.value);
            assertEquals(value.toLowerCase(Locale.ROOT), status.toString());
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
