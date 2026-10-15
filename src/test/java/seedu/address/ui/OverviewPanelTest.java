package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.overview.ClubOverview;
import seedu.address.model.overview.SquadSummary;
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises card rendering, narrow layouts, and switching between overview and player views.
 */
public class OverviewPanelTest {
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
    public void render_squadCards_wrapAndShowWarnings() throws Exception {
        onFxThread(() -> {
            ClubOverview overview = new ClubOverview(List.of(new SquadSummary("Football Fellas", 12, 10),
                    new SquadSummary("Soccer Stars", 10, 0), new SquadSummary("Youth United", 8, 8),
                    new SquadSummary("Zulu Juniors with a very long name that must wrap across multiple lines", 5, 3)));
            OverviewPanel panel = new OverviewPanel(overview);
            Scene scene = new Scene(panel, 960, 700);
            scene.getStylesheets().add(getClass().getResource("/view/DarkTheme.css").toExternalForm());
            panel.applyCss();
            panel.layout();
            FlowPane cards = (FlowPane) panel.lookup("#squadCards");
            assertEquals(4, cards.getChildren().size());
            assertEquals(1, panel.lookupAll(".overview-card-warning").size());
            assertEquals(4, panel.lookupAll(".overview-availability-bar").size());
            ProgressBar unavailableBar = (ProgressBar) cards.getChildren().get(1).lookup(".progress-bar");
            assertEquals(0.0, unavailableBar.getProgress());
            Label unavailableCount = (Label) cards.getChildren().get(1).lookup(".overview-unavailable");
            assertNotNull(unavailableCount);
            assertEquals("0 available", unavailableCount.getText());
            assertEquals(Color.web("#d46e6e"), unavailableCount.getTextFill());
            assertNull(cards.getChildren().get(1).lookup(".overview-available"));
            Label availableCount = (Label) cards.getChildren().get(0).lookup(".overview-available");
            assertEquals(Color.web("#6ed489"), availableCount.getTextFill());
            saveSnapshot(panel, "wide");
            panel.resize(450, 700);
            panel.layout();
            for (var card : cards.getChildren()) {
                assertTrue(card.getBoundsInParent().getMaxX() <= cards.getWidth() + 1);
            }
            assertTrue(cards.getChildren().get(1).getLayoutY() > cards.getChildren().get(0).getLayoutY());
            saveSnapshot(panel, "narrow");
        });
    }

    @Test
    public void render_emptyClub_showsGuidanceWithoutButtons() throws Exception {
        onFxThread(() -> {
            OverviewPanel panel = new OverviewPanel(new ClubOverview(List.of()));
            new Scene(panel, 450, 700);
            panel.applyCss();
            panel.layout();
            assertNull(panel.lookup("#squadCards"));
            assertTrue(panel.lookupAll(".button").isEmpty());
            assertTrue(panel.lookupAll(".label").stream()
                    .anyMatch(node -> ((Label) node).getText().equals("No players registered")));
        });
    }

