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
                .withAvailability(Availability.AVAILABLE)
                .withInjuryStatus("fit")
                .withFeeStatus("paid")
                .withConsentStatus("received")
                .build();
        Person unavailableSoccerPlayer = new PersonBuilder().withSquadName("Soccer Stars")
                .withAvailability(Availability.UNAVAILABLE)
                .withInjuryStatus("injured")
                .withFeeStatus("unpaid")
                .withConsentStatus("pending")
                .build();
        Person availableFootballPlayer = new PersonBuilder().withSquadName("Football Fellas")
                .withAvailability(Availability.AVAILABLE)
                .withInjuryStatus("fit")
                .withFeeStatus("paid")
                .withConsentStatus("pending")
                .build();

        // Filter by SquadName only
        MatchesFilterCriteriaPredicate squadPredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Soccer Stars")), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty());
        assertTrue(squadPredicate.test(availableSoccerPlayer));
        assertFalse(squadPredicate.test(availableFootballPlayer));

        // Filter by Availability only
        MatchesFilterCriteriaPredicate availabilityPredicate = new MatchesFilterCriteriaPredicate(
                Optional.empty(), Optional.of(new Availability(Availability.UNAVAILABLE)),
                Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(availabilityPredicate.test(unavailableSoccerPlayer));
        assertFalse(availabilityPredicate.test(availableSoccerPlayer));

        // Filter by Injury only
        MatchesFilterCriteriaPredicate injuryPredicate = new MatchesFilterCriteriaPredicate(
                Optional.empty(), Optional.empty(), Optional.of("injured"),
                Optional.empty(), Optional.empty());
        assertTrue(injuryPredicate.test(unavailableSoccerPlayer));
        assertFalse(injuryPredicate.test(availableSoccerPlayer));

        // Filter by Fee only
        MatchesFilterCriteriaPredicate feePredicate = new MatchesFilterCriteriaPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("unpaid"), Optional.empty());
        assertTrue(feePredicate.test(unavailableSoccerPlayer));
        assertFalse(feePredicate.test(availableSoccerPlayer));

        // Filter by Consent only
        MatchesFilterCriteriaPredicate consentPredicate = new MatchesFilterCriteriaPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.of("pending"));
        assertTrue(consentPredicate.test(unavailableSoccerPlayer));
        assertTrue(consentPredicate.test(availableFootballPlayer));
        assertFalse(consentPredicate.test(availableSoccerPlayer));

        // Filter by Combined Criteria (Squad + Availability + Injury + Fee + Consent)
        MatchesFilterCriteriaPredicate combinedPredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Soccer Stars")),
                Optional.of(new Availability(Availability.AVAILABLE)),
                Optional.of("fit"),
                Optional.of("paid"),
                Optional.of("received"));
        assertTrue(combinedPredicate.test(availableSoccerPlayer));
        assertFalse(combinedPredicate.test(unavailableSoccerPlayer));
        assertFalse(combinedPredicate.test(availableFootballPlayer));
    }

    @Test
    public void test_statusCaseInsensitive_returnsTrue() {
        Person player = new PersonBuilder()
                .withInjuryStatus("injured")
                .withFeeStatus("paid")
                .withConsentStatus("received")
                .build();

        MatchesFilterCriteriaPredicate predicate = new MatchesFilterCriteriaPredicate(
                Optional.empty(), Optional.empty(),
                Optional.of("INJURED"), Optional.of("PAID"), Optional.of("RECEIVED"));

        assertTrue(predicate.test(player));
    }

    @Test
    public void test_playerDetailCriteria_returnsExpectedResult() {
        Person player = new PersonBuilder().withPosition("Defender").withGuardianName("Jane Doe")
                .withGuardianContact("81234567").withTags("captain").withRemark("Reliable left footed player")
                .build();
        MatchesFilterCriteriaPredicate predicate = new MatchesFilterCriteriaPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Position("Defender")), Optional.of(new GuardianName("Jane Doe")),
                Optional.of(new GuardianContact("81234567")), Optional.of(new seedu.address.model.tag.Tag("captain")),
                Optional.of("LEFT FOOTED"));

        assertTrue(predicate.test(player));
        assertFalse(new MatchesFilterCriteriaPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Position("Striker")), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty()).test(player));
    }

    @Test
    public void test_squadWhitespaceAndCase_returnsExpectedResult() {
        Person player = new PersonBuilder().withSquadName(" Soccer   Stars ").build();
        MatchesFilterCriteriaPredicate normalizedPredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName(" \tSoccer \n Stars ")), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());
        MatchesFilterCriteriaPredicate differentCasePredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("soccer stars")), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(normalizedPredicate.test(player));
        assertFalse(differentCasePredicate.test(player));
    }

    @Test
    public void equals() {
        MatchesFilterCriteriaPredicate firstPredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Soccer Stars")), Optional.empty(),
                Optional.of("fit"), Optional.empty(), Optional.empty());
        MatchesFilterCriteriaPredicate samePredicate = new MatchesFilterCriteriaPredicate(
                Optional.of(new SquadName("Soccer Stars")), Optional.empty(),
                Optional.of("fit"), Optional.empty(), Optional.empty());
        MatchesFilterCriteriaPredicate differentPredicate = new MatchesFilterCriteriaPredicate(
                Optional.empty(), Optional.of(new Availability(Availability.AVAILABLE)),
                Optional.empty(), Optional.of("paid"), Optional.empty());

        assertTrue(firstPredicate.equals(firstPredicate));
        assertTrue(firstPredicate.equals(samePredicate));
        assertFalse(firstPredicate.equals(differentPredicate));
        assertFalse(firstPredicate.equals(null));
        assertFalse(firstPredicate.equals(1));
    }
}
