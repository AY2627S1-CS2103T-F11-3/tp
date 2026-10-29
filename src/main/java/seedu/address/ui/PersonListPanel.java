package seedu.address.ui;

import java.util.Comparator;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollBar;
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
    private TableView<Person> fixedPersonTableView;
    @FXML
    private Region fixedTableScrollBarSpacer;
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

    private ScrollBar fixedScrollBar;
    private ScrollBar detailScrollBar;
    private ScrollBar detailHorizontalScrollBar;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        configureColumns();
        personTableView.setItems(personList);
        fixedPersonTableView.setItems(personList);
        synchronizeTables();
    }

    private void configureColumns() {
        fixedPersonTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        fixedPersonTableView.prefWidthProperty().bind(indexColumn.widthProperty().add(nameColumn.widthProperty()));
        fixedPersonTableView.minWidthProperty().bind(indexColumn.widthProperty().add(nameColumn.widthProperty()));
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

    /** Keeps the frozen table aligned with the horizontally scrollable detail table. */
    private void synchronizeTables() {
        synchronizeSelection(fixedPersonTableView, personTableView);
        synchronizeSelection(personTableView, fixedPersonTableView);
        fixedPersonTableView.skinProperty().addListener((observable, oldSkin, newSkin) -> scheduleScrollBarBinding());
        personTableView.skinProperty().addListener((observable, oldSkin, newSkin) -> scheduleScrollBarBinding());
        scheduleScrollBarBinding();
    }

    /** Binds the scroll bars after both table skins have been created. */
    private void scheduleScrollBarBinding() {
        Platform.runLater(this::bindVerticalScrollBars);
    }

    private void bindVerticalScrollBars() {
        ScrollBar newFixedScrollBar = findScrollBar(fixedPersonTableView, javafx.geometry.Orientation.VERTICAL);
        ScrollBar newDetailScrollBar = findScrollBar(personTableView, javafx.geometry.Orientation.VERTICAL);
        if (newFixedScrollBar == fixedScrollBar && newDetailScrollBar == detailScrollBar) {
            return;
        }

        if (fixedScrollBar != null && detailScrollBar != null) {
            fixedScrollBar.valueProperty().unbindBidirectional(detailScrollBar.valueProperty());
        }

        fixedScrollBar = newFixedScrollBar;
        detailScrollBar = newDetailScrollBar;
        if (fixedScrollBar != null && detailScrollBar != null) {
            fixedScrollBar.valueProperty().bindBidirectional(detailScrollBar.valueProperty());
        }

        bindScrollBarSpacer(findScrollBar(personTableView, javafx.geometry.Orientation.HORIZONTAL));
    }

    /** Reserves the detail table's horizontal-scrollbar height below the frozen table. */
    private void bindScrollBarSpacer(ScrollBar newDetailHorizontalScrollBar) {
        if (newDetailHorizontalScrollBar == detailHorizontalScrollBar) {
            return;
        }
        fixedTableScrollBarSpacer.minHeightProperty().unbind();
        fixedTableScrollBarSpacer.prefHeightProperty().unbind();
        fixedTableScrollBarSpacer.maxHeightProperty().unbind();
        detailHorizontalScrollBar = newDetailHorizontalScrollBar;
        if (detailHorizontalScrollBar == null) {
            fixedTableScrollBarSpacer.setMinHeight(0);
            fixedTableScrollBarSpacer.setPrefHeight(0);
            fixedTableScrollBarSpacer.setMaxHeight(0);
            return;
        }
        fixedTableScrollBarSpacer.minHeightProperty().bind(detailHorizontalScrollBar.heightProperty());
        fixedTableScrollBarSpacer.prefHeightProperty().bind(detailHorizontalScrollBar.heightProperty());
        fixedTableScrollBarSpacer.maxHeightProperty().bind(detailHorizontalScrollBar.heightProperty());
    }

    private static void synchronizeSelection(TableView<Person> source, TableView<Person> destination) {
        var sourceSelection = source.getSelectionModel();
        sourceSelection.selectedIndexProperty().addListener((observable, oldIndex, newIndex) -> {
            destination.getSelectionModel().select(newIndex.intValue());
        });
    }

    private static ScrollBar findScrollBar(TableView<?> tableView, javafx.geometry.Orientation orientation) {
        return tableView.lookupAll(".scroll-bar").stream()
                .filter(node -> node instanceof ScrollBar)
                .map(node -> (ScrollBar) node)
                .filter(scrollBar -> scrollBar.getOrientation() == orientation)
                .findFirst()
                .orElse(null);
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
