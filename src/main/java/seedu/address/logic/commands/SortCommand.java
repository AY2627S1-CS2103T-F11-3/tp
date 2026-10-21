package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Locale;
import java.util.function.Function;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Sorts the displayed player list by a selected field.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sorts the displayed players by a field.\n"
            + "Parameters: FIELD\n"
            + "Fields: name, squad, position, availability, reset\n"
            + "Example: " + COMMAND_WORD + " squad";
    public static final String MESSAGE_SORT_SUCCESS = "Players sorted by %s.";
    public static final String MESSAGE_RESET_SUCCESS = "Player order reset.";

    private final SortField sortField;

    public SortCommand(SortField sortField) {
        this.sortField = requireNonNull(sortField);
    }

    @Override
    public boolean isAddressBookMutating() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateSortComparator(sortField.getComparator());

        if (sortField == SortField.RESET) {
            return new CommandResult(MESSAGE_RESET_SUCCESS);
        }
        return new CommandResult(String.format(MESSAGE_SORT_SUCCESS, sortField.getFieldName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof SortCommand otherSortCommand
                && sortField == otherSortCommand.sortField);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sortField", sortField)
                .toString();
    }

    /** Fields that can be used to order the displayed player list. */
    public enum SortField {
        NAME("name", person -> person.getName().fullName),
        SQUAD("squad", person -> person.getSquadName().value),
        POSITION("position", person -> person.getPosition().value),
        AVAILABILITY("availability", person -> person.getAvailability().value),
        RESET("reset", null);

        private final String fieldName;
        private final Comparator<Person> comparator;

        SortField(String fieldName, Function<Person, String> valueExtractor) {
            this.fieldName = fieldName;
            comparator = valueExtractor == null ? null : Comparator
                    .comparing(valueExtractor, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER);
        }

        public String getFieldName() {
            return fieldName;
        }

        public Comparator<Person> getComparator() {
            return comparator;
        }

        /** Returns the field corresponding to user input, or {@code null} if the input is unsupported. */
        public static SortField fromUserInput(String input) {
            for (SortField field : values()) {
                if (field.fieldName.equals(input.toLowerCase(Locale.ROOT))) {
                    return field;
                }
            }
            return null;
        }
    }
}
