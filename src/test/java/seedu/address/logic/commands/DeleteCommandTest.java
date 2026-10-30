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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_invalidArgumentsFilteredList_reportsDisplayedRange() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        DeleteCommand command = DeleteCommand.forInvalidIndices(List.of("a", "testing"));

        assertCommandFailure(command, model,
                "Invalid indices: 'a', 'testing'. Only index 1 is available. No players deleted.");
    }

    @Test
    public void execute_singleInvalidArgument_reportsDisplayedRange() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        DeleteCommand command = DeleteCommand.forInvalidIndices(List.of("0"));

        assertCommandFailure(command, model,
                "Invalid index: '0'. Only index 1 is available. No players deleted.");
    }

    @Test
    public void execute_invalidArgumentsEmptyList_reportsRecovery() {
        showNoPerson(model);
        DeleteCommand command = DeleteCommand.forInvalidIndices(List.of("a", "b", "c"));

        assertCommandFailure(command, model, "Invalid indices: 'a', 'b', 'c'. "
                + DeleteCommand.MESSAGE_NO_DISPLAYED_PLAYERS + " No players deleted.");
    }

    @Test
    public void equals_invalidArguments_comparesAllArguments() {
        DeleteCommand command = DeleteCommand.forInvalidIndices(List.of("a", "b"));
        assertEquals(command, DeleteCommand.forInvalidIndices(List.of("a", "b")));
        assertFalse(command.equals(DeleteCommand.forInvalidIndices(List.of("a", "c"))));
        assertFalse(command.equals(new DeleteCommand(INDEX_FIRST_PERSON)));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format("Deleted player: %s (%s, %s).",
                personToDelete.getName(), personToDelete.getSquadName(), personToDelete.getPosition());

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, String.format(DeleteCommand.MESSAGE_INDEX_OUT_OF_RANGE,
                outOfBoundIndex.getOneBased(), model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format("Deleted player: %s (%s, %s).",
                personToDelete.getName(), personToDelete.getSquadName(), personToDelete.getPosition());

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

        assertCommandFailure(deleteCommand, model, String.format(DeleteCommand.MESSAGE_INDEX_OUT_OF_RANGE,
                outOfBoundIndex.getOneBased(), model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_multipleIndices_preservesRemainingOrder() {
        List<Person> originalPersons = List.copyOf(model.getFilteredPersonList());
        DeleteCommand command = new DeleteCommand(List.of(Index.fromOneBased(2), Index.fromOneBased(4)));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(originalPersons.get(1));
        expectedModel.deletePerson(originalPersons.get(3));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS, 2,
                "1. Benson Meier (Unassigned, Unassigned)\n2. Daniel Meier (Unassigned, Unassigned)");
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
                "1. Benson Meier (Unassigned, Unassigned)\n2. Alice Pauline (Unassigned, Unassigned)");
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_repeatedIndex_deletesOnce() {
        Person personToDelete = model.getFilteredPersonList().getFirst();
        DeleteCommand command = new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        String expectedMessage = String.format("Deleted player: %s (%s, %s).",
                personToDelete.getName(), personToDelete.getSquadName(), personToDelete.getPosition());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validThenInvalidIndex_deletesNobody() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand command = new DeleteCommand(List.of(INDEX_FIRST_PERSON, invalidIndex));
        assertCommandFailure(command, model, String.format(DeleteCommand.MESSAGE_INDEX_OUT_OF_RANGE,
                invalidIndex.getOneBased(), model.getFilteredPersonList().size()));
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
        assertCommandFailure(command, model, String.format(DeleteCommand.MESSAGE_INDEX_OUT_OF_RANGE,
                INDEX_SECOND_PERSON.getOneBased(), model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_allIndices_deletesAllPlayers() throws Exception {
        List<Index> indices = IntStream.rangeClosed(1, model.getFilteredPersonList().size())
                .mapToObj(Index::fromOneBased).toList();

        new DeleteCommand(indices).execute(model);

        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        assertCommandFailure(new DeleteCommand(INDEX_FIRST_PERSON), model,
                DeleteCommand.MESSAGE_NO_DISPLAYED_PLAYERS);
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
    public void execute_multipleNames_deletesRequestedPlayers() {
        List<Person> originalPersons = List.copyOf(model.getAddressBook().getPersonList());
        Person first = originalPersons.get(0);
        Person third = originalPersons.get(2);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(first);
        expectedModel.deletePerson(third);
        DeleteCommand command = DeleteCommand.forNames(List.of(first.getName().fullName, third.getName().fullName));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS, 2,
                "1. Alice Pauline (Unassigned, Unassigned)\n2. Carl Kurz (Unassigned, Unassigned)");
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameWithDifferentCaseAndWhitespace_matchesFullName() {
        Person player = new PersonBuilder().withName("John  Doe").build();
        model.addPerson(player);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(player);

        DeleteCommand command = DeleteCommand.forNames(List.of("  JOHN\tDOE  "));
        String expectedMessage = String.format("Deleted player: %s (%s, %s).",
                player.getName(), player.getSquadName(), player.getPosition());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_repeatedNames_deletesOnce() {
        Person player = new PersonBuilder().withName("John Doe").build();
        model.addPerson(player);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(player);

        DeleteCommand command = DeleteCommand.forNames(List.of("John Doe", "john doe", "John  Doe"));
        String expectedMessage = String.format("Deleted player: %s (%s, %s).",
                player.getName(), player.getSquadName(), player.getPosition());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validThenMissingName_deletesNobody() {
        String validName = model.getAddressBook().getPersonList().getFirst().getName().fullName;
        DeleteCommand command = DeleteCommand.forNames(List.of(validName, "Missing Player"));
        assertCommandFailure(command, model,
                String.format(DeleteCommand.MESSAGE_NAME_NOT_FOUND, "Missing Player") + "\nNo players deleted.");
    }

    @Test
    public void execute_partialName_deletesNobody() {
        model.addPerson(new PersonBuilder().withName("John Doe").build());
        assertCommandFailure(DeleteCommand.forNames(List.of("John")), model,
                String.format(DeleteCommand.MESSAGE_NAME_NOT_FOUND, "John") + "\nNo players deleted.");
    }

    @Test
    public void execute_ambiguousNameAfterValidName_deletesNobody() {
        String validName = model.getAddressBook().getPersonList().getFirst().getName().fullName;
        Person firstMatch = new PersonBuilder().withName("John Doe").build();
        Person secondMatch = new PersonBuilder().withName("john doe").build();
        model.addPerson(firstMatch);
        model.addPerson(secondMatch);
        DeleteCommand command = DeleteCommand.forNames(List.of(validName, "John Doe"));

        assertAmbiguousName(command, "John Doe", List.of(firstMatch, secondMatch));
    }

    @Test
    public void execute_namesDifferOnlyInWhitespace_rejectsDuplicateAndDeletesPlayer() {
        Person firstMatch = new PersonBuilder().withName("John Doe").build();
        Person secondMatch = new PersonBuilder().withName("John  Doe").build();
        model.addPerson(firstMatch);
        assertThrows(DuplicatePersonException.class, () -> model.addPerson(secondMatch));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(firstMatch);
        String expectedMessage = String.format("Deleted player: %s (%s, %s).",
                firstMatch.getName(), firstMatch.getSquadName(), firstMatch.getPosition());
        assertCommandSuccess(DeleteCommand.forNames(List.of("John  Doe")), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_ambiguousNameWithHiddenMatch_allowsImmediateDeletionByIndex() throws Exception {
        Person visiblePlayer = new PersonBuilder().withName("John Doe").build();
        Person hiddenPlayer = new PersonBuilder().withName("john doe").build();
        model.addPerson(visiblePlayer);
        model.addPerson(hiddenPlayer);
        model.updateFilteredPersonList(person -> person.equals(visiblePlayer));

        assertAmbiguousName(DeleteCommand.forNames(List.of("John Doe")), "John Doe",
                List.of(visiblePlayer, hiddenPlayer));
        List<Person> expectedPersons = new ArrayList<>(model.getAddressBook().getPersonList());
        expectedPersons.remove(hiddenPlayer);

        new DeleteCommand(INDEX_SECOND_PERSON).execute(model);

        assertEquals(expectedPersons, model.getAddressBook().getPersonList());
        assertEquals(List.of(visiblePlayer), model.getFilteredPersonList());
    }

    @Test
    public void execute_ambiguousNameAfterSorting_usesDisplayedPlayerOrderInMessage() {
        Person soccerPlayer = new PersonBuilder().withName("John Doe").withSquadName("Soccer Stars").build();
        Person basketballPlayer = new PersonBuilder().withName("john doe").withSquadName("Basketball Bears").build();
        model.addPerson(soccerPlayer);
        model.addPerson(basketballPlayer);
        new SortCommand(SortCommand.SortField.SQUAD).execute(model);

        assertAmbiguousName(DeleteCommand.forNames(List.of("John Doe")), "John Doe",
                List.of(basketballPlayer, soccerPlayer));
    }

    @Test
    public void execute_moreThanTwoMatches_showsEveryMatchInOrder() {
        Person firstMatch = new PersonBuilder().withName("John Doe").withSquadName("Squad A").build();
        Person secondMatch = new PersonBuilder().withName("john doe").withSquadName("Squad B").build();
        Person thirdMatch = new PersonBuilder().withName("JOHN DOE").withPosition("Defender").build();
        model.addPerson(firstMatch);
        model.addPerson(new PersonBuilder().withName("Unrelated Player").build());
        model.addPerson(secondMatch);
        model.addPerson(thirdMatch);
        model.updateFilteredPersonList(person -> false);

        assertAmbiguousName(DeleteCommand.forNames(List.of("John Doe")), "John Doe",
                List.of(firstMatch, secondMatch, thirdMatch));
    }

    @Test
    public void execute_multipleAmbiguousNames_showsAllGroupsInDisplayOrder() {
        Person firstMatch = new PersonBuilder().withName("John Doe").build();
        Person secondMatch = new PersonBuilder().withName("john doe").build();
        model.addPerson(firstMatch);
        model.addPerson(secondMatch);
        Person thirdMatch = new PersonBuilder().withName("Amy Tan").build();
        Person fourthMatch = new PersonBuilder().withName("amy tan").build();
        model.addPerson(thirdMatch);
        model.addPerson(fourthMatch);
        model.updateFilteredPersonList(person -> person.equals(firstMatch));

        assertAmbiguousName(DeleteCommand.forNames(List.of("Amy Tan", "John Doe", "Amy Tan")), "Amy Tan, John Doe",
                List.of(firstMatch, secondMatch, thirdMatch, fourthMatch));
    }

    @Test
    public void execute_multipleMissingNames_reportsAllAndPreservesFilter() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandFailure(DeleteCommand.forNames(List.of("Missing Person", "Unknown Player",
                "missing person", "Unknown  Player")), model,
                "No players found with these full names: \"Missing Person, Unknown Player\".\nNo players deleted.");
    }

    @Test
    public void execute_repeatedMissingName_reportsFirstSpellingOnce() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        DeleteCommand command = DeleteCommand.forNames(List.of("Missing Person", "missing person",
                "  MISSING\tPerson  "));

        assertCommandFailure(command, model,
                "No player found with the full name \"Missing Person\".\nNo players deleted.");
    }

    @Test
    public void execute_repeatedAmbiguousName_reportsFirstSpellingOnce() {
        Person firstMatch = new PersonBuilder().withName("Alex Tan").withSquadName("Squad A").build();
        Person secondMatch = new PersonBuilder().withName("Alex Tan").withSquadName("Squad B").build();
        model.addPerson(firstMatch);
        model.addPerson(secondMatch);
        model.updateFilteredPersonList(person -> person.equals(firstMatch));
        DeleteCommand command = DeleteCommand.forNames(List.of("Alex Tan", "alex tan", "  ALEX\tTan  "));

        assertAmbiguousName(command, "Alex Tan", List.of(firstMatch, secondMatch));
    }

    @Test
    public void execute_missingAndAmbiguousNames_reportsBothRegardlessOfOrder() {
        Person firstMatch = new PersonBuilder().withName("Alex Tan").withSquadName("Squad A").build();
        Person secondMatch = new PersonBuilder().withName("Alex Tan").withSquadName("Squad B").build();
        model.addPerson(firstMatch);
        model.addPerson(secondMatch);
        List<Person> originalPersons = List.copyOf(model.getAddressBook().getPersonList());
        String validName = originalPersons.getFirst().getName().fullName;
        String details = "1. " + Messages.formatPlayerDetails(firstMatch)
                + "\n2. " + Messages.formatPlayerDetails(secondMatch);
        String expected = "No player found with the full name \"Missing Person\".\n"
                + String.format(DeleteCommand.MESSAGE_AMBIGUOUS_NAME, "Alex Tan", details);

        for (List<String> names : List.of(List.of(validName, "Missing Person", "Alex Tan"),
                List.of("Alex Tan", "Missing Person", validName))) {
            model.updateFilteredPersonList(person -> false);
            CommandException exception = assertThrows(CommandException.class, () ->
                    DeleteCommand.forNames(names).execute(model));
            assertEquals(expected, exception.getMessage());
            assertEquals(originalPersons, model.getAddressBook().getPersonList());
            assertEquals(List.of(firstMatch, secondMatch), model.getFilteredPersonList());
        }
    }

    @Test
    public void execute_nameOutsideFilteredList_deletesRegisteredPlayer() throws Exception {
        List<Person> originalPersons = List.copyOf(model.getAddressBook().getPersonList());
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person hiddenPlayer = originalPersons.get(1);

        DeleteCommand.forNames(List.of(hiddenPlayer.getName().fullName)).execute(model);

        List<Person> expectedPersons = new ArrayList<>(originalPersons);
        expectedPersons.remove(hiddenPlayer);
        assertEquals(expectedPersons, model.getAddressBook().getPersonList());
        assertEquals(List.of(originalPersons.getFirst()), model.getFilteredPersonList());
    }

    @Test
    public void execute_numericName_deletesNamedPlayer() throws Exception {
        Person player = new PersonBuilder().withName("17").build();
        model.addPerson(player);
        List<Person> expectedPersons = new ArrayList<>(model.getAddressBook().getPersonList());
        expectedPersons.remove(player);

        DeleteCommand.forNames(List.of("17")).execute(model);

        assertEquals(expectedPersons, model.getAddressBook().getPersonList());
    }

    @Test
    public void forNames_invalidNames_rejectsInput() {
        assertThrows(IllegalArgumentException.class, () -> DeleteCommand.forNames(List.of()));
        assertThrows(IllegalArgumentException.class, () -> DeleteCommand.forNames(List.of(" ")));
        assertThrows(NullPointerException.class, () -> DeleteCommand.forNames(null));
    }

    @Test
    public void forNames_mutatedInput_preservesTargets() {
        List<String> names = new ArrayList<>(List.of("John Doe"));
        DeleteCommand command = DeleteCommand.forNames(names);
        names.add("Amy Tan");
        assertEquals(DeleteCommand.forNames(List.of("John Doe")), command);
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
        assertEquals(DeleteCommand.forNames(List.of("John Doe")), DeleteCommand.forNames(List.of("John Doe")));
        assertFalse(DeleteCommand.forNames(List.of("John Doe")).equals(DeleteCommand.forNames(List.of("Amy Tan"))));
        assertFalse(deleteFirstCommand.equals(DeleteCommand.forNames(List.of("1"))));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName()
                + "{targetIndices=" + List.of(targetIndex) + ", targetNames=[], invalidIndices=[]}";
        assertEquals(expected, deleteCommand.toString());
    }

    private void assertAmbiguousName(DeleteCommand command, String name, List<Person> expectedMatches) {
        List<Person> originalPersons = List.copyOf(model.getAddressBook().getPersonList());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        String details = IntStream.range(0, expectedMatches.size())
                .mapToObj(index -> (index + 1) + ". " + Messages.formatPlayerDetails(expectedMatches.get(index)))
                .collect(Collectors.joining("\n"));
        assertEquals(String.format(DeleteCommand.MESSAGE_AMBIGUOUS_NAME, name, details), exception.getMessage());
        assertEquals(originalPersons, model.getAddressBook().getPersonList());
        assertEquals(expectedMatches, model.getFilteredPersonList());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