    @Test
    public void commands_overviewAndPlayerCommands_preserveFilterAndSwitchViews() throws Exception {
        onFxThread(() -> {
            ModelManager model = new ModelManager();
            model.addPerson(new PersonBuilder().withName("Alice").withSquadName("Alpha").build());
            model.addPerson(new PersonBuilder().withName("Bob").withSquadName("Beta").build());
            model.updateFilteredPersonList(player -> player.getSquadName().value.equals("Beta"));
            var displayed = List.copyOf(model.getFilteredPersonList());
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, new LogicManager(model, null), Path.of("data/addressbook.json"));
            window.fillInnerParts();
            window.executeCommand("overview");
            assertEquals(displayed, model.getFilteredPersonList());
            stage.getScene().getRoot().applyCss();
            stage.getScene().getRoot().layout();
            var overviewPanel = stage.getScene().lookup(".overview-panel");
            assertNotNull(overviewPanel);
            assertTrue(overviewPanel.lookupAll(".button").isEmpty());
            assertEquals(displayed, model.getFilteredPersonList());
            window.executeCommand("filter sn/Alpha");
            assertNull(stage.getScene().lookup(".overview-panel"));
            assertEquals("Alpha", model.getFilteredPersonList().getFirst().getSquadName().value);
            window.executeCommand("overview");
            window.executeCommand("list");
            assertNull(stage.getScene().lookup(".overview-panel"));
            assertEquals(2, model.getFilteredPersonList().size());
            window.executeCommand("overview");
            window.executeCommand("find Bob");
            assertNull(stage.getScene().lookup(".overview-panel"));
            assertEquals("Bob", model.getFilteredPersonList().getFirst().getName().fullName);
            assertFalse(stage.isShowing());
            stage.close();
        });
    }

    @Test
    public void commands_errorsFromOverview_restorePlayerListAndShowError() throws Exception {
        onFxThread(() -> {
            ModelManager model = new ModelManager();
            model.addPerson(new PersonBuilder().withName("Alice").withSquadName("Alpha").build());
            model.addPerson(new PersonBuilder().withName("Bob").withSquadName("Beta").build());
            model.updateFilteredPersonList(player -> player.getSquadName().value.equals("Beta"));
            var displayed = List.copyOf(model.getFilteredPersonList());
            Stage stage = new Stage();
            try {
                MainWindow window = new MainWindow(stage, new LogicManager(model, null),
                        Path.of("data/addressbook.json"));
                window.fillInnerParts();
                window.executeCommand("overview");
                ParseException parseError = assertThrows(ParseException.class, () ->
                        window.executeCommand("overview invalid"));
                assertNull(stage.getScene().lookup(".overview-panel"));
                assertEquals(displayed, model.getFilteredPersonList());
                TextArea feedback = (TextArea) stage.getScene().lookup("#resultDisplay");
                assertEquals(parseError.getMessage(), feedback.getText());

                window.executeCommand("overview");
                CommandException commandError = assertThrows(CommandException.class, () ->
                        window.executeCommand("delete 2"));
                assertNull(stage.getScene().lookup(".overview-panel"));
                assertEquals(displayed, model.getFilteredPersonList());
                assertEquals(2, model.getAddressBook().getPersonList().size());
                assertEquals(commandError.getMessage(), feedback.getText());
            } finally {
                stage.close();
            }
        });
    }

    @Test
    public void commands_helpAndExit_keepOverviewAndDispatchActions() throws Exception {
        onFxThread(() -> {
            ModelManager model = new ModelManager();
            model.addPerson(new PersonBuilder().build());
            Stage stage = new Stage();
            int[] helpRequests = {0};
            try {
                MainWindow window = new MainWindow(stage, new LogicManager(model, null),
                        Path.of("data/addressbook.json")) {
                    @Override
                    public void handleHelp() {
                        helpRequests[0]++;
                    }
                };
                window.fillInnerParts();
                window.executeCommand("overview");
                var overviewPanel = stage.getScene().lookup(".overview-panel");
                assertNotNull(overviewPanel);
                assertTrue(window.executeCommand("help").isShowHelp());
                assertEquals(1, helpRequests[0]);
                assertSame(overviewPanel, stage.getScene().lookup(".overview-panel"));

                stage.setWidth(760);
                assertTrue(window.executeCommand("exit").isExit());
                assertSame(overviewPanel, stage.getScene().lookup(".overview-panel"));
                assertFalse(stage.isShowing());
                assertEquals(760, model.getGuiSettings().getWindowWidth());
            } finally {
                stage.close();
            }
        });
    }

    private void onFxThread(FxAction action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }

    private void saveSnapshot(OverviewPanel panel, String name) throws Exception {
        WritableImage snapshot = panel.snapshot(null, null);
        BufferedImage image = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        Path path = Path.of("build", "overview-preview-" + name + ".png");
        Files.createDirectories(path.getParent());
        ImageIO.write(image, "png", path.toFile());
    }

    private interface FxAction {
        void run() throws Exception;
    }
}
