package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    // shorthands
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_SQUADNAME = new Prefix("sn/");
    public static final Prefix PREFIX_POSITION = new Prefix("pos/");
    public static final Prefix PREFIX_GUARDIANNAME = new Prefix("g/");
    public static final Prefix PREFIX_GUARDIANCONTACT = new Prefix("gc/");
    public static final Prefix PREFIX_AVAILABILITY = new Prefix("av/");
    public static final Prefix PREFIX_INJURY = new Prefix("inj/");
    public static final Prefix PREFIX_FEE = new Prefix("f/");
    public static final Prefix PREFIX_CONSENT = new Prefix("c/");

    // Aliases
    public static final Prefix PREFIX_NAME_FULL = new Prefix("name/");
    public static final Prefix PREFIX_SQUADNAME_FULL = new Prefix("squad/");
    public static final Prefix PREFIX_POSITION_FULL = new Prefix("position/");
    public static final Prefix PREFIX_GUARDIANNAME_FULL = new Prefix("guardian/");
    public static final Prefix PREFIX_GUARDIANCONTACT_FULL = new Prefix("contact/");
    public static final Prefix PREFIX_AVAILABILITY_FULL = new Prefix("available/");
    public static final Prefix PREFIX_FEE_FULL = new Prefix("fee/");
    public static final Prefix PREFIX_CONSENT_FULL = new Prefix("consent/");
    public static final Prefix PREFIX_INJURY_FULL = new Prefix("injury/");

    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_REMARK = new Prefix("r/");
}
