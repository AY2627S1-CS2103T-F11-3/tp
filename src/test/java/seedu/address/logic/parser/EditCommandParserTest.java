package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_AVAILABILITY;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_AVAILABILITY_UNAVAILABLE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANCONTACT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIANNAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SQUADNAME_FULL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.ConsentStatus;
import seedu.address.model.person.FeeStatus;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.Name;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;
    private static final String AVAILABILITY_DESC_UNAVAILABLE = " " + PREFIX_AVAILABILITY + "false";

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_statusShorthands_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withInjuryStatus("injured")
                .withFeeStatus("paid").withConsentStatus("received").build();
        assertParseSuccess(parser, "2 inj/INJURED f/paid c/received", new EditCommand(INDEX_SECOND_PERSON, descriptor));
        assertParseSuccess(parser, "2 injury/injured f/paid consent/received",
                new EditCommand(INDEX_SECOND_PERSON, descriptor));
    }

    @Test
    public void parse_statusShorthandsWithInvalidValues_failure() {
        String[] prefixes = {"inj/", "f/", "c/"};
        String[] messages = {InjuryStatus.MESSAGE_CONSTRAINTS, FeeStatus.MESSAGE_CONSTRAINTS,
            ConsentStatus.MESSAGE_CONSTRAINTS};
        for (int i = 0; i < prefixes.length; i++) {
            for (String value : new String[] {"", "unknown"}) {
                assertParseFailure(parser, "2 " + prefixes[i] + value, messages[i]);
            }
        }
    }

    @Test
    public void parse_duplicateStatusAliases_failure() {
        Prefix[][] aliases = {
            {CliSyntax.PREFIX_INJURY, CliSyntax.PREFIX_INJURY_FULL},
            {CliSyntax.PREFIX_FEE, CliSyntax.PREFIX_FEE_FULL},
            {CliSyntax.PREFIX_CONSENT, CliSyntax.PREFIX_CONSENT_FULL}
        };
        String[] values = {"injured", "paid", "received"};
        for (int i = 0; i < aliases.length; i++) {
            Prefix shortPrefix = aliases[i][0];
            Prefix fullPrefix = aliases[i][1];
            String value = values[i];
            assertParseFailure(parser, "2 " + shortPrefix + value + " " + shortPrefix + value,
                    Messages.getErrorMessageForDuplicatePrefixes(shortPrefix));
            assertParseFailure(parser, "2 " + shortPrefix + value + " " + fullPrefix + value,
                    Messages.getErrorMessageForDuplicatePrefixes(shortPrefix, fullPrefix));
            assertParseFailure(parser, "2 " + fullPrefix + value + " " + shortPrefix + value,
                    Messages.getErrorMessageForDuplicatePrefixes(shortPrefix, fullPrefix));
        }
    }

    @Test
    public void parse_injuryStatusAloneAndCombined_success() {
        for (String value : new String[] {"fit", "injured"}) {
            EditCommand expected = new EditCommand(INDEX_SECOND_PERSON,
                    new EditPersonDescriptorBuilder().withInjuryStatus(value).build());
            assertParseSuccess(parser, "2 injury/" + value, expected);
            assertParseSuccess(parser, "2 injury/ \t" + value.toUpperCase(Locale.ROOT) + " \n", expected);
        }
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withInjuryStatus("injured")
                .withName(VALID_NAME_AMY).withAvailability("unavailable").build();
        assertParseSuccess(parser, "2 injury/injured av/false" + NAME_DESC_AMY,
                new EditCommand(INDEX_SECOND_PERSON, descriptor));
    }

    @Test
    public void parse_invalidOrRepeatedInjuryStatus_failure() {
        for (String value : new String[] {"", " \t", "unknown", "un\u212Anown",
            "F\u0130T", "f\u0131t", "\u0130NJURED", "recovering", "true", "in jured"}) {
            assertParseFailure(parser, "2 injury/" + value, InjuryStatus.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, "2 injury/fit injury/injured",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_INJURY_FULL));
        assertParseFailure(parser, "0 injury/injured", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "2147483648 injury/injured", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_TAG_DESC,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = targetIndex.getOneBased() + TAG_DESC_HUSBAND
                + NAME_DESC_AMY + TAG_DESC_FRIEND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_availability_success() {
        Index targetIndex = INDEX_THIRD_PERSON;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withAvailability(VALID_AVAILABILITY_UNAVAILABLE).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, targetIndex.getOneBased() + AVAILABILITY_DESC_UNAVAILABLE, expectedCommand);

        descriptor = new EditPersonDescriptorBuilder().withAvailability("available").build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, targetIndex.getOneBased() + " " + PREFIX_AVAILABILITY_FULL + "TrUe",
                expectedCommand);
    }

    @Test
    public void parse_availabilityWithWhitespaceAndOtherFields_success() {
        Index targetIndex = INDEX_THIRD_PERSON;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withAvailability(VALID_AVAILABILITY_UNAVAILABLE).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, targetIndex.getOneBased() + " " + PREFIX_AVAILABILITY + " f a l s e",
                expectedCommand);

        descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withAvailability("available").withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        String userInput = targetIndex.getOneBased() + TAG_DESC_FRIEND + " " + PREFIX_AVAILABILITY_FULL
                + " t r u e" + NAME_DESC_AMY;
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_playerProfileFields_success() {
        Index targetIndex = INDEX_THIRD_PERSON;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withSquadName("Soccer Stars")
                .withPosition("Defender").withGuardianName("Jane Doe").withGuardianContact("81234567").build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        String userInput = targetIndex.getOneBased() + " " + PREFIX_GUARDIANNAME_FULL + "Jane Doe "
                + PREFIX_POSITION + "Defender " + PREFIX_SQUADNAME_FULL + "Soccer Stars "
                + PREFIX_GUARDIANCONTACT + "8123-4567";
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_emptyOptionalPlayerProfileFields_success() {
        Index targetIndex = INDEX_THIRD_PERSON;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withGuardianName(null).withGuardianContact(null).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, targetIndex.getOneBased() + " " + PREFIX_GUARDIANNAME_FULL
                + " " + PREFIX_GUARDIANCONTACT, expectedCommand);
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_repeatedName_failure() {
        assertParseFailure(parser, "1" + NAME_DESC_AMY + NAME_DESC_AMY,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + NAME_DESC_AMY,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_invalidOrRepeatedAvailability_failure() {
        assertParseFailure(parser, "1 " + PREFIX_AVAILABILITY + "yes", MESSAGE_INVALID_AVAILABILITY);
        assertParseFailure(parser, "1 " + PREFIX_AVAILABILITY, MESSAGE_INVALID_AVAILABILITY);
        assertParseFailure(parser, "1" + AVAILABILITY_DESC_UNAVAILABLE + AVAILABILITY_DESC_UNAVAILABLE,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_AVAILABILITY));
        assertParseFailure(parser, "1" + AVAILABILITY_DESC_UNAVAILABLE + " " + PREFIX_AVAILABILITY_FULL + "true",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_AVAILABILITY, PREFIX_AVAILABILITY_FULL));
    }

    @Test
    public void parse_legacyContactFields_failure() {
        for (String field : new String[] {" p/98765432", " e/john@example.com", " a/John Street"}) {
            assertParseFailure(parser, "1" + field, MESSAGE_INVALID_FORMAT);
            assertParseFailure(parser, "1" + NAME_DESC_AMY + field, Name.MESSAGE_CONSTRAINTS);
        }
    }
}
