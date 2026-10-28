package seedu.address.model.person;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a person matches the supplied filter criteria.
 */
public class MatchesFilterCriteriaPredicate implements Predicate<Person> {
    private final Optional<SquadName> squadName;
    private final Optional<Availability> availability;
    private final Optional<String> injury;
    private final Optional<String> fee;
    private final Optional<String> consent;
    private final Optional<Position> position;
    private final Optional<GuardianName> guardianName;
    private final Optional<GuardianContact> guardianContact;
    private final Optional<seedu.address.model.tag.Tag> tag;
    private final Optional<String> remarkKeyword;

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
        this(squadName, availability, injury, fee, consent, Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty());
    }

    /** Creates a predicate for the supplied optional filtering criteria. */
    public MatchesFilterCriteriaPredicate(
            Optional<SquadName> squadName, Optional<Availability> availability, Optional<String> injury,
            Optional<String> fee, Optional<String> consent, Optional<Position> position,
            Optional<GuardianName> guardianName, Optional<GuardianContact> guardianContact,
            Optional<seedu.address.model.tag.Tag> tag, Optional<String> remarkKeyword) {
        assert squadName != null : "squadName Optional should not be null";
        assert availability != null : "availability Optional should not be null";
        assert injury != null : "injury Optional should not be null";
        assert fee != null : "fee Optional should not be null";
        assert consent != null : "consent Optional should not be null";
        assert position != null && guardianName != null && guardianContact != null : "Profile criteria cannot be null";
        assert tag != null && remarkKeyword != null : "Tag and remark criteria cannot be null";

        this.squadName = squadName;
        this.availability = availability;
        this.injury = injury;
        this.fee = fee;
        this.consent = consent;
        this.position = position;
        this.guardianName = guardianName;
        this.guardianContact = guardianContact;
        this.tag = tag;
        this.remarkKeyword = remarkKeyword;
    }

    @Override
    public boolean test(Person person) {
        assert person != null : "Target person for predicate check cannot be null";
        boolean squadMatches = squadName.map(s -> s.equals(person.getSquadName())).orElse(true);
        boolean availabilityMatches = availability.map(a -> a.equals(person.getAvailability())).orElse(true);
        boolean injuryMatches = injury.map(i -> i.equalsIgnoreCase(person.getInjuryStatus().value)).orElse(true);
        boolean feeMatches = fee.map(f -> f.equalsIgnoreCase(person.getFeeStatus().value)).orElse(true);
        boolean consentMatches = consent.map(c -> c.equalsIgnoreCase(person.getConsentStatus().value)).orElse(true);
        boolean positionMatches = position.map(p -> p.equals(person.getPosition())).orElse(true);
        boolean guardianNameMatches = guardianName.map(g -> g.equals(person.getGuardianName())).orElse(true);
        boolean guardianContactMatches = guardianContact.map(c -> c.equals(person.getGuardianContact())).orElse(true);
        boolean tagMatches = tag.map(person.getTags()::contains).orElse(true);
        boolean remarkMatches = remarkKeyword.map(keyword -> person.getRemark().value.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT))).orElse(true);

        return squadMatches && availabilityMatches && injuryMatches && feeMatches && consentMatches
                && positionMatches && guardianNameMatches && guardianContactMatches && tagMatches && remarkMatches;
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
                && consent.equals(otherPredicate.consent)
                && position.equals(otherPredicate.position)
                && guardianName.equals(otherPredicate.guardianName)
                && guardianContact.equals(otherPredicate.guardianContact)
                && tag.equals(otherPredicate.tag)
                && remarkKeyword.equals(otherPredicate.remarkKeyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(squadName, availability, injury, fee, consent, position, guardianName, guardianContact, tag,
                remarkKeyword);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("squadName", squadName)
                .add("availability", availability)
                .add("position", position)
                .add("guardianName", guardianName)
                .add("guardianContact", guardianContact)
                .add("tag", tag)
                .add("remarkKeyword", remarkKeyword)
                .toString();
    }
}
