package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.person.Availability;
import seedu.address.model.person.GuardianContact;
import seedu.address.model.person.GuardianName;
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
    public static final String DEFAULT_GUARDIAN_NAME = "Not provided";
    public static final String DEFAULT_GUARDIAN_CONTACT = "000";
    public static final String DEFAULT_AVAILABILITY = Availability.AVAILABLE;

    private Name name;
    private Set<Tag> tags;
    private Remark remark;
    private SquadName squadName;
    private Position position;
    private GuardianName guardianName;
    private GuardianContact guardianContact;
    private Availability availability;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        tags = new HashSet<>();
        remark = new Remark(DEFAULT_REMARK);
        squadName = new SquadName(DEFAULT_SQUAD_NAME);
        position = new Position(DEFAULT_POSITION);
        guardianName = new GuardianName(DEFAULT_GUARDIAN_NAME);
        guardianContact = new GuardianContact(DEFAULT_GUARDIAN_CONTACT);
        availability = new Availability(DEFAULT_AVAILABILITY);
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
        this.guardianName = new GuardianName(guardianName);
        return this;
    }

    /**
     * Sets the guardian contact of the {@code Person} that we are building.
     */
    public PersonBuilder withGuardianContact(String guardianContact) {
        this.guardianContact = new GuardianContact(guardianContact);
        return this;
    }

    /**
     * Sets the availability of the {@code Person} that we are building.
     */
    public PersonBuilder withAvailability(String availability) {
        this.availability = new Availability(availability);
        return this;
    }

    /**
     * Builds a {@code Person} with the configured details.
     */
    public Person build() {
        return new Person(name, tags, remark, squadName, position, guardianName,
                guardianContact, availability);
    }

}
