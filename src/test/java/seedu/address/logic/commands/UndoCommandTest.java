package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class UndoCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_noPreviousStates_throwsCommandException() {
        assertCommandFailure(new UndoCommand(), model, UndoCommand.MESSAGE_NO_COMMAND_TO_UND0);
    }

    @Test
    public void execute_singleUndo_success() {
        // Mutate model and save state
        Person firstPerson = model.getFilteredPersonList().get(0);
        model.deletePerson(firstPerson);
        model.commitAddressBook();

        // expectedModel remains unmutated (initial state)
        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_UNDO_SUCCESS, expectedModel);
    }

    @Test
    public void execute_multipleUndos_success() {
        // First mutation: delete first person
        Person firstPerson = model.getFilteredPersonList().get(0);
        model.deletePerson(firstPerson);
        model.commitAddressBook();

        // Second mutation: delete second person (now at index 0)
        Person secondPerson = model.getFilteredPersonList().get(0);
        model.deletePerson(secondPerson);
        model.commitAddressBook();

        // Intermediate model representing state after the first deletion only
        Model intermediateModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person firstPersonToDelete = intermediateModel.getFilteredPersonList().get(0);
        intermediateModel.deletePerson(firstPersonToDelete);
        intermediateModel.commitAddressBook(); // Keep state pointer aligned (index 1)

        // First undo: reverts second deletion -> should match intermediate model
        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_UNDO_SUCCESS, intermediateModel);

        // Second undo: reverts first deletion -> should match original expectedModel
        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_UNDO_SUCCESS, expectedModel);

        // Third undo: no more states to revert -> fails
        assertCommandFailure(new UndoCommand(), model, UndoCommand.MESSAGE_NO_COMMAND_TO_UND0);
    }
}
