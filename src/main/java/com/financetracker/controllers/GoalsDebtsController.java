package com.financetracker.controllers;

import com.financetracker.models.Goal;
import com.financetracker.models.User;
import com.financetracker.services.AuthService;
import com.financetracker.services.GoalsService;
import com.financetracker.utils.SceneManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;

import java.util.List;

/**
 * Контроллер для экрана целей.
 */
public class GoalsDebtsController {

    // Top bar
    @FXML private TextField searchField;

    // User profile
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;

    // Content
    @FXML private GridPane goalsGrid;

    // Menu items
    @FXML private Button dashboardMenuItem;
    @FXML private Button operationsMenuItem;
    @FXML private Button categoriesMenuItem;
    @FXML private Button plansLimitsMenuItem;
    @FXML private Button goalsDebtsMenuItem;
    @FXML private Button settingsMenuItem;

    private final AuthService authService;
    private final GoalsService goalsService;

    public GoalsDebtsController() {
        this.authService = AuthService.getInstance();
        this.goalsService = GoalsService.getInstance();
    }

    @FXML
    public void initialize() {
        loadUserInfo();
        loadGoals();
    }

    private void loadUserInfo() {
        User user = authService.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getName());
            userEmailLabel.setText(user.getEmail());
        }
    }

    private void loadGoals() {
        goalsGrid.getChildren().clear();

        List<Goal> goals = goalsService.getAllGoals();

        int columns = 2;
        int row = 0;
        int col = 0;

        for (Goal goal : goals) {
            VBox card = createGoalCard(goal);
            goalsGrid.add(card, col, row);
            GridPane.setHgrow(card, Priority.ALWAYS);

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createGoalCard(Goal goal) {
        VBox card = new VBox(18);
        card.getStyleClass().add("goal-card");

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(goal.getIcon());
        iconLabel.getStyleClass().add("goal-icon");

        VBox titleBox = new VBox(4);
        Label nameLabel = new Label(goal.getName());
        nameLabel.getStyleClass().add("goal-title");

        HBox daysBox = new HBox(6);
        daysBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView clockIcon = new FontAwesomeIconView();
        clockIcon.setGlyphName("CLOCK_ALT");
        clockIcon.setSize("12");
        clockIcon.getStyleClass().add("goal-days-icon");

        Label daysLabel = new Label(goal.getDaysRemaining() + " дней осталось");
        daysLabel.getStyleClass().add("goal-days");
        daysBox.getChildren().addAll(clockIcon, daysLabel);

        titleBox.getChildren().addAll(nameLabel, daysBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editButton = createIconActionButton("PENCIL_SQUARE", "Редактирование цели");
        Button deleteButton = createIconActionButton("TRASH", "Удаление цели");
        deleteButton.getStyleClass().add("goal-delete-button");

        header.getChildren().addAll(iconLabel, titleBox, spacer, editButton, deleteButton);

        StackPane progressPane = createSegmentedProgress(goal);
        VBox.setVgrow(progressPane, Priority.NEVER);

        HBox metricsRow = new HBox(50);
        metricsRow.setAlignment(Pos.CENTER_LEFT);

        VBox savedBox = new VBox(4);
        Label savedCaption = new Label("Накоплено");
        savedCaption.getStyleClass().add("goal-metric-caption");
        Label savedValue = new Label(formatCurrency(goal.getCurrentAmount()));
        savedValue.getStyleClass().addAll("goal-metric-value", "goal-metric-saved");
        savedBox.getChildren().addAll(savedCaption, savedValue);

        VBox remainingBox = new VBox(4);
        Label remainingCaption = new Label("Осталось");
        remainingCaption.getStyleClass().add("goal-metric-caption");
        Label remainingValue = new Label(formatCurrency(goal.getRemaining()));
        remainingValue.getStyleClass().add("goal-metric-value");
        remainingBox.getChildren().addAll(remainingCaption, remainingValue);

        metricsRow.getChildren().addAll(savedBox, remainingBox);

        Separator separator = new Separator();

        VBox targetBox = new VBox(4);
        Label targetCaption = new Label("Целевая сумма");
        targetCaption.getStyleClass().add("goal-metric-caption");
        Label targetValue = new Label(formatCurrency(goal.getTargetAmount()));
        targetValue.getStyleClass().addAll("goal-metric-value", "goal-metric-target");
        targetBox.getChildren().addAll(targetCaption, targetValue);

        Button topUpButton = new Button("Пополнить");
        topUpButton.getStyleClass().add("goal-topup-button");
        topUpButton.setStyle("-fx-background-color: linear-gradient(to right, " + goal.getColor() + ", " + lightenColor(goal.getColor()) + ");");
        topUpButton.setMaxWidth(Double.MAX_VALUE);
        topUpButton.setOnAction(e -> showComingSoon("Пополнение цели"));

        card.getChildren().addAll(header, progressPane, metricsRow, separator, targetBox, topUpButton);
        return card;
    }

    /**
     * Кольцевой segmented-progress (4 сегмента) с динамическим заполнением по проценту цели.
     */
    private StackPane createSegmentedProgress(Goal goal) {
        StackPane wrapper = new StackPane();
        wrapper.setPrefSize(220, 220);

        Pane ringPane = new Pane();
        ringPane.setPrefSize(170, 170);

        double progress = Math.max(0.0, Math.min(goal.getProgress(), 100.0)) / 100.0;

        double center = 85;
        double radius = 67;
        double strokeWidth = 14;
        double segmentLength = 72; // 4 * 72 = 288 degrees
        double gap = 18;           // 4 * 18 = 72 degrees

        for (int i = 0; i < 4; i++) {
            double startAngle = 90 - (i * (segmentLength + gap));

            Arc track = new Arc(center, center, radius, radius, startAngle, -segmentLength);
            track.setFill(null);
            track.setStrokeWidth(strokeWidth);
            track.setStrokeLineCap(StrokeLineCap.ROUND);
            track.setType(ArcType.OPEN);
            track.getStyleClass().add("goal-ring-track");

            double segmentStart = i * 0.25;
            double segmentProgress = (progress - segmentStart) / 0.25;
            segmentProgress = Math.max(0.0, Math.min(segmentProgress, 1.0));

            Arc fill = new Arc(center, center, radius, radius, startAngle, -(segmentLength * segmentProgress));
            fill.setFill(null);
            fill.setStrokeWidth(strokeWidth);
            fill.setStrokeLineCap(StrokeLineCap.ROUND);
            fill.setType(ArcType.OPEN);
            fill.setStyle("-fx-stroke: " + goal.getColor() + ";");

            ringPane.getChildren().addAll(track, fill);
        }

        Circle innerCircle = new Circle(center, center, 45);
        innerCircle.getStyleClass().add("goal-ring-inner");
        ringPane.getChildren().add(innerCircle);

        VBox centerContent = new VBox(4);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setMouseTransparent(true);

        Label progressLabel = new Label(String.format("%.0f%%", Math.min(goal.getProgress(), 100.0)));
        progressLabel.getStyleClass().add("goal-progress-value");
        progressLabel.setStyle("-fx-text-fill: " + goal.getColor() + ";");

        Label progressText = new Label("достигнуто");
        progressText.getStyleClass().add("goal-progress-caption");

        centerContent.getChildren().addAll(progressLabel, progressText);

        wrapper.getChildren().addAll(ringPane, centerContent);
        return wrapper;
    }

    private Button createIconActionButton(String glyphName, String feature) {
        Button button = new Button();
        button.getStyleClass().add("goal-action-button");

        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setGlyphName(glyphName);
        icon.setSize("14");
        icon.getStyleClass().add("goal-action-icon");

        button.setGraphic(icon);
        button.setOnAction(e -> showComingSoon(feature));
        return button;
    }

    private String lightenColor(String baseHex) {
        if ("#3B82F6".equalsIgnoreCase(baseHex)) {
            return "#2563EB";
        }
        if ("#8B5CF6".equalsIgnoreCase(baseHex)) {
            return "#7C3AED";
        }
        return baseHex;
    }

    private String formatCurrency(double amount) {
        return String.format("₽ %,.0f", amount);
    }

    // ========== ACTIONS ==========

    @FXML
    private void handleAddGoal() {
        showComingSoon("Добавление цели");
    }

    @FXML
    private void handleAddOperation() {
        showComingSoon("Добавление операции");
    }

    @FXML
    private void handleNotifications() {
        showComingSoon("Уведомления");
    }

    @FXML
    private void handleProfile() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Профиль");
        alert.setHeaderText(null);
        alert.setContentText("Выйти из аккаунта?");

        ButtonType logoutButton = new ButtonType("Выйти");
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(logoutButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == logoutButton) {
                handleLogout();
            }
        });
    }

    // ========== MENU NAVIGATION ==========

    @FXML
    private void handleDashboardClick() {
        SceneManager.switchScene("dashboard");
    }

    @FXML
    private void handleOperationsClick() {
        SceneManager.switchScene("operations");
    }

    @FXML
    private void handleCategoriesClick() {
        SceneManager.switchScene("categories");
    }

    @FXML
    private void handlePlansLimitsClick() {
        SceneManager.switchScene("plans-limits");
    }

    @FXML
    private void handleGoalsDebtsClick() {
        // Уже на Goals
    }

    @FXML
    private void handleSettingsClick() {
        SceneManager.switchScene("settings");
    }

    private void handleLogout() {
        authService.logout();
        SceneManager.switchScene("login");
    }

    private void showComingSoon(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("В разработке");
        alert.setHeaderText(feature);
        alert.setContentText("Эта функция будет доступна в следующей версии.");
        alert.showAndWait();
    }
}
