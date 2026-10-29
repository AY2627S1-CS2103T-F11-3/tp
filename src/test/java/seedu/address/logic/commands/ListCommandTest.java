package seedu.address.logic.commands;

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
    public void execute_emptyList_confirmsClearedFilters() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        Model expectedEmptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        assertCommandSuccess(new ListCommand(), emptyModel, getExpectedDisplayList(expectedEmptyModel), expectedEmptyModel);
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
     * Generates the expected concise feedback message for executing a {@code ListCommand}.
     *
     * @param model the expected model state to generate the formatted list from.
     * @return the formatted expected display string.
     */
    private String getExpectedDisplayList(Model model) {
        int count = model.getFilteredPersonList().size();
        return String.format(ListCommand.MESSAGE_FILTERS_CLEARED, count, count == 1 ? "player" : "players");
    }
}
