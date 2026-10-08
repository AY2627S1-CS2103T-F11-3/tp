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
 * Guarantees: required fields are present, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;

    // Data fields
    private final SquadName squadName;
    private final Position position;
    private final GuardianName guardianName;
    private final GuardianContact guardianContact;
    private final Availability availability;
    private final Set<Tag> tags = new HashSet<>();
    private final Remark remark;

    /**
     * Every field except the optional guardian name and contact must be present and not null.
     */
    public Person(Name name, Set<Tag> tags, Remark remark,
                  SquadName squadName, Position position, GuardianName guardianName,
                  GuardianContact guardianContact, Availability availability) {
        requireAllNonNull(name, tags, remark, squadName, position, availability);
        this.name = name;
        this.tags.addAll(tags);
        this.remark = remark;
        this.squadName = squadName;
        this.position = position;
        this.guardianName = guardianName;
        this.guardianContact = guardianContact;
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

    public GuardianContact getGuardianContact() {
        return guardianContact;
    }

    public Availability getAvailability() {
        return availability;
    }

    /**
     * Returns true if both players have the same name, squad and position.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName())
                && otherPerson.getSquadName().equals(getSquadName())
                && otherPerson.getPosition().equals(getPosition());
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
                && Objects.equals(guardianName, otherPerson.guardianName)
                && Objects.equals(guardianContact, otherPerson.guardianContact)
                && availability.equals(otherPerson.availability);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, tags, squadName, position, guardianName,
                guardianContact, availability);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("tags", tags)
                .add("squadName", squadName)
                .add("position", position)
                .add("guardianName", guardianName)
                .add("guardianContact", guardianContact)
                .add("availability", availability)
                .toString();
    }

}
