package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class MatchesFilterCriteriaPredicateTest {

    @Test
    public void test_matchesSelectedCriteria_returnsExpectedResult() {
        Person availableSoccerPlayer = new PersonBuilder().withSquadName("Soccer Stars")
                .withAvailability(Availability.AVAILABLE).build();
        Person unavailableSoccerPlayer = new PersonBuilder().withSquadName("Soccer Stars")
                .withAvailability(Availability.UNAVAILABLE).build();
        Person availableFootballPlayer = new PersonBuilder().withSquadName("Football Fellas")
                .withAvailability(Availability.AVAILABLE).build();

        MatchesFilterCriteriaPredicate squadPredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Soccer Stars")), Optional.empty());
        assertTrue(squadPredicate.test(availableSoccerPlayer));
        assertFalse(squadPredicate.test(availableFootballPlayer));

        MatchesFilterCriteriaPredicate availabilityPredicate = new MatchesFilterCriteriaPredicate(Optional.empty(),
                Optional.of(new Availability(Availability.UNAVAILABLE)));
        assertTrue(availabilityPredicate.test(unavailableSoccerPlayer));
        assertFalse(availabilityPredicate.test(availableSoccerPlayer));

        MatchesFilterCriteriaPredicate combinedPredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Soccer Stars")), Optional.of(new Availability(Availability.AVAILABLE)));
        assertTrue(combinedPredicate.test(availableSoccerPlayer));
        assertFalse(combinedPredicate.test(unavailableSoccerPlayer));
        assertFalse(combinedPredicate.test(availableFootballPlayer));
    }

    @Test
    public void test_squadWhitespaceAndCase_returnsExpectedResult() {
        Person player = new PersonBuilder().withSquadName(" Soccer   Stars ").build();
        MatchesFilterCriteriaPredicate normalizedPredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName(" \tSoccer \n Stars ")), Optional.empty());
        MatchesFilterCriteriaPredicate differentCasePredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("soccer stars")), Optional.empty());
        assertTrue(normalizedPredicate.test(player));
        assertFalse(differentCasePredicate.test(player));
    }

    @Test
    public void equals() {
        MatchesFilterCriteriaPredicate firstPredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Soccer Stars")), Optional.empty());
        MatchesFilterCriteriaPredicate samePredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Soccer Stars")), Optional.empty());
        MatchesFilterCriteriaPredicate differentPredicate = new MatchesFilterCriteriaPredicate(Optional.empty(),
                Optional.of(new Availability(Availability.AVAILABLE)));

        assertTrue(firstPredicate.equals(firstPredicate));
        assertTrue(firstPredicate.equals(samePredicate));
        assertFalse(firstPredicate.equals(differentPredicate));
        assertFalse(firstPredicate.equals(null));
        assertFalse(firstPredicate.equals(1));
    }
}
