package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Availability;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests bulk availability updates against the real filtered model.
 */
public class AvailabilityCommandTest {

    private static final Availability AVAILABLE = new Availability(Availability.AVAILABLE);
    private static final Availability UNAVAILABLE = new Availability(Availability.UNAVAILABLE);
    private final Model model = new ModelManager();
    private final Person amy = new PersonBuilder().withName("Amy Tan").withSquadName("U12")
            .withPosition("Defender").withGuardianName("Jane Tan").withGuardianContact("81234567")
            .withTags("captain").withRemark("Left footed").build();
    private final Person ben = new PersonBuilder().withName("Ben Tan").withSquadName("U12").build();
    private final Person cal = new PersonBuilder().withName("Cal Lee").withSquadName("U14").build();

    @Test
    public void execute_multipleTargets_preservesOtherFieldsAndUntargetedPlayers() throws Exception {
        addPlayers();

        CommandResult result = command(UNAVAILABLE, 1, 3).execute(model);

        assertEquals(List.of(unavailable(amy), ben, unavailable(cal)), model.getAddressBook().getPersonList());
        // Person.equals does not compare remarks, so verify this field explicitly.
        assertEquals(amy.getRemark(), model.getAddressBook().getPersonList().getFirst().getRemark());
        assertEquals("Marked 2 players as unavailable:\nAmy Tan\nCal Lee",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_availabilityFilterShrinks_updatesOriginalTargets() throws Exception {
        addPlayers();
        model.updateFilteredPersonList(player -> player.getAvailability().equals(AVAILABLE));

        command(UNAVAILABLE, 1, 2).execute(model);

        assertEquals(List.of(unavailable(amy), unavailable(ben), cal), model.getAddressBook().getPersonList());
        assertEquals(List.of(cal), model.getFilteredPersonList());
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexes() throws Exception {
        addPlayers();
        model.updateFilteredPersonList(player -> player.getSquadName().value.equals("U14"));

        command(UNAVAILABLE, 1).execute(model);

        assertEquals(List.of(amy, ben, unavailable(cal)), model.getAddressBook().getPersonList());
        assertEquals(List.of(unavailable(cal)), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedIndexes_updatesAndReportsEachPlayerOnce() throws Exception {
        addPlayers();

        CommandResult result = command(UNAVAILABLE, 2, 1, 2).execute(model);

        assertEquals(List.of(unavailable(amy), unavailable(ben), cal), model.getAddressBook().getPersonList());
        assertEquals("Marked 2 players as unavailable:\nBen Tan\nAmy Tan",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_laterIndexInvalid_changesNothing() {
        addPlayers();
        assertCommandFailure(command(UNAVAILABLE, 1, 4), model,
                "Index 4 is outside the displayed list. Choose an index from 1 to 3. No players updated.");
    }

    @Test
    public void execute_indexOutsideFilteredList_changesNothing() {
        addPlayers();
        model.updateFilteredPersonList(player -> player.getSquadName().value.equals("U14"));
        assertCommandFailure(command(UNAVAILABLE, 1, 2), model,
                "Index 2 is outside the displayed list. Choose an index from 1 to 1. No players updated.");
    }

    @Test
    public void execute_noDisplayedPlayers_showsRecoveryGuidance() {
        addPlayers();
        model.updateFilteredPersonList(player -> false);
        assertCommandFailure(command(UNAVAILABLE, 1), model, AvailabilityCommand.MESSAGE_NO_DISPLAYED_PLAYERS);
    }

    @Test
    public void execute_availableAndAlreadyAvailablePlayers_success() throws Exception {
        model.addPerson(unavailable(amy));
        model.addPerson(ben);

        CommandResult result = command(AVAILABLE, 1, 2).execute(model);

        assertEquals(List.of(amy, ben), model.getAddressBook().getPersonList());
        assertEquals("Marked 1 player as available:\nAmy Tan\n\n"
                + "Already available (unchanged, 1 player):\nBen Tan", result.getFeedbackToUser());
        assertSame(ben, model.getAddressBook().getPersonList().get(1));
    }

    @Test
    public void execute_mixedUnavailableWithRepeatedIndexes_reportsDistinctGroups() throws Exception {
        model.addPerson(amy);
        model.addPerson(unavailable(ben));
        model.addPerson(unavailable(cal));

        CommandResult result = command(UNAVAILABLE, 2, 1, 3, 2, 1).execute(model);

        assertEquals("Marked 1 player as unavailable:\nAmy Tan\n\n"
                + "Already unavailable (unchanged, 2 players):\nBen Tan\nCal Lee", result.getFeedbackToUser());
        assertEquals(List.of(unavailable(amy), unavailable(ben), unavailable(cal)),
                model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_allAlreadyAvailable_preservesFilteredListAndReportsNoChanges() throws Exception {
        addPlayers();
        model.updateFilteredPersonList(player -> player.getSquadName().value.equals("U12"));

        CommandResult result = command(AVAILABLE, 1, 2, 1).execute(model);

        assertEquals("No changes needed. All 2 selected players are already available.\nAmy Tan\nBen Tan",
                result.getFeedbackToUser());
        assertEquals(List.of(amy, ben), model.getFilteredPersonList());
        assertSame(amy, model.getFilteredPersonList().getFirst());
        assertSame(ben, model.getFilteredPersonList().get(1));
    }

    @Test
    public void execute_allAlreadyUnavailable_reportsNoChanges() throws Exception {
        model.addPerson(unavailable(amy));
        model.addPerson(unavailable(ben));

        CommandResult result = command(UNAVAILABLE, 1, 2).execute(model);

        assertEquals("No changes needed. All 2 selected players are already unavailable.\nAmy Tan\nBen Tan",
                result.getFeedbackToUser());
        assertEquals(List.of(unavailable(amy), unavailable(ben)), model.getFilteredPersonList());
    }

    @Test
    public void execute_singleAlreadyAvailable_reportsSingularNoChanges() throws Exception {
        model.addPerson(amy);

        CommandResult result = command(AVAILABLE, 1, 1).execute(model);

        assertEquals("No changes needed. The selected player is already available.\nAmy Tan",
                result.getFeedbackToUser());
        assertSame(amy, model.getFilteredPersonList().getFirst());
    }

    @Test
    public void execute_allUnavailableToAvailable_reportsChangedPlayers() throws Exception {
        model.addPerson(unavailable(amy));
        model.addPerson(unavailable(ben));

        CommandResult result = command(AVAILABLE, 1, 2).execute(model);

        assertEquals("Marked 2 players as available:\nAmy Tan\nBen Tan", result.getFeedbackToUser());
        assertEquals(List.of(amy, ben), model.getFilteredPersonList());
    }

    @Test
    public void constructor_mutatedInputList_keepsOriginalTargets() throws Exception {
        addPlayers();
        List<Index> indexes = new ArrayList<>(List.of(Index.fromOneBased(1)));
        AvailabilityCommand command = new AvailabilityCommand(indexes, UNAVAILABLE);
        indexes.add(Index.fromOneBased(2));

        command.execute(model);

        assertEquals(List.of(unavailable(amy), ben, cal), model.getAddressBook().getPersonList());
    }

    @Test
    public void constructor_missingTargetsOrAvailability_rejectsInvalidArguments() {
        assertThrows(NullPointerException.class, () -> new AvailabilityCommand(null, AVAILABLE));
        assertThrows(IllegalArgumentException.class, () -> new AvailabilityCommand(List.of(), AVAILABLE));
        List<Index> targets = List.of(Index.fromOneBased(1));
        assertThrows(NullPointerException.class, () -> new AvailabilityCommand(targets, null));
    }

    private void addPlayers() {
        model.addPerson(amy);
        model.addPerson(ben);
        model.addPerson(cal);
    }

    private AvailabilityCommand command(Availability availability, int... indexes) {
        List<Index> targets = new ArrayList<>();
        for (int index : indexes) {
            targets.add(Index.fromOneBased(index));
        }
        return new AvailabilityCommand(targets, availability);
    }

    private Person unavailable(Person player) {
        return new PersonBuilder(player).withAvailability(Availability.UNAVAILABLE).build();
    }
}
