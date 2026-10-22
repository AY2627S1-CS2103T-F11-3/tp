package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/** Tests empty-list guidance and the suggested recovery commands. */
public class EmptyPlayerListTest {
    private final Model model = new ModelManager();
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void execute_emptyClub_suggestsAddingForListFindAndFilter() throws Exception {
        for (String input : List.of("list", "find Amy", "filter sn/U12")) {
            Command command = parser.parseCommand(input);
            assertFalse(command.isAddressBookMutating());
            assertEquals(Messages.MESSAGE_NO_REGISTERED_PLAYERS, command.execute(model).getFeedbackToUser());
            assertTrue(model.getAddressBook().getPersonList().isEmpty());
        }
        String addExample = Messages.MESSAGE_NO_REGISTERED_PLAYERS.split("\n")[1];
        parser.parseCommand(addExample).execute(model);
        assertEquals("Amy Tan", model.getFilteredPersonList().getFirst().getName().fullName);
    }

    @Test
    public void execute_noMatches_suggestsListAndPreservesExistingPlayers() throws Exception {
        Person amy = new PersonBuilder().withName("Amy Tan").withSquadName("U12").build();
        model.addPerson(amy);
        for (String input : List.of("find Missing", "filter sn/U14")) {
            assertEquals(Messages.MESSAGE_NO_MATCHING_PLAYERS,
                    parser.parseCommand(input).execute(model).getFeedbackToUser());
            assertTrue(model.getFilteredPersonList().isEmpty());
            assertEquals(List.of(amy), model.getAddressBook().getPersonList());
            parser.parseCommand("list").execute(model);
            assertEquals(List.of(amy), model.getFilteredPersonList());
        }
    }

    @Test
    public void execute_deleteLastPlayer_thenListSuggestsAdding() throws Exception {
        model.addPerson(new PersonBuilder().build());
        parser.parseCommand("delete 1").execute(model);
        assertEquals(Messages.MESSAGE_NO_REGISTERED_PLAYERS,
                parser.parseCommand("list").execute(model).getFeedbackToUser());
    }
}
