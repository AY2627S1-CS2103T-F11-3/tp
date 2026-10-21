package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONSENT_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANCONTACT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getAddCommand(Person person) {
        return AddCommand.COMMAND_WORD + " " + getPersonDetails(person);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + person.getName().fullName + " ");
        sb.append(PREFIX_SQUADNAME + person.getSquadName().value + " ");
        sb.append(PREFIX_POSITION + person.getPosition().value + " ");
        if (person.getGuardianName() != null) {
            sb.append(PREFIX_GUARDIANNAME + person.getGuardianName().value + " ");
        }
        if (person.getGuardianContact() != null) {
            sb.append(PREFIX_GUARDIANCONTACT + person.getGuardianContact().value + " ");
        }
        sb.append(PREFIX_AVAILABILITY + Boolean.toString(person.getAvailability().value.equals("available")) + " ");
        sb.append(PREFIX_INJURY_FULL).append(person.getInjuryStatus().value).append(" ");
        sb.append(PREFIX_FEE_FULL).append(person.getFeeStatus().value).append(" ");
        sb.append(PREFIX_CONSENT_FULL).append(person.getConsentStatus().value).append(" ");
        person.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPersonDescriptor}'s details.
     */
    public static String getEditPersonDescriptorDetails(EditPersonDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getSquadName().ifPresent(squad ->
                sb.append(PREFIX_SQUADNAME).append(squad.value).append(" "));
        descriptor.getPosition().ifPresent(position ->
                sb.append(PREFIX_POSITION).append(position.value).append(" "));
        if (descriptor.isGuardianNameEdited()) {
            sb.append(PREFIX_GUARDIANNAME).append(descriptor.getGuardianName().map(value -> value.value).orElse(""))
                    .append(" ");
        }
        if (descriptor.isGuardianContactEdited()) {
            String guardianContact = descriptor.getGuardianContact().map(value -> value.value).orElse("");
            sb.append(PREFIX_GUARDIANCONTACT).append(guardianContact).append(" ");
        }
        descriptor.getAvailability().ifPresent(availability -> sb.append(PREFIX_AVAILABILITY)
                .append(availability.value.equals("available")).append(" "));
        descriptor.getInjuryStatus().ifPresent(injury -> sb.append(PREFIX_INJURY_FULL)
                .append(injury.value).append(" "));
        descriptor.getFeeStatus().ifPresent(fee -> sb.append(PREFIX_FEE_FULL).append(fee.value).append(" "));
        descriptor.getConsentStatus().ifPresent(consent -> sb.append(PREFIX_CONSENT_FULL)
                .append(consent.value).append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG);
            } else {
                tags.forEach(s -> sb.append(PREFIX_TAG).append(s.tagName).append(" "));
            }
        }
        return sb.toString();
    }
}
