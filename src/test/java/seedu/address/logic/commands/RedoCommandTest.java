package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class RedoCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_noPreviousStates_throwsCommandException() {
        assertCommandFailure(new RedoCommand(), model, RedoCommand.MESSAGE_NO_COMMAND_TO_REDO);
    }

    @Test
    public void execute_singleRedo_success() {
        // Mutate model, commit, and then undo to create a redoable state
        Person firstPerson = model.getFilteredPersonList().get(0);
        model.deletePerson(firstPerson);
        model.commitAddressBook();
        model.undoAddressBook();

        // expectedModel represents the state after the deletion is redone
        Person firstPersonToDelete = expectedModel.getFilteredPersonList().get(0);
        expectedModel.deletePerson(firstPersonToDelete);
        expectedModel.commitAddressBook();

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_REDO_SUCCESS, expectedModel);
    }

    @Test
    public void execute_multipleRedos_success() {
        // First mutation: delete first person
        Person firstPerson = model.getFilteredPersonList().get(0);
        model.deletePerson(firstPerson);
        model.commitAddressBook();

        // Second mutation: delete second person (now at index 0)
        Person secondPerson = model.getFilteredPersonList().get(0);
        model.deletePerson(secondPerson);
        model.commitAddressBook();

        // Undo twice to revert both deletions
        model.undoAddressBook();
        model.undoAddressBook();

        // Intermediate model representing state after redoing the first deletion only
        Model intermediateModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person firstPersonToDelete = intermediateModel.getFilteredPersonList().get(0);
        intermediateModel.deletePerson(firstPersonToDelete);
        intermediateModel.commitAddressBook();

        // Final model representing state after redoing both deletions
        Model finalExpectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person personToDeleteFirst = finalExpectedModel.getFilteredPersonList().get(0);
        finalExpectedModel.deletePerson(personToDeleteFirst);
        finalExpectedModel.commitAddressBook();
        Person personToDeleteSecond = finalExpectedModel.getFilteredPersonList().get(0);
        finalExpectedModel.deletePerson(personToDeleteSecond);
        finalExpectedModel.commitAddressBook();

        // First redo: restores first deletion -> should match intermediate model
        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_REDO_SUCCESS, intermediateModel);

        // Second redo: restores second deletion -> should match finalExpectedModel
        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_REDO_SUCCESS, finalExpectedModel);

        // Third redo: no more states to restore -> fails
        assertCommandFailure(new RedoCommand(), model, RedoCommand.MESSAGE_NO_COMMAND_TO_REDO);
    }

    @Test
    public void equals() {
        RedoCommand redoFirst = new RedoCommand();
        RedoCommand redoSecond = new RedoCommand();

        // same object -> returns true
        assertEquals(redoFirst, redoFirst);

        // same values -> returns true
        assertEquals(redoFirst, redoSecond);

        // different types -> returns false
        assertNotEquals(1, redoFirst);

        // null -> returns false
        assertNotEquals(null, redoFirst);
    }
}
