package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Availability;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.GuardianNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Position;
import seedu.address.model.person.Remark;
import seedu.address.model.person.SquadName;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {

    public static final Remark EMPTY_REMARK = new Remark("");

    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"),
                getTagSet("friends"), EMPTY_REMARK, new SquadName("U12 A"), new Position("Goalkeeper"),
                new GuardianName("Jamie Yeoh"), new GuardianNumber("81234567"),
                new Availability(Availability.AVAILABLE)),
            new Person(new Name("Bernice Yu"),
                getTagSet("colleagues", "friends"), EMPTY_REMARK, new SquadName("U12 A"),
                new Position("Defender"), new GuardianName("Casey Yu"), new GuardianNumber("82345678"),
                new Availability(Availability.UNAVAILABLE)),
            new Person(new Name("Charlotte Oliveiro"),
                getTagSet("neighbours"), EMPTY_REMARK, new SquadName("U13 B"), new Position("Midfielder"),
                new GuardianName("Morgan Oliveiro"), new GuardianNumber("83456789"),
                new Availability(Availability.AVAILABLE)),
            new Person(new Name("David Li"),
                getTagSet("family"), EMPTY_REMARK, new SquadName("U13 B"), new Position("Forward"),
                new GuardianName("Taylor Li"), new GuardianNumber("84567890"),
                new Availability(Availability.AVAILABLE)),
            new Person(new Name("Irfan Ibrahim"),
                getTagSet("classmates"), EMPTY_REMARK, new SquadName("U14 A"), new Position("Defender"),
                new GuardianName("Aisha Ibrahim"), new GuardianNumber("85678901"),
                new Availability(Availability.UNAVAILABLE)),
            new Person(new Name("Roy Balakrishnan"),
                getTagSet("colleagues"), EMPTY_REMARK, new SquadName("U14 A"), new Position("Midfielder"),
                new GuardianName("Ravi Balakrishnan"), new GuardianNumber("86789012"),
                new Availability(Availability.AVAILABLE))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
