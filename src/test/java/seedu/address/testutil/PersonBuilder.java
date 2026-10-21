package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

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
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_REMARK = "";
    public static final String DEFAULT_SQUAD_NAME = "Unassigned";
    public static final String DEFAULT_POSITION = "Unassigned";
    public static final String DEFAULT_GUARDIAN_NAME = null;
    public static final String DEFAULT_GUARDIAN_CONTACT = null;
    public static final String DEFAULT_AVAILABILITY = Availability.AVAILABLE;
    public static final String DEFAULT_INJURY_STATUS = InjuryStatus.UNKNOWN;

    private Name name;
    private Set<Tag> tags;
    private Remark remark;
    private SquadName squadName;
    private Position position;
    private GuardianName guardianName;
    private GuardianContact guardianContact;
    private Availability availability;
    private InjuryStatus injuryStatus;
    private FeeStatus feeStatus;
    private ConsentStatus consentStatus;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        tags = new HashSet<>();
        remark = new Remark(DEFAULT_REMARK);
        squadName = new SquadName(DEFAULT_SQUAD_NAME);
        position = new Position(DEFAULT_POSITION);
        guardianName = null;
        guardianContact = null;
        availability = new Availability(DEFAULT_AVAILABILITY);
        injuryStatus = new InjuryStatus(DEFAULT_INJURY_STATUS);
        feeStatus = new FeeStatus(FeeStatus.UNKNOWN);
        consentStatus = new ConsentStatus(ConsentStatus.UNKNOWN);
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        tags = new HashSet<>(personToCopy.getTags());
        remark = personToCopy.getRemark();
        squadName = personToCopy.getSquadName();
        position = personToCopy.getPosition();
        guardianName = personToCopy.getGuardianName();
        guardianContact = personToCopy.getGuardianContact();
        availability = personToCopy.getAvailability();
        injuryStatus = personToCopy.getInjuryStatus();
        feeStatus = personToCopy.getFeeStatus();
        consentStatus = personToCopy.getConsentStatus();
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Remark} of the {@code Person} that we are building.
     */
    public PersonBuilder withRemark(String remark) {
        this.remark = new Remark(remark);
        return this;
    }

    /**
     * Sets the squad name of the {@code Person} that we are building.
     */
    public PersonBuilder withSquadName(String squadName) {
        this.squadName = new SquadName(squadName);
        return this;
    }

    /**
     * Sets the position of the {@code Person} that we are building.
     */
    public PersonBuilder withPosition(String position) {
        this.position = new Position(position);
        return this;
    }

    /**
     * Sets the guardian name of the {@code Person} that we are building.
     */
    public PersonBuilder withGuardianName(String guardianName) {
        this.guardianName = guardianName == null ? null : new GuardianName(guardianName);
        return this;
    }

    /**
     * Sets the guardian contact of the {@code Person} that we are building.
     */
    public PersonBuilder withGuardianContact(String guardianContact) {
        this.guardianContact = guardianContact == null ? null : new GuardianContact(guardianContact);
        return this;
    }

    /**
     * Sets the availability of the {@code Person} that we are building.
     */
    public PersonBuilder withAvailability(String availability) {
        this.availability = new Availability(availability);
        return this;
    }

    /** Sets the recorded fee payment status. */
    public PersonBuilder withFeeStatus(String feeStatus) {
        this.feeStatus = new FeeStatus(feeStatus);
        return this;
    }

    /** Sets the recorded consent status. */
    public PersonBuilder withConsentStatus(String consentStatus) {
        this.consentStatus = new ConsentStatus(consentStatus);
        return this;
    }

    /** Sets the injury status of the player being built. */
    public PersonBuilder withInjuryStatus(String injuryStatus) {
        this.injuryStatus = new InjuryStatus(injuryStatus);
        return this;
    }

    /**
     * Builds a {@code Person} with the configured details.
     */
    public Person build() {
        return new Person(name, tags, remark, squadName, position, guardianName,
                guardianContact, availability, injuryStatus, feeStatus, consentStatus);
    }

}
