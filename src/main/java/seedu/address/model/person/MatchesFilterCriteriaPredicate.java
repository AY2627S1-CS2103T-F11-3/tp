package seedu.address.model.person;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a person matches the supplied squad and availability criteria.
 */
public class MatchesFilterCriteriaPredicate implements Predicate<Person> {
    private final Optional<SquadName> squadName;
    private final Optional<Availability> availability;
    private final Optional<String> injury;
    private final Optional<String> fee;
    private final Optional<String> consent;

    /**
     * Creates a predicate for the supplied optional criteria.
     *
     * @param availability availability criterion, if supplied
     * @param consent consent criterion, if supplied
     * @param injury injury criterion, if supplied
     * @param fee fee criterion, if supplied
     * @param squadName squad criterion, if supplied
     */
    public MatchesFilterCriteriaPredicate(
            Optional<SquadName> squadName,
            Optional<Availability> availability,
            Optional<String> injury,
            Optional<String> fee,
            Optional<String> consent) {
        this.squadName = squadName;
        this.availability = availability;
        this.injury = injury;
        this.fee = fee;
        this.consent = consent;
    }

    @Override
    public boolean test(Person person) {
        boolean squadMatches = squadName.map(s -> s.equals(person.getSquadName())).orElse(true);
        boolean availabilityMatches = availability.map(a -> a.equals(person.getAvailability())).orElse(true);
        boolean injuryMatches = injury.map(i -> i.equalsIgnoreCase(person.getInjuryStatus().value)).orElse(true);
        boolean feeMatches = fee.map(f -> f.equalsIgnoreCase(person.getFeeStatus().value)).orElse(true);
        boolean consentMatches = consent.map(c -> c.equalsIgnoreCase(person.getConsentStatus().value)).orElse(true);

        return squadMatches && availabilityMatches && injuryMatches && feeMatches && consentMatches;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MatchesFilterCriteriaPredicate otherPredicate)) {
            return false;
        }
        return squadName.equals(otherPredicate.squadName)
                && availability.equals(otherPredicate.availability)
                && injury.equals(otherPredicate.injury)
                && fee.equals(otherPredicate.fee)
                && consent.equals(otherPredicate.consent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(squadName, availability);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("squadName", squadName)
                .add("availability", availability)
                .toString();
    }
}
