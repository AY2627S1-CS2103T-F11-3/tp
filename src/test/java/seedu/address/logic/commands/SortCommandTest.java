package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Availability;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class SortCommandTest {
    private Person adam;
    private Person bella;
    private Person charlie;
    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        adam = new PersonBuilder().withName("Adam Player").withSquadName("Zebras")
                .withPosition("Striker").withAvailability(Availability.UNAVAILABLE).build();
        bella = new PersonBuilder().withName("Bella Player").withSquadName("Alphas")
                .withPosition("Goalkeeper").withAvailability(Availability.AVAILABLE).build();
        charlie = new PersonBuilder().withName("Charlie Player").withSquadName("Mavericks")
                .withPosition("Defender").withAvailability(Availability.AVAILABLE).build();
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(adam);
        addressBook.addPerson(bella);
        addressBook.addPerson(charlie);
        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void execute_sortBySquad_ordersDisplayedPlayersWithoutChangingAddressBook() {
        SortCommand command = new SortCommand(SortCommand.SortField.SQUAD);
        expectedModel.updateSortComparator(SortCommand.SortField.SQUAD.getComparator());

        assertCommandSuccess(command, model, "Players sorted by squad.", expectedModel);
        assertEquals(List.of(bella, charlie, adam), model.getFilteredPersonList());
        assertEquals(List.of(adam, bella, charlie), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_sortAfterFiltering_ordersOnlyMatchingPlayers() {
        model.updateFilteredPersonList(person -> !person.equals(adam));
        expectedModel.updateFilteredPersonList(person -> !person.equals(adam));
        SortCommand command = new SortCommand(SortCommand.SortField.POSITION);
        expectedModel.updateSortComparator(SortCommand.SortField.POSITION.getComparator());

        assertCommandSuccess(command, model, "Players sorted by position.", expectedModel);
        assertEquals(List.of(charlie, bella), model.getFilteredPersonList());
    }

    @Test
    public void execute_reset_restoresStoredPlayerOrder() {
        model.updateSortComparator(SortCommand.SortField.NAME.getComparator());
        expectedModel.updateSortComparator(null);
        SortCommand command = new SortCommand(SortCommand.SortField.RESET);

        assertCommandSuccess(command, model, SortCommand.MESSAGE_RESET_SUCCESS, expectedModel);
        assertEquals(List.of(adam, bella, charlie), model.getFilteredPersonList());
    }

    @Test
    public void equals() {
        SortCommand firstCommand = new SortCommand(SortCommand.SortField.NAME);
        SortCommand secondCommand = new SortCommand(SortCommand.SortField.SQUAD);

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(new SortCommand(SortCommand.SortField.NAME)));
        assertFalse(firstCommand.equals(secondCommand));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.isAddressBookMutating());
    }
}
