package seedu.address.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
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
 * A utility class to help with building EditPersonDescriptor objects.
 */
public class EditPersonDescriptorBuilder {

    private EditPersonDescriptor descriptor;

    public EditPersonDescriptorBuilder() {
        descriptor = new EditPersonDescriptor();
    }

    public EditPersonDescriptorBuilder(EditPersonDescriptor descriptor) {
        this.descriptor = new EditPersonDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPersonDescriptor} with fields containing {@code person}'s details
     */
    public EditPersonDescriptorBuilder(Person person) {
        descriptor = new EditPersonDescriptor();
        descriptor.setName(person.getName());
        descriptor.setTags(person.getTags());
        descriptor.setSquadName(person.getSquadName());
        descriptor.setPosition(person.getPosition());
        descriptor.setGuardianName(person.getGuardianName());
        descriptor.setGuardianContact(person.getGuardianContact());
        descriptor.setAvailability(person.getAvailability());
        descriptor.setInjuryStatus(person.getInjuryStatus());
        descriptor.setFeeStatus(person.getFeeStatus());
        descriptor.setConsentStatus(person.getConsentStatus());
        descriptor.setRemark(person.getRemark());
    }

    /**
     * Sets the {@code Name} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code EditPersonDescriptor}
     * that we are building.
     */
    public EditPersonDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    /**
     * Sets the {@code Availability} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withAvailability(String availability) {
        descriptor.setAvailability(new Availability(availability));
        return this;
    }

    /** Sets the squad name. */
    public EditPersonDescriptorBuilder withSquadName(String squadName) {
        descriptor.setSquadName(new SquadName(squadName));
        return this;
    }

    /** Sets the position. */
    public EditPersonDescriptorBuilder withPosition(String position) {
        descriptor.setPosition(new Position(position));
        return this;
    }

    /** Sets or clears the guardian name. */
    public EditPersonDescriptorBuilder withGuardianName(String guardianName) {
        descriptor.setGuardianName(guardianName == null ? null : new GuardianName(guardianName));
        return this;
    }

    /** Sets or clears the guardian contact. */
    public EditPersonDescriptorBuilder withGuardianContact(String guardianContact) {
        descriptor.setGuardianContact(guardianContact == null ? null : new GuardianContact(guardianContact));
        return this;
    }

    public EditPersonDescriptor build() {
        return descriptor;
    }

    /** Sets the fee payment status. */
    public EditPersonDescriptorBuilder withFeeStatus(String feeStatus) {
        descriptor.setFeeStatus(new FeeStatus(feeStatus));
        return this;
    }

    /** Sets the consent status. */
    public EditPersonDescriptorBuilder withConsentStatus(String consentStatus) {
        descriptor.setConsentStatus(new ConsentStatus(consentStatus));
        return this;
    }

    /** Sets the injury status. */
    public EditPersonDescriptorBuilder withInjuryStatus(String injuryStatus) {
        descriptor.setInjuryStatus(new InjuryStatus(injuryStatus));
        return this;
    }

    /** Sets the remark. An empty string clears the existing remark. */
    public EditPersonDescriptorBuilder withRemark(String remark) {
        descriptor.setRemark(new Remark(remark));
        return this;
    }
}
