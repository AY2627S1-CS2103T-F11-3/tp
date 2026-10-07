package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_multipleIndices_preservesRemainingOrder() {
        List<Person> originalPersons = List.copyOf(model.getFilteredPersonList());
        DeleteCommand command = new DeleteCommand(List.of(Index.fromOneBased(2), Index.fromOneBased(4)));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(originalPersons.get(1));
        expectedModel.deletePerson(originalPersons.get(3));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS, 2,
                originalPersons.get(1).getName().fullName + ", " + originalPersons.get(3).getName().fullName);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_reverseIndices_deletesOriginalTargets() {
        List<Person> originalPersons = List.copyOf(model.getFilteredPersonList());
        DeleteCommand command = new DeleteCommand(List.of(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(originalPersons.get(0));
        expectedModel.deletePerson(originalPersons.get(1));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS, 2,
                originalPersons.get(1).getName().fullName + ", " + originalPersons.get(0).getName().fullName);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_repeatedIndex_deletesOnce() {
        Person personToDelete = model.getFilteredPersonList().getFirst();
        DeleteCommand command = new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validThenInvalidIndex_deletesNobody() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand command = new DeleteCommand(List.of(INDEX_FIRST_PERSON, invalidIndex));
        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_multipleIndicesFilteredList_deletesDisplayedTargets() throws Exception {
        List<Person> originalPersons = List.copyOf(model.getAddressBook().getPersonList());
        model.updateFilteredPersonList(person -> person.equals(originalPersons.get(1))
                || person.equals(originalPersons.get(3)));

        new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)).execute(model);

        List<Person> expectedPersons = new ArrayList<>(originalPersons);
        expectedPersons.remove(originalPersons.get(1));
        expectedPersons.remove(originalPersons.get(3));
        assertEquals(expectedPersons, model.getAddressBook().getPersonList());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_multipleIndicesWithInvalidFilteredIndex_deletesNobody() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        DeleteCommand command = new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_allIndices_deletesAllPlayers() throws Exception {
        List<Index> indices = IntStream.rangeClosed(1, model.getFilteredPersonList().size())
                .mapToObj(Index::fromOneBased).toList();

        new DeleteCommand(indices).execute(model);

        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        assertCommandFailure(new DeleteCommand(INDEX_FIRST_PERSON), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void constructor_emptyIndices_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new DeleteCommand(List.of()));
    }

    @Test
    public void constructor_mutatedInput_preservesTargets() {
        List<Index> indices = new ArrayList<>(List.of(INDEX_FIRST_PERSON));
        DeleteCommand command = new DeleteCommand(indices);
        indices.add(INDEX_SECOND_PERSON);
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));

        assertEquals(new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)),
                new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)));
        assertFalse(deleteFirstCommand.equals(new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON))));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndices=" + List.of(targetIndex) + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
