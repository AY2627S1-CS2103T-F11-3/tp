package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    // shorthands
    public static final Prefix PREFIX_NAME = new Prefix("n/");

    // Aliases
    public static final Prefix PREFIX_NAME_FULL = new Prefix("name/");

    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_REMARK = new Prefix("r/");
}
