package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_FULL;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_nameOnly_success() {
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY, new AddCommand(expectedPerson));
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_AMY, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_nameAndTags_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND).build();
        assertParseSuccess(parser, NAME_DESC_BOB + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                new AddCommand(expectedPerson));
        assertParseSuccess(parser, TAG_DESC_HUSBAND + NAME_DESC_BOB + TAG_DESC_FRIEND,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_fullNamePrefix_success() {
        Person withoutTags = new PersonBuilder(BOB).withTags().build();
        assertParseSuccess(parser, " name/" + VALID_NAME_BOB, new AddCommand(withoutTags));
        assertParseSuccess(parser, " name/  " + VALID_NAME_BOB + "  ", new AddCommand(withoutTags));

        Person withTags = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND).build();
        assertParseSuccess(parser, " name/" + VALID_NAME_BOB + TAG_DESC_FRIEND, new AddCommand(withTags));
        assertParseSuccess(parser, TAG_DESC_FRIEND + " name/" + VALID_NAME_BOB, new AddCommand(withTags));
    }

    @Test
    public void parse_hyphensAndRepeatedWhitespace_success() {
        Person expectedPerson = new PersonBuilder().withName("John-Paul Doe 2").withTags().build();
        assertParseSuccess(parser, " n/ \tJohn-Paul \n Doe  2 ", new AddCommand(expectedPerson));
        assertParseSuccess(parser, " name/ \tJohn-Paul \n Doe  2 ", new AddCommand(expectedPerson));
    }

    @Test
    public void parse_repeatedNamePrefix_failure() {
        String expectedMessage = Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME);
        assertParseFailure(parser, NAME_DESC_AMY + NAME_DESC_BOB, expectedMessage);
        assertParseFailure(parser, NAME_DESC_BOB + NAME_DESC_BOB, expectedMessage);
        assertParseFailure(parser, INVALID_NAME_DESC + NAME_DESC_BOB, expectedMessage);
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_NAME_DESC, expectedMessage);
    }

    @Test
    public void parse_repeatedFullNamePrefix_failure() {
        String expectedMessage = Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME_FULL);
        assertParseFailure(parser, " name/John Doe name/Jane Doe", expectedMessage);
        assertParseFailure(parser, " name/John Doe name/John Doe", expectedMessage);
    }

    @Test
    public void parse_mixedNamePrefixes_failure() {
        String expectedMessage = Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_NAME_FULL);
        assertParseFailure(parser, " n/John Doe name/Jane Doe", expectedMessage);
        assertParseFailure(parser, " name/Jane Doe n/John Doe", expectedMessage);
        assertParseFailure(parser, " n/John Doe name/John Doe", expectedMessage);
        assertParseFailure(parser, " n/ name/John Doe", expectedMessage);
    }

    @Test
    public void parse_missingName_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, TAG_DESC_FRIEND, expectedMessage);
        assertParseFailure(parser, VALID_NAME_BOB, expectedMessage);
    }

    @Test
    public void parse_invalidName_failure() {
        assertParseFailure(parser, INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " name/", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " name/   ", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " name/John@Doe", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidTag_failure() {
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " name/" + VALID_NAME_BOB + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, INVALID_NAME_DESC + INVALID_TAG_DESC, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB, expectedMessage);
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + " name/" + VALID_NAME_BOB, expectedMessage);
    }

    @Test
    public void parse_legacyContactFields_failure() {
        for (String field : new String[] {" p/98765432", " e/john@example.com", " a/John Street"}) {
            assertParseFailure(parser, NAME_DESC_BOB + field, Name.MESSAGE_CONSTRAINTS);
            assertParseFailure(parser, NAME_DESC_BOB + TAG_DESC_FRIEND + field, Tag.MESSAGE_CONSTRAINTS);
        }
    }
}
