package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void feeAndConsent_affectEqualityButNotIdentityAndSurviveCopies() {
        Person defaults = new PersonBuilder().build();
        Person paid = new PersonBuilder(defaults).withFeeStatus("paid").build();
        Person consented = new PersonBuilder(defaults).withConsentStatus("received").build();
        assertEquals("unpaid", defaults.getFeeStatus().value);
        assertEquals("pending", defaults.getConsentStatus().value);
        assertTrue(defaults.isSamePerson(paid));
        assertTrue(defaults.isSamePerson(consented));
        assertFalse(defaults.equals(paid));
        assertFalse(defaults.equals(consented));
        Person both = new PersonBuilder(paid).withConsentStatus("received").build();
        assertEquals(both, new PersonBuilder(both).build());
        assertEquals(both.hashCode(), new PersonBuilder(both).build().hashCode());
        assertThrows(NullPointerException.class, () -> new Person(both.getName(), both.getTags(),
                both.getRemark(), both.getSquadName(), both.getPosition(), both.getGuardianName(),
                both.getGuardianContact(), both.getAvailability(), both.getInjuryStatus(),
                null, both.getConsentStatus()));
        assertThrows(NullPointerException.class, () -> new Person(both.getName(), both.getTags(),
                both.getRemark(), both.getSquadName(), both.getPosition(), both.getGuardianName(),
                both.getGuardianContact(), both.getAvailability(), both.getInjuryStatus(), both.getFeeStatus(), null));
    }

    @Test
    public void injuryStatus_affectsEqualityButNotIdentity() {
        Person fit = new PersonBuilder().build();
        Person injured = new PersonBuilder(fit).withInjuryStatus("injured").build();
        assertEquals("fit", fit.getInjuryStatus().value);
        assertTrue(fit.isSamePerson(injured));
        assertFalse(fit.equals(injured));
        Person copied = new PersonBuilder(injured).build();
        assertEquals(injured, copied);
        assertEquals(injured.hashCode(), copied.hashCode());
        assertEquals(injured.getAvailability(), fit.getAvailability());
        assertTrue(injured.toString().contains("injuryStatus=injured"));
    }

    @Test
    public void constructor_nullInjuryStatus_throwsNullPointerException() {
        Person person = new PersonBuilder().build();
        assertThrows(NullPointerException.class, () -> new Person(person.getName(), person.getTags(),
                person.getRemark(), person.getSquadName(), person.getPosition(), person.getGuardianName(),
                person.getGuardianContact(), person.getAvailability(), null));
    }

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same identity, different tags -> returns true
        Person editedAlice = new PersonBuilder(ALICE)
                .withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        assertFalse(ALICE.isSamePerson(new PersonBuilder(ALICE).withSquadName("Another Squad").build()));
        assertFalse(ALICE.isSamePerson(new PersonBuilder(ALICE).withPosition("Striker").build()));
        assertTrue(ALICE.isSamePerson(new PersonBuilder(ALICE).withGuardianName("Jane Doe")
                .withGuardianContact("81234567").withAvailability("unavailable").build()));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns true after normalization
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    // TODO: test formats
    @Test
    public void toListString_validPerson_returnsFormattedString() {
        Person person = new Person(
                new Name("John Doe"),
                new HashSet<>(),
                new Remark(""),
                new SquadName("Soccer Stars"),
                new Position("Goalkeeper"),
                new GuardianName("Jane Doe"),
                new GuardianContact("91234567"),
                new Availability("available")
        );

        String expected = "John Doe, Soccer Stars, Goalkeeper, available";
        assertEquals(expected, person.toListString());
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

        // different squad name -> returns false
        editedAlice = new PersonBuilder(ALICE).withSquadName("U15 A").build();
        assertFalse(ALICE.equals(editedAlice));

        // different position -> returns false
        editedAlice = new PersonBuilder(ALICE).withPosition("Goalkeeper").build();
        assertFalse(ALICE.equals(editedAlice));

        // different guardian name -> returns false
        editedAlice = new PersonBuilder(ALICE).withGuardianName("Alex Tan").build();
        assertFalse(ALICE.equals(editedAlice));

        // different guardian contact -> returns false
        editedAlice = new PersonBuilder(ALICE).withGuardianContact("91234567").build();
        assertFalse(ALICE.equals(editedAlice));

        // different availability -> returns false
        editedAlice = new PersonBuilder(ALICE).withAvailability(Availability.UNAVAILABLE).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", tags=" + ALICE.getTags()
                + ", squadName=" + ALICE.getSquadName() + ", position=" + ALICE.getPosition()
                + ", guardianName=" + ALICE.getGuardianName() + ", guardianContact="
                + ALICE.getGuardianContact() + ", availability=" + ALICE.getAvailability()
                + ", injuryStatus=" + ALICE.getInjuryStatus()
                + ", feeStatus=" + ALICE.getFeeStatus() + ", consentStatus=" + ALICE.getConsentStatus() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
