package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void formatPlayerDetails_playerWithDistinctFields_includesIdentifyingDetails() {
        Person player = new PersonBuilder().withName("John Doe").withPhone("81234567")
                .withEmail("john@example.com").withAddress("12 Green Road").withTags("captain")
                .withSquadName("Squad A").withPosition("Defender").withGuardianName("Jane Doe")
                .withGuardianNumber("87654321").withAvailability("unavailable").withRemark("Left footed").build();

        String expected = "John Doe; Phone: 81234567; Email: john@example.com; Address: 12 Green Road; Tags: [captain]"
                + "; Squad: Squad A; Position: Defender; Guardian: Jane Doe; Contact: 87654321"
                + "; Availability: unavailable; Remark: Left footed";

        assertEquals(expected, Messages.formatPlayerDetails(player));
    }
}
