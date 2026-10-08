package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void formatPlayerDetails_playerWithDistinctFields_includesIdentifyingDetails() {
        Person player = new PersonBuilder().withName("John Doe")
                .withTags("captain")
                .withSquadName("Squad A").withPosition("Defender").withGuardianName("Jane Doe")
                .withGuardianContact("87654321").withAvailability("unavailable").withRemark("Left footed").build();

        String expected = "John Doe; Tags: [captain]"
                + "; Squad: Squad A; Position: Defender; Guardian: Jane Doe; Contact: 87654321"
                + "; Availability: unavailable; Remark: Left footed";

        assertEquals(expected, Messages.formatPlayerDetails(player));
    }
}
