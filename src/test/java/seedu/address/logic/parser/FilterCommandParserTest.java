package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.Availability;
import seedu.address.model.person.MatchesFilterCriteriaPredicate;
import seedu.address.model.person.SquadName;

public class FilterCommandParserTest {
    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_validCriteria_returnsFilterCommand() {
        assertParseSuccess(parser, "/squad Soccer Stars", command("Soccer Stars", null));
        assertParseSuccess(parser, "/availability TrUe", command(null, Availability.AVAILABLE));
        assertParseSuccess(parser, "/availability false /squad Football Fellas",
                command("Football Fellas", Availability.UNAVAILABLE));
        assertParseSuccess(parser, "sn/Soccer Stars av/true", command("Soccer Stars", Availability.AVAILABLE));
        assertParseSuccess(parser, "/squad Soccer Stars av/false",
                command("Soccer Stars", Availability.UNAVAILABLE));
    }

    @Test
    public void parse_missingCriteriaOrValue_throwsParseException() {
        assertParseFailure(parser, "", FilterCommandParser.MESSAGE_MISSING_CRITERION);
        assertParseFailure(parser, "   ", FilterCommandParser.MESSAGE_MISSING_CRITERION);
        assertParseFailure(parser, "/squad", FilterCommandParser.MESSAGE_MISSING_SQUAD);
        assertParseFailure(parser, "sn/", FilterCommandParser.MESSAGE_MISSING_SQUAD);
        assertParseFailure(parser, "/availability", FilterCommandParser.MESSAGE_MISSING_AVAILABILITY);
        assertParseFailure(parser, "av/", FilterCommandParser.MESSAGE_MISSING_AVAILABILITY);
    }

    @Test
    public void parse_invalidAvailability_throwsParseException() {
        assertParseFailure(parser, "/availability available", FilterCommandParser.MESSAGE_INVALID_AVAILABILITY);
        assertParseFailure(parser, "av/yes", FilterCommandParser.MESSAGE_INVALID_AVAILABILITY);
    }

    @Test
    public void parse_duplicateCriteria_throwsParseException() {
        assertParseFailure(parser, "/squad Team A sn/Team B", FilterCommandParser.MESSAGE_DUPLICATE_SQUAD);
        assertParseFailure(parser, "/availability true av/false",
                FilterCommandParser.MESSAGE_DUPLICATE_AVAILABILITY);
    }

    @Test
    public void parse_unknownPrefixOrPreamble_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "/position Goalkeeper", expectedMessage);
        assertParseFailure(parser, "/squad Team A /position Goalkeeper", expectedMessage);
        assertParseFailure(parser, "/squadTeam", expectedMessage);
        assertParseFailure(parser, "unexpected /squad Team A", expectedMessage);
    }

    private FilterCommand command(String squadName, String availability) {
        Optional<SquadName> squad = squadName == null ? Optional.empty() : Optional.of(new SquadName(squadName));
        Optional<Availability> available = availability == null ? Optional.empty()
                : Optional.of(new Availability(availability));
        return new FilterCommand(new MatchesFilterCriteriaPredicate(squad, available));
    }
}
