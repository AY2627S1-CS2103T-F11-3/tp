package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONSENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONSENT_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANCONTACT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANCONTACT_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANNAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
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
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_SQUADNAME_FULL + "SQUAD_NAME] "
            + "[" + PREFIX_POSITION_FULL + "POSITION] "
            + "[" + PREFIX_GUARDIANNAME_FULL + "GUARDIAN_NAME] "
            + "[" + PREFIX_GUARDIANCONTACT_FULL + "TELEPHONE_NUMBER] "
            + "[" + PREFIX_AVAILABILITY_FULL + "true|false] "
            + "[" + PREFIX_INJURY_FULL + "fit|injured] "
            + "[" + PREFIX_FEE_FULL + "paid|unpaid] "
            + "[" + PREFIX_CONSENT_FULL + "received|pending] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Aliases: " + PREFIX_SQUADNAME + ", " + PREFIX_POSITION + ", " + PREFIX_GUARDIANNAME + ", "
            + PREFIX_GUARDIANCONTACT + ", " + PREFIX_AVAILABILITY + ", " + PREFIX_INJURY + ", "
            + PREFIX_FEE + ", and " + PREFIX_CONSENT + " for their full prefixes\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_POSITION + "Defender " + PREFIX_AVAILABILITY + "false";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Player %1$s updated!";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private static final Logger logger = LogsCenter.getLogger(EditCommand.class);

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            if (editPersonDescriptor.getFeeStatus().isPresent()
                    || editPersonDescriptor.getConsentStatus().isPresent()) {
                logger.fine("Rejected player status edit: displayed index is out of range");
            }
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        if (editPersonDescriptor.getFeeStatus().isPresent() || editPersonDescriptor.getConsentStatus().isPresent()) {
            logger.fine(() -> "Applying player status edit: fee field supplied="
                    + editPersonDescriptor.getFeeStatus().isPresent() + ", consent field supplied="
                    + editPersonDescriptor.getConsentStatus().isPresent());
        }
        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedPerson.getName()));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        Remark updatedRemark = personToEdit.getRemark(); // edit command does not allow editing remarks
        SquadName updatedSquadName = editPersonDescriptor.getSquadName().orElse(personToEdit.getSquadName());
        Position updatedPosition = editPersonDescriptor.getPosition().orElse(personToEdit.getPosition());
        GuardianName updatedGuardianName = editPersonDescriptor.isGuardianNameEdited()
                ? editPersonDescriptor.getGuardianName().orElse(null) : personToEdit.getGuardianName();
        GuardianContact updatedGuardianContact = editPersonDescriptor.isGuardianContactEdited()
                ? editPersonDescriptor.getGuardianContact().orElse(null) : personToEdit.getGuardianContact();
        Availability updatedAvailability =
                editPersonDescriptor.getAvailability().orElse(personToEdit.getAvailability());
        InjuryStatus updatedInjuryStatus =
                editPersonDescriptor.getInjuryStatus().orElse(personToEdit.getInjuryStatus());

        FeeStatus updatedFeeStatus = editPersonDescriptor.getFeeStatus().orElse(personToEdit.getFeeStatus());
        ConsentStatus updatedConsentStatus =
                editPersonDescriptor.getConsentStatus().orElse(personToEdit.getConsentStatus());

        return new Person(updatedName, updatedTags, updatedRemark,
                updatedSquadName, updatedPosition, updatedGuardianName, updatedGuardianContact, updatedAvailability,
                updatedInjuryStatus, updatedFeeStatus, updatedConsentStatus);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Set<Tag> tags;
        private SquadName squadName;
        private Position position;
        private GuardianName guardianName;
        private GuardianContact guardianContact;
        private Availability availability;
        private InjuryStatus injuryStatus;
        private FeeStatus feeStatus;
        private ConsentStatus consentStatus;
        private boolean guardianNameEdited;
        private boolean guardianContactEdited;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setTags(toCopy.tags);
            setSquadName(toCopy.squadName);
            setPosition(toCopy.position);
            if (toCopy.guardianNameEdited) {
                setGuardianName(toCopy.guardianName);
            }
            if (toCopy.guardianContactEdited) {
                setGuardianContact(toCopy.guardianContact);
            }
            setAvailability(toCopy.availability);
            setInjuryStatus(toCopy.injuryStatus);
            setFeeStatus(toCopy.feeStatus);
            setConsentStatus(toCopy.consentStatus);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, tags, squadName, position, availability,
                    injuryStatus, feeStatus, consentStatus)
                    || guardianNameEdited || guardianContactEdited;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        public void setSquadName(SquadName squadName) {
            this.squadName = squadName;
        }

        public Optional<SquadName> getSquadName() {
            return Optional.ofNullable(squadName);
        }

        public void setPosition(Position position) {
            this.position = position;
        }

        public Optional<Position> getPosition() {
            return Optional.ofNullable(position);
        }

        public void setGuardianName(GuardianName guardianName) {
            this.guardianName = guardianName;
            guardianNameEdited = true;
        }

        public Optional<GuardianName> getGuardianName() {
            return Optional.ofNullable(guardianName);
        }

        public boolean isGuardianNameEdited() {
            return guardianNameEdited;
        }

        public void setGuardianContact(GuardianContact guardianContact) {
            this.guardianContact = guardianContact;
            guardianContactEdited = true;
        }

        public Optional<GuardianContact> getGuardianContact() {
            return Optional.ofNullable(guardianContact);
        }

        public boolean isGuardianContactEdited() {
            return guardianContactEdited;
        }

        public void setAvailability(Availability availability) {
            this.availability = availability;
        }

        public Optional<Availability> getAvailability() {
            return Optional.ofNullable(availability);
        }

        public void setFeeStatus(FeeStatus feeStatus) {
            this.feeStatus = feeStatus;
        }

        public Optional<FeeStatus> getFeeStatus() {
            return Optional.ofNullable(feeStatus);
        }

        public void setConsentStatus(ConsentStatus consentStatus) {
            this.consentStatus = consentStatus;
        }

        public Optional<ConsentStatus> getConsentStatus() {
            return Optional.ofNullable(consentStatus);
        }

        public void setInjuryStatus(InjuryStatus injuryStatus) {
            this.injuryStatus = injuryStatus;
        }

        public Optional<InjuryStatus> getInjuryStatus() {
            return Optional.ofNullable(injuryStatus);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && Objects.equals(squadName, otherEditPersonDescriptor.squadName)
                    && Objects.equals(position, otherEditPersonDescriptor.position)
                    && Objects.equals(guardianName, otherEditPersonDescriptor.guardianName)
                    && Objects.equals(guardianContact, otherEditPersonDescriptor.guardianContact)
                    && Objects.equals(availability, otherEditPersonDescriptor.availability)
                    && Objects.equals(injuryStatus, otherEditPersonDescriptor.injuryStatus)
                    && Objects.equals(feeStatus, otherEditPersonDescriptor.feeStatus)
                    && Objects.equals(consentStatus, otherEditPersonDescriptor.consentStatus)
                    && guardianNameEdited == otherEditPersonDescriptor.guardianNameEdited
                    && guardianContactEdited == otherEditPersonDescriptor.guardianContactEdited;
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
                .add("injuryStatus", injuryStatus)
                .add("feeStatus", feeStatus)
                .add("consentStatus", consentStatus)
                    .toString();
        }
    }
}
