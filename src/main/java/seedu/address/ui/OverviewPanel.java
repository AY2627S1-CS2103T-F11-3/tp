package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seedu.address.model.overview.ClubOverview;
import seedu.address.model.overview.SquadSummary;

/**
 * Scrollable overview of club totals and squad availability cards.
 */
public class OverviewPanel extends ScrollPane {
    /**
     * Creates an overview snapshot for command-based navigation.
     */
    public OverviewPanel(ClubOverview overview) {
        requireNonNull(overview);
        getStyleClass().add("overview-panel");
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("overview-content");
        setContent(content);

        content.getChildren().add(label("Club overview", "overview-title"));

        FlowPane totals = new FlowPane(12, 12);
        totals.getChildren().addAll(metric(overview.squads().size(), "Squads"),
                metric(overview.getTotalPlayers(), "Players"),
                metric(overview.getAvailablePlayers(), "Available"),
                metric(overview.getUnavailablePlayers(), "Unavailable"));
        content.getChildren().add(totals);

        if (overview.squads().isEmpty()) {
            VBox empty = new VBox(12, label("No players registered", "overview-squad-name"),
                    label("Add your first player to see squad availability.", "overview-muted"),
                    label("Example: add n/Alex sn/Youth United pos/Defender", "overview-muted"));
            empty.getStyleClass().add("overview-card");
            content.getChildren().add(empty);
        } else {
            FlowPane cards = new FlowPane(16, 16);
            cards.setId("squadCards");
            content.getChildren().add(cards);
            for (SquadSummary squad : overview.squads()) {
                VBox card = squadCard(squad);
                // Cards fill the viewport on narrow windows, and wrap in rows on wider windows.
                card.prefWidthProperty().bind(Bindings.when(content.widthProperty().lessThan(616))
                        .then(Bindings.max(0, content.widthProperty().subtract(40))).otherwise(280));
                card.maxWidthProperty().bind(content.widthProperty().subtract(40));
                cards.getChildren().add(card);
            }
        }
    }

    private VBox metric(int count, String name) {
        VBox metric = new VBox(4, label(Integer.toString(count), "overview-metric-value"),
                label(name, "overview-muted"));
        metric.setPrefWidth(125);
        metric.getStyleClass().add("overview-metric");
        return metric;
    }

    private VBox squadCard(SquadSummary squad) {
        VBox card = new VBox(10);
        card.setMinWidth(0);
        card.getStyleClass().add("overview-card");
        card.getChildren().add(label(squad.squadName(), "overview-squad-name"));
        String availabilityStyle = squad.availablePlayers() == 0 ? "overview-unavailable" : "overview-available";
        card.getChildren().add(label(squad.availablePlayers() + " available", availabilityStyle));
        card.getChildren().add(label("of " + squad.totalPlayers() + " players", "overview-muted"));
        ProgressBar bar = new ProgressBar((double) squad.availablePlayers() / squad.totalPlayers());
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setAccessibleText(squad.availablePlayers() + " of " + squad.totalPlayers() + " players available");
        bar.getStyleClass().add("overview-availability-bar");
        card.getChildren().add(bar);
        if (squad.availablePlayers() == 0) {
            card.getStyleClass().add("overview-card-warning");
            card.getChildren().add(label("No players available", "overview-warning"));
        } else {
            card.getChildren().add(label(squad.getUnavailablePlayers() + " unavailable", "overview-muted"));
        }
        return card;
    }

    private Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMinWidth(0);
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
