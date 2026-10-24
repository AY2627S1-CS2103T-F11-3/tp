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
import seedu.address.model.person.ConsentStatus;
import seedu.address.model.person.FeeStatus;
import seedu.address.model.person.GuardianContact;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.InjuryStatus;
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
    private final String guardianContact;
    private final String availability;
    private final String injuryStatus;
    private final String feeStatus;
    private final String consentStatus;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name,
            @JsonProperty("tags") List<JsonAdaptedTag> tags, @JsonProperty("remark") String remark,
            @JsonProperty("squadName") String squadName, @JsonProperty("position") String position,
            @JsonProperty("guardianName") String guardianName,
            @JsonProperty("guardianContact") String guardianContact,
            @JsonProperty("availability") String availability,
            @JsonProperty("guardianNumber") String legacyGuardianContact,
            @JsonProperty("injuryStatus") String injuryStatus,
            @JsonProperty("feeStatus") String feeStatus,
            @JsonProperty("consentStatus") String consentStatus) {
        this.name = name;
        this.remark = remark;
        this.squadName = squadName;
        this.position = position;
        this.guardianName = guardianName;
        this.guardianContact = guardianContact == null ? legacyGuardianContact : guardianContact;
        this.availability = availability;
        this.injuryStatus = injuryStatus;
        this.feeStatus = feeStatus;
        this.consentStatus = consentStatus;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /** Constructs a record without recorded fee or consent statuses. */
    public JsonAdaptedPerson(String name, List<JsonAdaptedTag> tags, String remark,
            String squadName, String position, String guardianName, String guardianContact,
            String availability, String legacyGuardianContact, String injuryStatus) {
        this(name, tags, remark, squadName, position, guardianName, guardianContact,
                availability, legacyGuardianContact, injuryStatus, null, null);
    }

    /** Constructs a legacy record without a recorded injury status. */
    public JsonAdaptedPerson(String name, List<JsonAdaptedTag> tags, String remark,
            String squadName, String position, String guardianName, String guardianContact,
            String availability, String legacyGuardianContact) {
        this(name, tags, remark, squadName, position, guardianName, guardianContact,
                availability, legacyGuardianContact, null);
    }

    /**
     * Constructs a person using the current field names.
     */
    public JsonAdaptedPerson(String name, List<JsonAdaptedTag> tags, String remark,
            String squadName, String position, String guardianName, String guardianContact, String availability) {
        this(name, tags, remark, squadName, position, guardianName, guardianContact, availability, null);
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
        guardianName = source.getGuardianName() == null ? null : source.getGuardianName().value;
        guardianContact = source.getGuardianContact() == null ? null : source.getGuardianContact().value;
        availability = source.getAvailability().value;
        injuryStatus = source.getInjuryStatus().value;
        feeStatus = source.getFeeStatus().value;
        consentStatus = source.getConsentStatus().value;
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
        final GuardianContact modelGuardianContact = createGuardianContact();
        final Availability modelAvailability = createAvailability();

        return new Person(modelName, modelTags, modelRemark, modelSquadName,
                modelPosition, modelGuardianName, modelGuardianContact, modelAvailability, createInjuryStatus(),
                createFeeStatus(), createConsentStatus());
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
        // Sample profiles previously used Forward for the striker role.
        if ("Forward".equalsIgnoreCase(value)) {
            value = "Striker";
        }
        if (!Position.isValidPosition(value)) {
            throw new IllegalValueException(Position.MESSAGE_CONSTRAINTS);
        }
        return new Position(value);
    }

    private GuardianName createGuardianName() throws IllegalValueException {
        if (guardianName == null || ("Not provided".equals(guardianName) && "000".equals(guardianContact))) {
            return null;
        }
        String value = guardianName;
        if (!GuardianName.isValidGuardianName(value)) {
            throw new IllegalValueException(GuardianName.MESSAGE_CONSTRAINTS);
        }
        return new GuardianName(value);
    }

    private GuardianContact createGuardianContact() throws IllegalValueException {
        if (guardianContact == null || "000".equals(guardianContact)) {
            return null;
        }
        String value = guardianContact;
        if (!GuardianContact.isValidGuardianContact(value)) {
            throw new IllegalValueException(GuardianContact.MESSAGE_CONSTRAINTS);
        }
        return new GuardianContact(value);
    }

    private FeeStatus createFeeStatus() throws IllegalValueException {
        String value = feeStatus == null || feeStatus.strip().matches("(?i:unknown)")
                ? FeeStatus.UNPAID : feeStatus;
        if (!FeeStatus.isValidFeeStatus(value)) {
            throw new IllegalValueException(FeeStatus.MESSAGE_CONSTRAINTS);
        }
        return new FeeStatus(value);
    }

    private ConsentStatus createConsentStatus() throws IllegalValueException {
        String value = consentStatus == null || consentStatus.strip().matches("(?i:unknown)")
                ? ConsentStatus.PENDING : consentStatus;
        if (!ConsentStatus.isValidConsentStatus(value)) {
            throw new IllegalValueException(ConsentStatus.MESSAGE_CONSTRAINTS);
        }
        return new ConsentStatus(value);
    }

    private InjuryStatus createInjuryStatus() throws IllegalValueException {
        String value = injuryStatus == null || injuryStatus.strip().matches("(?i:unknown)")
                ? InjuryStatus.FIT : injuryStatus;
        if (!InjuryStatus.isValidInjuryStatus(value)) {
            throw new IllegalValueException(InjuryStatus.MESSAGE_CONSTRAINTS);
        }
        return new InjuryStatus(value);
    }

    private Availability createAvailability() throws IllegalValueException {
        String value = availability == null ? Availability.AVAILABLE : availability;
        if (!Availability.isValidAvailability(value)) {
            throw new IllegalValueException(Availability.MESSAGE_CONSTRAINTS);
        }
        return new Availability(value);
    }

}
