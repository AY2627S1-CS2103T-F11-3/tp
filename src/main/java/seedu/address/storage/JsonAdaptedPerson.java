package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Availability;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.GuardianNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Position;
import seedu.address.model.person.Remark;
import seedu.address.model.person.SquadName;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final String remark;
    private final String squadName;
    private final String position;
    private final String guardianName;
    private final String guardianNumber;
    private final String availability;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name,
            @JsonProperty("tags") List<JsonAdaptedTag> tags, @JsonProperty("remark") String remark,
            @JsonProperty("squadName") String squadName, @JsonProperty("position") String position,
            @JsonProperty("guardianName") String guardianName, @JsonProperty("guardianNumber") String guardianNumber,
            @JsonProperty("availability") String availability) {
        this.name = name;
        this.remark = remark;
        this.squadName = squadName;
        this.position = position;
        this.guardianName = guardianName;
        this.guardianNumber = guardianNumber;
        this.availability = availability;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    JsonAdaptedPerson(String name, List<JsonAdaptedTag> tags,
                       String remark) {
        this(name, tags, remark, null, null, null, null, null);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        remark = source.getRemark().value;
        squadName = source.getSquadName().value;
        position = source.getPosition().value;
        guardianName = source.getGuardianName().value;
        guardianNumber = source.getGuardianNumber().value;
        availability = source.getAvailability().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        final Set<Tag> modelTags = new HashSet<>(personTags);

        if (remark == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Remark.class.getSimpleName()));
        }
        final Remark modelRemark = new Remark(remark);

        final SquadName modelSquadName = createSquadName();
        final Position modelPosition = createPosition();
        final GuardianName modelGuardianName = createGuardianName();
        final GuardianNumber modelGuardianNumber = createGuardianNumber();
        final Availability modelAvailability = createAvailability();

        return new Person(modelName, modelTags, modelRemark, modelSquadName,
                modelPosition, modelGuardianName, modelGuardianNumber, modelAvailability);
    }

    private SquadName createSquadName() throws IllegalValueException {
        String value = squadName == null ? "Unassigned" : squadName;
        if (!SquadName.isValidSquadName(value)) {
            throw new IllegalValueException(SquadName.MESSAGE_CONSTRAINTS);
        }
        return new SquadName(value);
    }

    private Position createPosition() throws IllegalValueException {
        String value = position == null ? "Unassigned" : position;
        if (!Position.isValidPosition(value)) {
            throw new IllegalValueException(Position.MESSAGE_CONSTRAINTS);
        }
        return new Position(value);
    }

    private GuardianName createGuardianName() throws IllegalValueException {
        String value = guardianName == null ? "Not provided" : guardianName;
        if (!GuardianName.isValidGuardianName(value)) {
            throw new IllegalValueException(GuardianName.MESSAGE_CONSTRAINTS);
        }
        return new GuardianName(value);
    }

    private GuardianNumber createGuardianNumber() throws IllegalValueException {
        String value = guardianNumber == null ? "000" : guardianNumber;
        if (!GuardianNumber.isValidGuardianNumber(value)) {
            throw new IllegalValueException(GuardianNumber.MESSAGE_CONSTRAINTS);
        }
        return new GuardianNumber(value);
    }

    private Availability createAvailability() throws IllegalValueException {
        String value = availability == null ? Availability.AVAILABLE : availability;
        if (!Availability.isValidAvailability(value)) {
            throw new IllegalValueException(Availability.MESSAGE_CONSTRAINTS);
        }
        return new Availability(value);
    }

}
