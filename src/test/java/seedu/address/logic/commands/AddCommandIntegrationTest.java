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
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, validPerson.getName()),
                expectedModel);
    }

    @Test
    public void execute_sameNameWithDifferentSquadOrPosition_success() {
        Person original = new PersonBuilder().withName("John Doe").withSquadName("Stars")
                .withPosition("Goalkeeper").build();
        model.addPerson(original);
        Person anotherSquad = new PersonBuilder(original).withSquadName("Fellas").build();
        Person anotherPosition = new PersonBuilder(original).withPosition("Defender").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(anotherSquad);
        assertCommandSuccess(new AddCommand(anotherSquad), model, "Player John Doe added!", expectedModel);
        expectedModel.addPerson(anotherPosition);
        assertCommandSuccess(new AddCommand(anotherPosition), model, "Player John Doe added!", expectedModel);
    }

    @Test
    public void execute_sameIdentityWithDifferentOptionalFields_rejectsDuplicate() {
        Person original = new PersonBuilder().withName("John Doe").withSquadName("Stars")
                .withPosition("Goalkeeper").build();
        model.addPerson(original);
        Person duplicate = new PersonBuilder(original).withName(" John  Doe ").withSquadName(" Stars ")
                .withPosition("goal-keeper").withGuardianName("Jane Doe").withGuardianContact("81234567")
                .withAvailability("unavailable").withTags("captain").build();
        assertCommandFailure(new AddCommand(duplicate), model, "John Doe is already a registered player!");
    }

    @Test
    public void execute_nameOrSquadWithDifferentCase_success() {
        Person original = new PersonBuilder().withName("John Doe").withSquadName("Stars")
                .withPosition("Goalkeeper").build();
        model.addPerson(original);
        Person differentName = new PersonBuilder(original).withName("john doe").build();
        Person differentSquad = new PersonBuilder(original).withSquadName("stars").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(differentName);
        assertCommandSuccess(new AddCommand(differentName), model, "Player john doe added!", expectedModel);
        expectedModel.addPerson(differentSquad);
        assertCommandSuccess(new AddCommand(differentSquad), model, "Player John Doe added!", expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, personInList.getName()));
    }

}
