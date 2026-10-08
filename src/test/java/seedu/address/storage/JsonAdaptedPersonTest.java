package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.SquadName;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_REMARK = BENSON.getRemark().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_namesWithWhitespace_normalizesNames() throws Exception {
        JsonAdaptedPerson adapted = new JsonAdaptedPerson(" \tJohn-Paul \n Doe 2 ", VALID_TAGS, VALID_REMARK,
                " \tSoccer \n Stars ", "Defender", " \tJane-Anne \n Doe ", "81234567", "available");
        Person person = adapted.toModelType();
        assertEquals("John-Paul Doe 2", person.getName().fullName);
        assertEquals("Soccer Stars", person.getSquadName().value);
        assertEquals("Jane-Anne Doe", person.getGuardianName().value);
        assertEquals(person, new JsonAdaptedPerson(person).toModelType());
    }

    @Test
    public void toModelType_invalidSquadOrGuardianName_throwsIllegalValueException() {
        JsonAdaptedPerson invalidSquad = new JsonAdaptedPerson(VALID_NAME, VALID_TAGS, VALID_REMARK,
                "Team@A", "Defender", "Jane Doe", "81234567", "available");
        assertThrows(IllegalValueException.class, SquadName.MESSAGE_CONSTRAINTS, invalidSquad::toModelType);
        JsonAdaptedPerson invalidGuardian = new JsonAdaptedPerson(VALID_NAME, VALID_TAGS, VALID_REMARK,
                "Team A", "Defender", "Jane/Doe", "81234567", "available");
        assertThrows(IllegalValueException.class, GuardianName.MESSAGE_CONSTRAINTS, invalidGuardian::toModelType);
    }

    @Test
    public void toModelType_omittedGuardianDetails_remainNullAfterRoundTrip() throws Exception {
        Person person = new JsonAdaptedPerson(VALID_NAME, VALID_TAGS, VALID_REMARK,
                "Stars", "Striker", null, null, "available").toModelType();
        assertNull(person.getGuardianName());
        assertNull(person.getGuardianContact());
        assertEquals(person, new JsonAdaptedPerson(person).toModelType());
    }

    @Test
    public void toModelType_legacyPlaceholders_migratesWithoutLosingIdentity() throws Exception {
        Person person = new JsonAdaptedPerson(VALID_NAME, VALID_TAGS, VALID_REMARK,
                "Unassigned", "Unassigned", "Not provided", "000", "available").toModelType();
        assertEquals("Unassigned", person.getPosition().value);
        assertNull(person.getGuardianName());
        assertNull(person.getGuardianContact());
        Person formerForward = new JsonAdaptedPerson(VALID_NAME, VALID_TAGS, VALID_REMARK,
                "Stars", "Forward", "Jane Doe", "81234567", "available").toModelType();
        assertEquals("Striker", formerForward.getPosition().value);
        assertEquals("81234567", formerForward.getGuardianContact().value);
    }

    @Test
    public void toModelType_guardianNamedNotProvided_preservesExplicitName() throws Exception {
        Person person = new JsonAdaptedPerson(VALID_NAME, VALID_TAGS, VALID_REMARK,
                "Stars", "Striker", "Not provided", null, "available").toModelType();
        assertEquals("Not provided", person.getGuardianName().value);
        assertEquals(person, new JsonAdaptedPerson(person).toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_TAGS, VALID_REMARK);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null,
                VALID_TAGS, VALID_REMARK);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, invalidTags, VALID_REMARK);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

}
