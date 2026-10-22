package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

/**
 * Tests independent status updates and preservation of player profile data.
 */
public class PlayerStatusFieldTest {
    private final Person player = new PersonBuilder().withName("Amy Tan").withSquadName("U12")
            .withPosition("Defender").withGuardianName("Jane Tan").withGuardianContact("81234567")
            .withTags("captain").withRemark("Left footed").withAvailability("unavailable")
            .withInjuryStatus("injured").withFeeStatus("paid").withConsentStatus("received").build();

    @Test
    public void withValue_eachField_preservesOtherProfileData() {
        assertUpdate(PlayerStatusField.AVAILABILITY, "available",
                new PersonBuilder(player).withAvailability("available").build());
        assertUpdate(PlayerStatusField.INJURY, "fit",
                new PersonBuilder(player).withInjuryStatus("fit").build());
        assertUpdate(PlayerStatusField.FEE, "unpaid",
                new PersonBuilder(player).withFeeStatus("unpaid").build());
        assertUpdate(PlayerStatusField.CONSENT, "pending",
                new PersonBuilder(player).withConsentStatus("pending").build());
    }

    @Test
    public void withValue_existingStatus_preservesPlayerData() {
        for (PlayerStatusField field : PlayerStatusField.values()) {
            assertUpdate(field, field.getValue(player), player);
        }
    }

    @Test
    public void withValue_mixedCase_usesExistingStatusNormalization() {
        assertUpdate(PlayerStatusField.AVAILABILITY, "AVAILABLE",
                new PersonBuilder(player).withAvailability("available").build());
        assertUpdate(PlayerStatusField.INJURY, " FIT ",
                new PersonBuilder(player).withInjuryStatus("fit").build());
        assertUpdate(PlayerStatusField.FEE, " UNPAID ",
                new PersonBuilder(player).withFeeStatus("unpaid").build());
        assertUpdate(PlayerStatusField.CONSENT, " PENDING ",
                new PersonBuilder(player).withConsentStatus("pending").build());
    }

    @Test
    public void withValue_invalidValue_rejectsWithoutChangingOriginal() {
        for (PlayerStatusField field : PlayerStatusField.values()) {
            String originalValue = field.getValue(player);
            assertThrows(IllegalArgumentException.class, () -> field.withValue(player, "unknown"));
            assertThrows(IllegalArgumentException.class, () -> field.withValue(player, ""));
            assertEquals(originalValue, field.getValue(player));
        }
    }

    @Test
    public void withValue_missingInput_rejectsNull() {
        for (PlayerStatusField field : PlayerStatusField.values()) {
            assertThrows(NullPointerException.class, () -> field.withValue(null, "available"));
            assertThrows(NullPointerException.class, () -> field.withValue(player, null));
            assertThrows(NullPointerException.class, () -> field.getValue(null));
        }
    }

    private void assertUpdate(PlayerStatusField field, String value, Person expected) {
        Person updated = field.withValue(player, value);
        assertEquals(expected, updated);
        assertEquals(expected.getRemark(), updated.getRemark());
        assertEquals(field.getValue(expected), field.getValue(updated));
    }
}
