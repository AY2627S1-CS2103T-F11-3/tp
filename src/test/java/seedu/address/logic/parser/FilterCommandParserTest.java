package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.Availability;
import seedu.address.model.person.MatchesFilterCriteriaPredicate;
import seedu.address.model.person.Position;
import seedu.address.model.person.SquadName;

public class FilterCommandParserTest {
    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_validCriteria_returnsFilterCommand() {
        assertParseSuccess(parser, "squad/Soccer Stars",
                command("Soccer Stars", null, null, null, null));
        assertParseSuccess(parser, "available/TrUe",
                command(null, Availability.AVAILABLE, null, null, null));
        assertParseSuccess(parser, "available/false squad/Football Fellas",
                command("Football Fellas", Availability.UNAVAILABLE, null, null, null));
        assertParseSuccess(parser, "sn/Soccer Stars av/true",
                command("Soccer Stars", Availability.AVAILABLE, null, null, null));
        assertParseSuccess(parser, "squad/Soccer Stars av/false",
                command("Soccer Stars", Availability.UNAVAILABLE, null, null, null));
        assertParseSuccess(parser, "sn/Soccer Stars available/true",
                command("Soccer Stars", Availability.AVAILABLE, null, null, null));
        assertParseSuccess(parser, "\t squad/ Soccer Stars \t av/ false ",
                command("Soccer Stars", Availability.UNAVAILABLE, null, null, null));

        // Status field combinations
        assertParseSuccess(parser, "injury/fit",
                command(null, null, "fit", null, null));
        assertParseSuccess(parser, "fee/paid",
                command(null, null, null, "paid", null));
        assertParseSuccess(parser, "consent/received",
                command(null, null, null, null, "received"));
        assertParseSuccess(parser, "squad/Soccer Stars injury/injured fee/unpaid consent/pending",
                command("Soccer Stars", null, "injured", "unpaid", "pending"));
        assertParseSuccess(parser, "inj/INJURED f/paid c/received",
                command(null, null, "injured", "paid", "received"));
        assertParseSuccess(parser, "inj/fit fee/unpaid c/pending",
                command(null, null, "fit", "unpaid", "pending"));
        assertParseSuccess(parser, "position/Defender squad/Soccer Stars",
                new FilterCommand(new MatchesFilterCriteriaPredicate(
                        Optional.of(new SquadName("Soccer Stars")), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.of(new Position("Defender")))));
    }

    @Test
    public void parse_squadHyphensAndRepeatedWhitespace_success() {
        assertParseSuccess(parser, "squad/ \tU12-A \n Stars  2 ",
                command("U12-A Stars 2", null, null, null, null));
        assertParseSuccess(parser, "sn/ \tU12-A \n Stars  2 ",
                command("U12-A Stars 2", null, null, null, null));
    }

