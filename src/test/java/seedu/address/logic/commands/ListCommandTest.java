package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_emptyAddressBook_showsNoPlayersMessage() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        ListCommand command = new ListCommand();

        CommandResult result = command.execute(emptyModel);

        assertEquals(ListCommand.MESSAGE_EMPTY_LIST, result.getFeedbackToUser());
    }

    // TODO: Implement checking for squad name, position and availability
    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        ListCommand listCommand = new ListCommand();
        CommandResult result = listCommand.execute(model);

        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());

        String feedback = result.getFeedbackToUser();
        for (int i = 0; i < model.getFilteredPersonList().size(); i++) {
            Person person = model.getFilteredPersonList().get(i);
            String expectedEntry = (i + 1) + ". " + person.getName().fullName;
            assertTrue(feedback.contains(expectedEntry));
        }
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        ListCommand listCommand = new ListCommand();
        CommandResult result = listCommand.execute(model);

        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());

        String feedback = result.getFeedbackToUser();
        for (int i = 0; i < expectedModel.getFilteredPersonList().size(); i++) {
            Person person = expectedModel.getFilteredPersonList().get(i);
            String expectedEntry = (i + 1) + ". " + person.getName().fullName;
            assertTrue(feedback.contains(expectedEntry));
        }
    }
}
