package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void invalidDisplayedIndex_usesPlayerTerminology() {
        assertEquals("The player index provided is invalid.", Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void getPlayerListSummary_matchingPlayers_usesPlayerTerminologyAndCorrectPlural() {
        assertEquals("1 player listed!", Messages.getPlayerListSummary(3, 1));
        assertEquals("2 players listed!", Messages.getPlayerListSummary(3, 2));
        assertEquals("3 players listed!", Messages.getPlayerListSummary(3, 3));
    }

    @Test
    public void getPlayerListSummary_emptyResults_distinguishesEmptyClubFromNoMatches() {
        assertEquals("No players registered. Add your first player, for example:\n"
                + "add n/Amy Tan sn/U12 pos/Defender", Messages.getPlayerListSummary(0, 0));
        assertEquals("No matching players found. Change your search or filter, or use list to show all players.",
                Messages.getPlayerListSummary(3, 0));
    }

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
