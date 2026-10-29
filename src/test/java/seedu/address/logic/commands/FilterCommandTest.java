package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PLAYER_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Availability;
import seedu.address.model.person.MatchesFilterCriteriaPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.SquadName;
import seedu.address.testutil.PersonBuilder;

public class FilterCommandTest {
    private Person availableSoccerPlayer;
    private Person unavailableSoccerPlayer;
    private Person injuredPlayer;
    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        availableSoccerPlayer = new PersonBuilder().withName("Alice Player").withSquadName("Soccer Stars")
                .withAvailability(Availability.AVAILABLE)
                .withInjuryStatus("fit")
                .withFeeStatus("paid")
                .withConsentStatus("received")
                .build();
        unavailableSoccerPlayer = new PersonBuilder().withName("Bob Player").withSquadName("Soccer Stars")
                .withAvailability(Availability.UNAVAILABLE)
                .withInjuryStatus("fit")
                .withFeeStatus("unpaid")
                .withConsentStatus("pending")
                .build();
        Person availableFootballPlayer = new PersonBuilder().withName("Cara Player").withSquadName("Football Fellas")
                .withAvailability(Availability.AVAILABLE)
                .withInjuryStatus("fit")
                .withFeeStatus("paid")
                .withConsentStatus("received")
                .build();
        injuredPlayer = new PersonBuilder().withName("Dave Player").withSquadName("Soccer Stars")
                .withAvailability(Availability.UNAVAILABLE)
                .withInjuryStatus("injured")
                .withFeeStatus("unpaid")
                .withConsentStatus("pending")
                .build();

        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(availableSoccerPlayer);
        addressBook.addPerson(unavailableSoccerPlayer);
        addressBook.addPerson(availableFootballPlayer);
        addressBook.addPerson(injuredPlayer);

        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void execute_matchingPlayers_updatesOnlyDisplayedList() {
        MatchesFilterCriteriaPredicate predicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Soccer Stars")),
                Optional.of(new Availability(Availability.AVAILABLE)),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, MESSAGE_PLAYER_LISTED_OVERVIEW, expectedModel);
        assertEquals(List.of(availableSoccerPlayer), model.getFilteredPersonList());
        assertEquals(4, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_matchingStatusCriteria_updatesOnlyDisplayedList() {
        // Filter by injury/injured and fee/unpaid
        MatchesFilterCriteriaPredicate predicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Soccer Stars")),
                Optional.empty(),
                Optional.of("injured"),
                Optional.of("unpaid"),
                Optional.empty());
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, MESSAGE_PLAYER_LISTED_OVERVIEW, expectedModel);
        assertEquals(List.of(injuredPlayer), model.getFilteredPersonList());
    }

    @Test
    public void execute_noMatchingPlayers_showsMessage() {
        MatchesFilterCriteriaPredicate predicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Missing Squad")),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, FilterCommand.MESSAGE_NO_MATCHING_PLAYERS, expectedModel);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void equals() {
        MatchesFilterCriteriaPredicate firstPredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Soccer Stars")),
                Optional.empty(),
                Optional.of("fit"),
                Optional.empty(),
                Optional.empty());
        MatchesFilterCriteriaPredicate secondPredicate = new MatchesFilterCriteriaPredicate(
                Optional.empty(),
                Optional.of(new Availability(Availability.AVAILABLE)),
                Optional.empty(),
                Optional.of("paid"),
                Optional.empty());

        FilterCommand firstCommand = new FilterCommand(firstPredicate);
        FilterCommand secondCommand = new FilterCommand(secondPredicate);

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(new FilterCommand(firstPredicate)));
        assertFalse(firstCommand.equals(secondCommand));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.isAddressBookMutating());
    }
}
