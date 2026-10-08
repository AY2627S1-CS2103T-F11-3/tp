package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

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

    public MatchesFilterCriteriaPredicate(Optional<SquadName> squadName, Optional<Availability> availability) {
        this.squadName = requireNonNull(squadName);
        this.availability = requireNonNull(availability);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        return squadName.map(person.getSquadName()::equals).orElse(true)
                && availability.map(person.getAvailability()::equals).orElse(true);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MatchesFilterCriteriaPredicate otherPredicate)) {
            return false;
        }
        return squadName.equals(otherPredicate.squadName) && availability.equals(otherPredicate.availability);
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
