package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;

    // Data fields
    private final SquadName squadName;
    private final Position position;
    private final GuardianName guardianName;
    private final GuardianNumber guardianNumber;
    private final Availability availability;
    private final Set<Tag> tags = new HashSet<>();
    private final Remark remark;

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Set<Tag> tags, Remark remark,
                  SquadName squadName, Position position, GuardianName guardianName,
                  GuardianNumber guardianNumber, Availability availability) {
        requireAllNonNull(name, tags, remark, squadName, position, guardianName,
                guardianNumber, availability);
        this.name = name;
        this.tags.addAll(tags);
        this.remark = remark;
        this.squadName = squadName;
        this.position = position;
        this.guardianName = guardianName;
        this.guardianNumber = guardianNumber;
        this.availability = availability;
    }

    public Name getName() {
        return name;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public Remark getRemark() {
        return remark;
    }

    public SquadName getSquadName() {
        return squadName;
    }

    public Position getPosition() {
        return position;
    }

    public GuardianName getGuardianName() {
        return guardianName;
    }

    public GuardianNumber getGuardianNumber() {
        return guardianNumber;
    }

    public Availability getAvailability() {
        return availability;
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && tags.equals(otherPerson.tags)
                && squadName.equals(otherPerson.squadName)
                && position.equals(otherPerson.position)
                && guardianName.equals(otherPerson.guardianName)
                && guardianNumber.equals(otherPerson.guardianNumber)
                && availability.equals(otherPerson.availability);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, tags, squadName, position, guardianName,
                guardianNumber, availability);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("tags", tags)
                .add("squadName", squadName)
                .add("position", position)
                .add("guardianName", guardianName)
                .add("guardianNumber", guardianNumber)
                .add("availability", availability)
                .toString();
    }

}