    @Test
    public void parse_invalidSquadName_throwsParseException() {
        assertParseFailure(parser, "squad/Team@A", SquadName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "sn/Team_A", SquadName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_missingCriteriaOrValue_throwsParseException() {
        assertParseFailure(parser, "", FilterCommandParser.MESSAGE_MISSING_CRITERION);
        assertParseFailure(parser, "   ", FilterCommandParser.MESSAGE_MISSING_CRITERION);
        assertParseFailure(parser, "squad/", FilterCommandParser.MESSAGE_MISSING_SQUAD);
        assertParseFailure(parser, "sn/", FilterCommandParser.MESSAGE_MISSING_SQUAD);
        assertParseFailure(parser, "available/", FilterCommandParser.MESSAGE_MISSING_AVAILABILITY);
        assertParseFailure(parser, "av/", FilterCommandParser.MESSAGE_MISSING_AVAILABILITY);
        assertParseFailure(parser, "injury/", FilterCommandParser.MESSAGE_MISSING_INJURY);
        assertParseFailure(parser, "fee/", FilterCommandParser.MESSAGE_MISSING_FEE);
        assertParseFailure(parser, "consent/", FilterCommandParser.MESSAGE_MISSING_CONSENT);
        assertParseFailure(parser, "inj/", FilterCommandParser.MESSAGE_MISSING_INJURY);
        assertParseFailure(parser, "f/", FilterCommandParser.MESSAGE_MISSING_FEE);
        assertParseFailure(parser, "c/", FilterCommandParser.MESSAGE_MISSING_CONSENT);
        assertParseFailure(parser, "pos/", FilterCommandParser.MESSAGE_MISSING_POSITION);
    }

    @Test
    public void parse_invalidAvailability_throwsParseException() {
        assertParseFailure(parser, "available/available", FilterCommandParser.MESSAGE_INVALID_AVAILABILITY);
        assertParseFailure(parser, "av/yes", FilterCommandParser.MESSAGE_INVALID_AVAILABILITY);
    }

    @Test
    public void parse_invalidPlayerStatus_throwsParseException() {
        assertParseFailure(parser, "injury/recovering", FilterCommandParser.MESSAGE_INVALID_INJURY);
        assertParseFailure(parser, "fee/waived", FilterCommandParser.MESSAGE_INVALID_FEE);
        assertParseFailure(parser, "consent/signed", FilterCommandParser.MESSAGE_INVALID_CONSENT);
        assertParseFailure(parser, "inj/unknown", FilterCommandParser.MESSAGE_INVALID_INJURY);
        assertParseFailure(parser, "f/unknown", FilterCommandParser.MESSAGE_INVALID_FEE);
        assertParseFailure(parser, "c/unknown", FilterCommandParser.MESSAGE_INVALID_CONSENT);
    }

    @Test
    public void parse_duplicateCriteria_throwsParseException() {
        assertParseFailure(parser, "squad/Team A sn/Team B", FilterCommandParser.MESSAGE_DUPLICATE_SQUAD);
        assertParseFailure(parser, "available/true av/false", FilterCommandParser.MESSAGE_DUPLICATE_AVAILABILITY);
        assertParseFailure(parser, "sn/Team A squad/Team B", FilterCommandParser.MESSAGE_DUPLICATE_SQUAD);
        assertParseFailure(parser, "squad/Team A squad/Team B", FilterCommandParser.MESSAGE_DUPLICATE_SQUAD);
        assertParseFailure(parser, "av/true available/false", FilterCommandParser.MESSAGE_DUPLICATE_AVAILABILITY);
        assertParseFailure(parser, "available/true available/false",
                FilterCommandParser.MESSAGE_DUPLICATE_AVAILABILITY);
        assertParseFailure(parser, "injury/fit injury/injured", FilterCommandParser.MESSAGE_DUPLICATE_INJURY);
        assertParseFailure(parser, "fee/paid fee/unpaid", FilterCommandParser.MESSAGE_DUPLICATE_FEE);
        assertParseFailure(parser, "consent/received consent/pending", FilterCommandParser.MESSAGE_DUPLICATE_CONSENT);
        // Repeated shorthand and mixed aliases share the duplicate-criterion partition.
        assertParseFailure(parser, "inj/fit inj/injured", FilterCommandParser.MESSAGE_DUPLICATE_INJURY);
        assertParseFailure(parser, "injury/fit inj/injured", FilterCommandParser.MESSAGE_DUPLICATE_INJURY);
        assertParseFailure(parser, "f/paid fee/unpaid", FilterCommandParser.MESSAGE_DUPLICATE_FEE);
        assertParseFailure(parser, "consent/received c/pending", FilterCommandParser.MESSAGE_DUPLICATE_CONSENT);
        assertParseFailure(parser, "pos/Defender position/Striker", FilterCommandParser.MESSAGE_DUPLICATE_POSITION);
    }

    @Test
    public void parse_unknownPrefixOrPreamble_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "/position Goalkeeper", expectedMessage);
        assertParseFailure(parser, "squad/Team A /position Goalkeeper", expectedMessage);
        assertParseFailure(parser, "/squadTeam", expectedMessage);
        assertParseFailure(parser, "unexpected squad/Team A", expectedMessage);
        assertParseFailure(parser, "/squad Team A", expectedMessage);
        assertParseFailure(parser, "/availability true", expectedMessage);
        assertParseFailure(parser, "availability/true", expectedMessage);
        assertParseFailure(parser, "position/Goalkeeper", expectedMessage);
    }

    private FilterCommand command(String squadName, String availability,
                                  String injury, String fee, String consent) {
        Optional<SquadName> squad = Optional.ofNullable(squadName).map(SquadName::new);
        Optional<Availability> available = Optional.ofNullable(availability).map(Availability::new);
        Optional<String> injuryStatus = Optional.ofNullable(injury);
        Optional<String> feeStatus = Optional.ofNullable(fee);
        Optional<String> consentStatus = Optional.ofNullable(consent);

        return new FilterCommand(new MatchesFilterCriteriaPredicate(
                squad, available, injuryStatus, feeStatus, consentStatus));
    }
}
