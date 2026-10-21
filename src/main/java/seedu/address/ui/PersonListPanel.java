package seedu.address.ui;

import java.util.Comparator;
import java.util.stream.Collectors;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * Panel containing the player table.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";

    @FXML
    private TableView<Person> personTableView;
    @FXML
    private TableColumn<Person, String> indexColumn;
    @FXML
    private TableColumn<Person, String> nameColumn;
    @FXML
    private TableColumn<Person, String> squadColumn;
    @FXML
    private TableColumn<Person, String> positionColumn;
    @FXML
    private TableColumn<Person, String> guardianNameColumn;
    @FXML
    private TableColumn<Person, String> guardianContactColumn;
    @FXML
    private TableColumn<Person, String> availabilityColumn;
    @FXML
    private TableColumn<Person, String> injuryColumn;
    @FXML
    private TableColumn<Person, String> feeColumn;
    @FXML
    private TableColumn<Person, String> consentColumn;
    @FXML
    private TableColumn<Person, String> tagsColumn;
    @FXML
    private TableColumn<Person, String> remarkColumn;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        configureColumns();
        personTableView.setItems(personList);
    }

    private void configureColumns() {
        indexColumn.setCellValueFactory(cell -> stringValue(personTableView.getItems().indexOf(cell.getValue()) + 1));
        nameColumn.setCellValueFactory(cell -> stringValue(cell.getValue().getName().fullName));
        squadColumn.setCellValueFactory(cell -> stringValue(cell.getValue().getSquadName().value));
        positionColumn.setCellValueFactory(cell -> stringValue(cell.getValue().getPosition().value));
        guardianNameColumn.setCellValueFactory(cell -> stringValue(cell.getValue().getGuardianName()));
        guardianContactColumn.setCellValueFactory(cell -> stringValue(cell.getValue().getGuardianContact()));
        availabilityColumn.setCellValueFactory(cell -> stringValue(cell.getValue().getAvailability().value));
        availabilityColumn.setCellFactory(column -> new AvailabilityTableCell());
        injuryColumn.setCellValueFactory(cell -> stringValue(cell.getValue().getInjuryStatus().value));
        feeColumn.setCellValueFactory(cell -> stringValue(cell.getValue().getFeeStatus().value));
        consentColumn.setCellValueFactory(cell -> stringValue(cell.getValue().getConsentStatus().value));
        tagsColumn.setCellValueFactory(cell -> stringValue(cell.getValue().getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .map(tag -> tag.tagName)
                .collect(Collectors.joining(", "))));
        remarkColumn.setCellValueFactory(cell -> stringValue(cell.getValue().getRemark().value));
    }

    private static ReadOnlyStringWrapper stringValue(Object value) {
        return new ReadOnlyStringWrapper(value == null ? "Not provided" : value.toString());
    }

    private static class AvailabilityTableCell extends TableCell<Person, String> {
        @Override
        protected void updateItem(String availability, boolean empty) {
            super.updateItem(availability, empty);
            getStyleClass().removeAll("availability-available", "availability-unavailable");

            if (empty || availability == null) {
                setText(null);
                return;
            }

            setText(availability);
            getStyleClass().add("available".equals(availability)
                    ? "availability-available" : "availability-unavailable");
        }
    }

}
