package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Availability;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests club-wide availability summaries and preservation of model state.
 */
public class OverviewCommandTest {
    private final OverviewCommand command = new OverviewCommand();

    @Test
    public void execute_emptyClub_showsGuidance() {
        Model model = new ModelManager();
        assertCommandSuccess(command, model, OverviewCommand.MESSAGE_EMPTY, new ModelManager());
    }

    @Test
    public void execute_multipleSquads_summarisesAndSortsSquads() {
        Model model = createModel();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        String expected = "Club overview: 3 squads, 4 players.\nAvailable: 2 | Unavailable: 2\n\nSquads:"
                + "\nAlpha: 2 total | 1 available | 1 unavailable"
                + "\nBeta: 1 total | 1 available | 0 unavailable"
                + "\nZulu: 1 total | 0 available | 1 unavailable (No players available)";
        assertCommandSuccess(command, model, expected, expectedModel);
    }

    @Test
    public void execute_filteredList_countsHiddenPlayersAndPreservesFilter() {
        Model model = createModel();
        model.updateFilteredPersonList(player -> player.getSquadName().value.equals("Beta"));
        List<Person> displayedPlayers = List.copyOf(model.getFilteredPersonList());
        List<Person> registeredPlayers = List.copyOf(model.getAddressBook().getPersonList());
        String expected = command.execute(createModel()).getFeedbackToUser();

        assertEquals(expected, command.execute(model).getFeedbackToUser());
        assertEquals(displayedPlayers, model.getFilteredPersonList());
        assertEquals(registeredPlayers, model.getAddressBook().getPersonList());
        Person hiddenPlayer = registeredPlayers.get(0);
        Person movedPlayer = new PersonBuilder(hiddenPlayer).withSquadName("Beta").build();
        model.setPerson(hiddenPlayer, movedPlayer);
        assertEquals(List.of(movedPlayer, displayedPlayers.get(0)), model.getFilteredPersonList());
    }

    @Test
    public void execute_noDisplayedPlayers_stillSummarisesClub() {
        Model model = createModel();
        model.updateFilteredPersonList(player -> false);
        assertEquals(command.execute(createModel()).getFeedbackToUser(), command.execute(model).getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_playerChanges_recalculatesCountsAndRemovesEmptySquads() {
        Model model = new ModelManager();
        Person player = new PersonBuilder().withSquadName("Alpha").build();
        model.addPerson(player);
        assertEquals("Club overview: 1 squad, 1 player.\nAvailable: 1 | Unavailable: 0\n\nSquads:"
                + "\nAlpha: 1 total | 1 available | 0 unavailable", command.execute(model).getFeedbackToUser());

        Person unavailable = new PersonBuilder(player).withAvailability(Availability.UNAVAILABLE).build();
        model.setPerson(player, unavailable);
        assertEquals("Club overview: 1 squad, 1 player.\nAvailable: 0 | Unavailable: 1\n\nSquads:"
                + "\nAlpha: 1 total | 0 available | 1 unavailable (No players available)",
                command.execute(model).getFeedbackToUser());

        model.deletePerson(unavailable);
        assertEquals(OverviewCommand.MESSAGE_EMPTY, command.execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_squadNamesDifferOnlyInCase_keepsSeparateSquads() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Alice").withSquadName("Alpha").build());
        model.addPerson(new PersonBuilder().withName("Bob").withSquadName("alpha").build());
        assertEquals("Club overview: 2 squads, 2 players.\nAvailable: 2 | Unavailable: 0\n\nSquads:"
                + "\nAlpha: 1 total | 1 available | 0 unavailable"
                + "\nalpha: 1 total | 1 available | 0 unavailable", command.execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void isAddressBookMutating_returnsFalse() {
        assertFalse(command.isAddressBookMutating());
    }

    private Model createModel() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(new PersonBuilder().withName("Zoe").withSquadName("Zulu")
                .withAvailability(Availability.UNAVAILABLE).build());
        addressBook.addPerson(new PersonBuilder().withName("Ben").withSquadName("Beta").build());
        addressBook.addPerson(new PersonBuilder().withName("Alice").withSquadName("Alpha").build());
        addressBook.addPerson(new PersonBuilder().withName("Amy").withSquadName("Alpha")
                .withAvailability(Availability.UNAVAILABLE).build());
        return new ModelManager(addressBook, new UserPrefs());
    }
}
