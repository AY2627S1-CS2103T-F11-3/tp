package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests returning to the main view without changing player data or list state.
 */
public class MainCommandTest {
    private final MainCommand command = new MainCommand();

    @Test
    public void execute_filteredList_preservesPlayersAndFilter() {
        ModelManager model = new ModelManager();
        var hidden = new PersonBuilder().withName("Alice").withSquadName("Alpha").build();
        var displayed = new PersonBuilder().withName("Bob").withSquadName("Beta").build();
        model.addPerson(hidden);
        model.addPerson(displayed);
        model.updateFilteredPersonList(player -> player.getSquadName().value.equals("Beta"));
        var filteredList = model.getFilteredPersonList();

        assertEquals(new CommandResult(MainCommand.MESSAGE_SUCCESS), command.execute(model));
        assertSame(filteredList, model.getFilteredPersonList());
        assertEquals(List.of(displayed), filteredList);
        assertEquals(List.of(hidden, displayed), model.getAddressBook().getPersonList());
        var moved = new PersonBuilder(hidden).withSquadName("Beta").build();
        model.setPerson(hidden, moved);
        assertEquals(List.of(moved, displayed), filteredList);
    }

    @Test
    public void execute_emptyClub_succeedsRepeatedly() {
        ModelManager model = new ModelManager();
        assertEquals(new CommandResult(MainCommand.MESSAGE_SUCCESS), command.execute(model));
        assertEquals(new CommandResult(MainCommand.MESSAGE_SUCCESS), command.execute(model));
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(List.of(), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_readOnlyCommand_doesNotRequireStorage() throws Exception {
        ModelManager model = new ModelManager();
        assertFalse(command.isAddressBookMutating());
        assertEquals(new CommandResult(MainCommand.MESSAGE_SUCCESS), new LogicManager(model, null).execute("main"));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
}
