package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

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
    public void execute_emptyList_showsNoPlayersMessage() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        Model expectedEmptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        assertCommandSuccess(new ListCommand(), emptyModel, ListCommand.MESSAGE_EMPTY_LIST, expectedEmptyModel);
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        String expectedMessage = getExpectedDisplayList(expectedModel);
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }
    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        String expectedMessage = getExpectedDisplayList(expectedModel);
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    /**
     * Generates the expected feedback message for executing a {@code ListCommand}
     * on the given {@code model}.
     *
     * @param model the expected model state to generate the formatted list from.
     * @return the formatted expected display string.
     */
    private String getExpectedDisplayList(Model model) {
        List<Person> list = model.getFilteredPersonList();
        if (list.isEmpty()) {
            return ListCommand.MESSAGE_EMPTY_LIST;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(i + 1)
                    .append(". ")
                    .append(list.get(i).toListString());
            if (i < list.size() - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
