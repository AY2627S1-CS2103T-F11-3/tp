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
import seedu.address.model.person.FeeStatus;
import seedu.address.model.person.Person;
import seedu.address.model.person.PlayerStatusField;
import seedu.address.testutil.PersonBuilder;

/** Tests bulk status updates against the real model and filtered list. */
public class MarkCommandTest {
    private final Model model = new ModelManager();
    private final Person amy = new PersonBuilder().withName("Amy Tan").withSquadName("U12")
            .withPosition("Defender").withGuardianName("Jane Tan").withGuardianContact("81234567")
            .withRemark("Left footed").withTags("captain").build();
    private final Person ben = new PersonBuilder().withName("Ben Lee").withSquadName("U12").build();
    private final Person cal = new PersonBuilder().withName("Cal Lim").withSquadName("U14").build();

    @Test
    public void execute_eachStatus_updatesOnlySelectedFieldAndPlayers() throws Exception {
        assertStatusUpdate(PlayerStatusField.AVAILABILITY, "unavailable",
                new PersonBuilder(amy).withAvailability("unavailable").build());
        assertStatusUpdate(PlayerStatusField.INJURY, "injured",
                new PersonBuilder(amy).withInjuryStatus("injured").build());
        assertStatusUpdate(PlayerStatusField.FEE, "paid",
                new PersonBuilder(amy).withFeeStatus("paid").build());
        assertStatusUpdate(PlayerStatusField.CONSENT, "received",
                new PersonBuilder(amy).withConsentStatus("received").build());
    }

    @Test
    public void execute_availabilityFilterShrinks_updatesOriginalTargets() throws Exception {
        addPlayers();
        model.updateFilteredPersonList(player -> player.getAvailability().value.equals("available"));

        command(PlayerStatusField.AVAILABILITY, "unavailable", 1, 2).execute(model);

        assertEquals(List.of(new PersonBuilder(amy).withAvailability("unavailable").build(),
                new PersonBuilder(ben).withAvailability("unavailable").build(), cal),
                model.getAddressBook().getPersonList());
        assertEquals(List.of(cal), model.getFilteredPersonList());
    }

    @Test
    public void execute_mixedStatusesAndRepeatedIndexes_reportsDistinctGroups() throws Exception {
        model.addPerson(new PersonBuilder(amy).withFeeStatus("paid").build());
        model.addPerson(ben);

        CommandResult result = command(PlayerStatusField.FEE, " PAID ", 2, 1, 2).execute(model);

        assertEquals("Updated fee status: paid for 1 player:\nBen Lee\n\n"
                + "Already recorded fee status: paid (unchanged, 1 player):\nAmy Tan", result.getFeedbackToUser());
        assertEquals("paid", model.getAddressBook().getPersonList().get(1).getFeeStatus().value);
    }

    @Test
    public void execute_allAlreadyMarked_preservesRecordsAndFilter() throws Exception {
        addPlayers();
        model.updateFilteredPersonList(player -> player.getSquadName().value.equals("U12"));

        CommandResult result = command(PlayerStatusField.CONSENT, "pending", 1, 2, 1).execute(model);

        assertEquals("No changes needed. Already recorded consent status: pending (2 players):\nAmy Tan\nBen Lee",
                result.getFeedbackToUser());
        assertEquals(List.of(amy, ben), model.getFilteredPersonList());
        assertSame(amy, model.getFilteredPersonList().getFirst());
        assertSame(ben, model.getFilteredPersonList().get(1));
    }

    @Test
    public void execute_filteredIndex_updatesDisplayedPlayer() throws Exception {
        addPlayers();
        model.updateFilteredPersonList(player -> player.getSquadName().value.equals("U14"));

        command(PlayerStatusField.INJURY, "injured", 1).execute(model);

        Person injuredCal = new PersonBuilder(cal).withInjuryStatus("injured").build();
        assertEquals(List.of(amy, ben, injuredCal), model.getAddressBook().getPersonList());
        assertEquals(List.of(injuredCal), model.getFilteredPersonList());
    }

    @Test
    public void execute_laterIndexOutsideFilteredList_changesNothing() {
        addPlayers();
        model.updateFilteredPersonList(player -> player.getSquadName().value.equals("U12"));
        assertCommandFailure(command(PlayerStatusField.FEE, "paid", 1, 3), model,
                "Index 3 is outside the displayed list. Choose an index from 1 to 2. No players updated.");
    }

    @Test
    public void execute_invalidStatusValue_changesNothing() {
        addPlayers();
        assertCommandFailure(command(PlayerStatusField.FEE, "unknown", 1, 2), model,
                FeeStatus.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void execute_emptyResults_showsRecoveryGuidance() {
        addPlayers();
        model.updateFilteredPersonList(player -> false);
        assertCommandFailure(command(PlayerStatusField.FEE, "paid", 1), model, MarkCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void constructor_mutatedInputList_keepsOriginalSelection() throws Exception {
        addPlayers();
        List<Index> indices = new ArrayList<>(List.of(Index.fromOneBased(1)));
        MarkCommand command = new MarkCommand(indices, PlayerStatusField.FEE, "paid");
        indices.add(Index.fromOneBased(2));

        command.execute(model);

        assertEquals(List.of(new PersonBuilder(amy).withFeeStatus("paid").build(), ben, cal),
                model.getAddressBook().getPersonList());
    }

    @Test
    public void constructor_missingArguments_rejectsInvalidInput() {
        List<Index> indices = List.of(Index.fromOneBased(1));
        assertThrows(NullPointerException.class, () -> new MarkCommand(null, PlayerStatusField.FEE, "paid"));
        assertThrows(IllegalArgumentException.class, () -> new MarkCommand(List.of(), PlayerStatusField.FEE, "paid"));
        assertThrows(NullPointerException.class, () -> new MarkCommand(indices, null, "paid"));
        assertThrows(NullPointerException.class, () -> new MarkCommand(indices, PlayerStatusField.FEE, null));
    }

    private void assertStatusUpdate(PlayerStatusField field, String value, Person expected) throws Exception {
        Model freshModel = new ModelManager();
        freshModel.addPerson(amy);
        freshModel.addPerson(ben);
        CommandResult result = command(field, value, 1).execute(freshModel);
        assertEquals(List.of(expected, ben), freshModel.getAddressBook().getPersonList());
        assertEquals(amy.getRemark(), freshModel.getAddressBook().getPersonList().getFirst().getRemark());
        assertEquals("Updated " + field.getDisplayName() + ": " + value + " for 1 player:\nAmy Tan",
                result.getFeedbackToUser());
    }

    private void addPlayers() {
        model.addPerson(amy);
        model.addPerson(ben);
        model.addPerson(cal);
    }

    private MarkCommand command(PlayerStatusField field, String value, int... indexes) {
        List<Index> indices = new ArrayList<>();
        for (int index : indexes) {
            indices.add(Index.fromOneBased(index));
        }
        return new MarkCommand(indices, field, value);
    }
}
