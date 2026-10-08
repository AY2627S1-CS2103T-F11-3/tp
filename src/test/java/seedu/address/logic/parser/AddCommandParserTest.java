package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.GuardianContact;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Position;
import seedu.address.model.person.SquadName;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String REQUIRED = " name/John Doe squad/Soccer Stars position/Goalkeeper";
    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_requiredFieldsAndAliases_success() {
        AddCommand expected = new AddCommand(player());
        assertParseSuccess(parser, REQUIRED, expected);
        assertParseSuccess(parser, " n/John Doe sn/Soccer Stars pos/Goalkeeper", expected);
        assertParseSuccess(parser, " pos/goal-keeper n/John Doe squad/Soccer Stars", expected);
        assertParseSuccess(parser, " position/ G O A L - K E E P E R sn/Soccer Stars name/John Doe", expected);
    }

    @Test
    public void parse_namesWithHyphensAndWhitespace_normalizesNames() {
        Person expected = new PersonBuilder().withName("John-Paul Doe 2").withSquadName("U12-A Stars")
                .withPosition("Defender").withGuardianName("Jane-Anne Doe").build();
        assertParseSuccess(parser, " n/ \tJohn-Paul \n Doe  2 sn/ U12-A   Stars pos/defender g/Jane-Anne \t Doe ",
                new AddCommand(expected));
    }

    @Test
    public void parse_optionalFieldsAndAliases_success() {
        Person expected = new PersonBuilder(player()).withGuardianName("Long Ox").withGuardianContact("99999999")
                .withAvailability("unavailable").build();
        assertParseSuccess(parser, REQUIRED + " guardian/Long Ox contact/99999999 available/FALSE",
                new AddCommand(expected));
        assertParseSuccess(parser, " av/f a L s E gc/99-99 99\t99 g/Long Ox" + REQUIRED, new AddCommand(expected));
        assertParseSuccess(parser, REQUIRED + " av/ t R u E ", new AddCommand(player()));
    }

    @Test
    public void parse_guardianFieldsIndependently_success() {
        assertParseSuccess(parser, REQUIRED + " g/Jane Doe",
                new AddCommand(new PersonBuilder(player()).withGuardianName("Jane Doe").build()));
        assertParseSuccess(parser, REQUIRED + " gc/81234567",
                new AddCommand(new PersonBuilder(player()).withGuardianContact("81234567").build()));
    }

    @Test
    public void parse_tags_success() {
        Person expected = new PersonBuilder(player()).withTags("friend", "captain").build();
        assertParseSuccess(parser, " t/captain" + REQUIRED + " t/friend t/friend", new AddCommand(expected));
    }

    @Test
    public void parse_missingRequiredFields_failure() {
        assertParseFailure(parser, "", AddCommandParser.MESSAGE_MISSING_NAME);
        assertParseFailure(parser, " sn/Stars pos/striker", AddCommandParser.MESSAGE_MISSING_NAME);
        assertParseFailure(parser, " n/John Doe pos/striker", AddCommandParser.MESSAGE_MISSING_SQUAD);
        assertParseFailure(parser, " n/John Doe sn/Stars", AddCommandParser.MESSAGE_MISSING_POSITION);
    }

    @Test
    public void parse_invalidOrEmptyValues_failure() {
        assertParseFailure(parser, " n/John@Doe sn/Stars pos/striker", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/ sn/Stars pos/striker", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/John sn/ pos/striker", SquadName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/John sn/Stars! pos/striker", SquadName.MESSAGE_CONSTRAINTS);
        for (String position : new String[] {"", "Unassigned", "Forward", "Left Striker", "striker,defender"}) {
            assertParseFailure(parser, " n/John sn/Stars pos/" + position, Position.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, REQUIRED + " g/", GuardianName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, REQUIRED + " guardian/Jane@Doe", GuardianName.MESSAGE_CONSTRAINTS);
        for (String contact : new String[] {"", "000", "1234567", "123456789", "1234abcd", "+81234567"}) {
            assertParseFailure(parser, REQUIRED + " contact/" + contact, GuardianContact.MESSAGE_CONSTRAINTS);
        }
        for (String availability : new String[] {"", "yes", "available", "true-false"}) {
            assertParseFailure(parser, REQUIRED + " available/" + availability,
                    AddCommandParser.MESSAGE_INVALID_AVAILABILITY);
        }
    }

    @Test
    public void parse_duplicateAliases_failure() {
        Prefix[][] aliases = {
            {CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_NAME_FULL},
            {CliSyntax.PREFIX_SQUADNAME, CliSyntax.PREFIX_SQUADNAME_FULL},
            {CliSyntax.PREFIX_POSITION, CliSyntax.PREFIX_POSITION_FULL},
            {CliSyntax.PREFIX_GUARDIANNAME, CliSyntax.PREFIX_GUARDIANNAME_FULL},
            {CliSyntax.PREFIX_GUARDIANCONTACT, CliSyntax.PREFIX_GUARDIANCONTACT_FULL},
            {CliSyntax.PREFIX_AVAILABILITY, CliSyntax.PREFIX_AVAILABILITY_FULL}
        };
        String[] values = {"John Doe", "Soccer Stars", "Goalkeeper", "Jane Doe", "81234567", "true"};
        String[] bases = {" squad/Stars position/striker", " name/John position/striker",
            " name/John squad/Stars", REQUIRED, REQUIRED, REQUIRED};
        for (int index = 0; index < aliases.length; index++) {
            String message = Messages.getErrorMessageForDuplicatePrefixes(aliases[index]);
            for (Prefix first : aliases[index]) {
                for (Prefix second : aliases[index]) {
                    String input = bases[index] + " " + first + values[index] + " " + second + values[index];
                    assertParseFailure(parser, input, message);
                }
            }
        }
    }

    @Test
    public void parse_invalidValues_reportsFirstSuppliedField() {
        assertParseFailure(parser, " available/no contact/abc" + REQUIRED,
                AddCommandParser.MESSAGE_INVALID_AVAILABILITY);
        assertParseFailure(parser, " contact/abc available/no" + REQUIRED, GuardianContact.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " pos/invalid sn/Stars! n/John@Doe", Position.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " sn/Stars! pos/invalid n/John@Doe", SquadName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_unknownPrefixOrPreamble_failure() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "John Doe squad/Stars position/striker", message);
        for (String prefix : new String[] {"p/81234567", "e/john@example.com", "a/Street", "unknown/value",
            "/squad Stars"}) {
            assertParseFailure(parser, REQUIRED + " " + prefix, message);
        }
    }

    private Person player() {
        return new PersonBuilder().withName("John Doe").withSquadName("Soccer Stars")
                .withPosition("Goalkeeper").build();
    }
}
