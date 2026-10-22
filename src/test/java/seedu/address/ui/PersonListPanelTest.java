package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/** Exercises player status display alongside filtered indexes and table updates. */
public class PersonListPanelTest {
    @Test
    public void render_emptyList_showsRecoveryHintAcrossListChanges() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            var source = FXCollections.<Person>observableArrayList();
            FilteredList<Person> displayed = new FilteredList<>(source);
            Region root = new PersonListPanel(displayed).getRoot();
            new Scene(root, 450, 350);
            root.resize(450, 350);
            root.applyCss();
            root.layout();
            TableView<?> table = (TableView<?>) root.lookup("#personTableView");
            Label hint = (Label) table.getPlaceholder();
            assertTrue(hint.getText().contains("Use list"));
            assertTrue(hint.getText().contains("add n/Amy Tan sn/U12 pos/Defender"));
            assertTrue(hint.isWrapText());
            assertTrue(table.lookup(".placeholder").isVisible());
            assertTrue(hint.getWidth() <= table.getWidth());

            source.add(new PersonBuilder().build());
            root.layout();
            assertFalse(table.lookup(".placeholder").isVisible());
            displayed.setPredicate(player -> false);
            root.layout();
            assertTrue(table.lookup(".placeholder").isVisible());
            displayed.setPredicate(player -> true);
            root.layout();
            assertFalse(table.lookup(".placeholder").isVisible());
            source.clear();
            root.layout();
            assertTrue(table.lookup(".placeholder").isVisible());
            return null;
        });
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }

    @BeforeAll
    public static void startJavaFx() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });
        try {
            Platform.startup(startup);
        } catch (IllegalStateException e) {
            Platform.runLater(startup);
        }
        startup.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void render_feeAndConsentStatuses_updatesDisplayedCellsAndRetainsFilteredIndexes() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            Person defaults = new PersonBuilder().withName("Default Player").build();
            Person recorded = new PersonBuilder().withName("Recorded Player").withFeeStatus("paid")
                    .withConsentStatus("received").withInjuryStatus("injured").build();
            Person pending = new PersonBuilder().withName("Pending Player").withFeeStatus("unpaid")
                    .withConsentStatus("pending").build();
            var source = FXCollections.observableArrayList(defaults, recorded, pending);
            FilteredList<Person> displayed = new FilteredList<>(source);
            Region root = new PersonListPanel(displayed).getRoot();
            new Scene(root, 1900, 300);
            root.resize(1900, 300);
            root.applyCss();
            root.layout();
            TableView<?> table = (TableView<?>) root.lookup("#personTableView");
            var feeColumn = table.getColumns().stream().filter(column -> "Fee payment".equals(column.getText()))
                    .findFirst().orElseThrow();
            var consentColumn = table.getColumns().stream().filter(column -> "Consent".equals(column.getText()))
                    .findFirst().orElseThrow();
            assertFalse(feeColumn.isSortable());
            assertFalse(consentColumn.isSortable());
            assertEquals(List.of("unpaid", "paid", "unpaid"),
                    List.of(feeColumn.getCellData(0), feeColumn.getCellData(1), feeColumn.getCellData(2)));
            assertEquals(List.of("pending", "received", "pending"),
                    List.of(consentColumn.getCellData(0), consentColumn.getCellData(1), consentColumn.getCellData(2)));
            assertTrue(table.lookupAll(".table-cell").stream().anyMatch(node -> node instanceof TableCell<?, ?> cell
                    && cell.getTableColumn() == consentColumn && "received".equals(cell.getText())));
            displayed.setPredicate(player -> player.getName().equals(recorded.getName()));
            root.layout();
            assertEquals("1", table.getColumns().getFirst().getCellData(0));
            assertEquals("paid", feeColumn.getCellData(0));
            source.set(1, new PersonBuilder(recorded).withFeeStatus("unpaid").withConsentStatus("pending").build());
            root.layout();
            assertEquals("unpaid", feeColumn.getCellData(0));
            assertEquals("pending", consentColumn.getCellData(0));
            root.resize(450, 300);
            table.scrollToColumnIndex(table.getColumns().indexOf(consentColumn));
            root.layout();
            assertTrue(consentColumn.isVisible());
            assertTrue(table.getWidth() <= 450);
            displayed.setPredicate(player -> false);
            root.layout();
            assertTrue(table.getItems().isEmpty());
            return null;
        });
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }

    @Test
    public void render_injuryStatuses_preservesIndexesAndUpdatesWithPlayers() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            Person defaultFit = new PersonBuilder().withName("Default Player").build();
            Person fit = new PersonBuilder().withName("Fit Player").withInjuryStatus("fit").build();
            Person injured = new PersonBuilder().withName("Injured Player").withInjuryStatus("injured").build();
            var source = FXCollections.observableArrayList(defaultFit, fit, injured);
            FilteredList<Person> displayed = new FilteredList<>(source);
            Region root = new PersonListPanel(displayed).getRoot();
            new Scene(root, 1600, 300);
            root.resize(1600, 300);
            root.applyCss();
            root.layout();
            TableView<?> table = (TableView<?>) root.lookup("#personTableView");
            var injuryColumn = table.getColumns().stream().filter(column -> "Injury".equals(column.getText()))
                    .findFirst().orElseThrow();
            assertFalse(injuryColumn.isSortable());
            assertEquals(List.of("fit", "fit", "injured"),
                    List.of(injuryColumn.getCellData(0), injuryColumn.getCellData(1), injuryColumn.getCellData(2)));
            assertTrue(table.lookupAll(".table-cell").stream().anyMatch(node -> node instanceof TableCell<?, ?> cell
                    && cell.getTableColumn() == injuryColumn && "injured".equals(cell.getText())));

            displayed.setPredicate(player -> player.equals(injured));
            root.layout();
            assertEquals("1", table.getColumns().getFirst().getCellData(0));
            assertEquals("injured", injuryColumn.getCellData(0));
            source.set(2, new PersonBuilder(injured).withInjuryStatus("fit").build());
            displayed.setPredicate(player -> true);
            root.layout();
            assertEquals("fit", injuryColumn.getCellData(2));
            assertEquals("3", table.getColumns().getFirst().getCellData(2));

            root.resize(450, 300);
            table.scrollToColumnIndex(table.getColumns().indexOf(injuryColumn));
            root.layout();
            assertTrue(injuryColumn.isVisible());
            assertTrue(table.getWidth() <= 450);
            source.clear();
            root.layout();
            assertTrue(table.getItems().isEmpty());
            return null;
        });
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }
}
