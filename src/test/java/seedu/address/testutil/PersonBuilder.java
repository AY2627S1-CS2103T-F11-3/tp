package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.person.Address;
import seedu.address.model.person.Availability;
import seedu.address.model.person.Email;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.GuardianNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
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
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final String DEFAULT_REMARK = "";
    public static final String DEFAULT_SQUAD_NAME = "Unassigned";
    public static final String DEFAULT_POSITION = "Unassigned";
    public static final String DEFAULT_GUARDIAN_NAME = "Not provided";
    public static final String DEFAULT_GUARDIAN_NUMBER = "000";
    public static final String DEFAULT_AVAILABILITY = Availability.AVAILABLE;

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Set<Tag> tags;
    private Remark remark;
    private SquadName squadName;
    private Position position;
    private GuardianName guardianName;
    private GuardianNumber guardianNumber;
    private Availability availability;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        tags = new HashSet<>();
        remark = new Remark(DEFAULT_REMARK);
        squadName = new SquadName(DEFAULT_SQUAD_NAME);
        position = new Position(DEFAULT_POSITION);
        guardianName = new GuardianName(DEFAULT_GUARDIAN_NAME);
        guardianNumber = new GuardianNumber(DEFAULT_GUARDIAN_NUMBER);
        availability = new Availability(DEFAULT_AVAILABILITY);
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        tags = new HashSet<>(personToCopy.getTags());
        remark = personToCopy.getRemark();
        squadName = personToCopy.getSquadName();
        position = personToCopy.getPosition();
        guardianName = personToCopy.getGuardianName();
        guardianNumber = personToCopy.getGuardianNumber();
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
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
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
     * Sets the guardian number of the {@code Person} that we are building.
     */
    public PersonBuilder withGuardianNumber(String guardianNumber) {
        this.guardianNumber = new GuardianNumber(guardianNumber);
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
        return new Person(name, phone, email, address, tags, remark, squadName, position, guardianName,
                guardianNumber, availability);
    }

}
