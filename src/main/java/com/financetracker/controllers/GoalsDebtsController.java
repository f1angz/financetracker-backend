package com.financetracker.controllers;

import com.financetracker.models.Debt;
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
import javafx.scene.shape.StrokeLineCap;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class GoalsDebtsController {

    @FXML private TextField searchField;
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    @FXML private GridPane goalsGrid;
    @FXML private TabPane goalsTabPane;
    @FXML private GridPane activeDebtsGrid;
    @FXML private VBox paidDebtsContainer;

    @FXML private Button dashboardMenuItem;
    @FXML private Button operationsMenuItem;
    @FXML private Button categoriesMenuItem;
    @FXML private Button plansLimitsMenuItem;
    @FXML private Button goalsDebtsMenuItem;
    @FXML private Button settingsMenuItem;

    private final AuthService authService;
    private final GoalsService goalsService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public GoalsDebtsController() {
        this.authService = AuthService.getInstance();
        this.goalsService = GoalsService.getInstance();
    }

    @FXML
    public void initialize() {
        loadUserInfo();
        loadGoals();
        loadDebts();
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

        int row = 0, col = 0;
        for (Goal goal : goals) {
            VBox card = createGoalCard(goal);
            goalsGrid.add(card, col, row);
            GridPane.setHgrow(card, Priority.ALWAYS);
            col++;
            if (col >= 2) { col = 0; row++; }
        }
    }

    private void loadDebts() {
        activeDebtsGrid.getChildren().clear();
        paidDebtsContainer.getChildren().clear();

        List<Debt> debts = goalsService.getAllDebts();

        int row = 0, col = 0;
        for (Debt debt : debts) {
            if (debt.getStatus() == Debt.DebtStatus.ACTIVE) {
                VBox card = createDebtCard(debt);
                activeDebtsGrid.add(card, col, row);
                GridPane.setHgrow(card, Priority.ALWAYS);
                col++;
                if (col >= 2) { col = 0; row++; }
            } else {
                HBox paidRow = createPaidDebtRow(debt);
                paidDebtsContainer.getChildren().add(paidRow);
            }
        }
    }

    private VBox createDebtCard(Debt debt) {
        VBox card = new VBox(16);
        card.getStyleClass().add("debt-card");

        boolean isBorrowed = debt.getType() == Debt.DebtType.BORROWED;

        // Header: avatar + person info + type badge
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.getStyleClass().add(isBorrowed ? "debt-avatar-borrowed" : "debt-avatar-lent");
        FontAwesomeIconView avatarIcon = new FontAwesomeIconView();
        avatarIcon.setGlyphName("USER");
        avatarIcon.setSize("18");
        avatarIcon.setStyle("-fx-fill: " + (isBorrowed ? "#DC2626" : "#16A34A") + ";");
        avatar.getChildren().add(avatarIcon);

        VBox personBox = new VBox(2);
        HBox.setHgrow(personBox, Priority.ALWAYS);
        Label personLabel = new Label(debt.getPerson());
        personLabel.getStyleClass().add("debt-person-name");
        Label descLabel = new Label(debt.getDescription());
        descLabel.getStyleClass().add("debt-description");
        personBox.getChildren().addAll(personLabel, descLabel);

        Label typeBadge = new Label(debt.getType().getDisplayName());
        typeBadge.getStyleClass().add(isBorrowed ? "debt-type-badge-borrowed" : "debt-type-badge-lent");

        header.getChildren().addAll(avatar, personBox, typeBadge);

        // Amount
        Label amountLabel = new Label(formatCurrency(debt.getAmount()));
        amountLabel.getStyleClass().add(isBorrowed ? "debt-amount-borrowed" : "debt-amount-lent");

        // Dates grid
        HBox datesRow = new HBox(24);
        datesRow.setAlignment(Pos.CENTER_LEFT);

        VBox dateFromBox = new VBox(4);
        Label dateFromCaption = new Label("Дата займа");
        dateFromCaption.getStyleClass().add("debt-date-label");
        Label dateFromValue = new Label(debt.getDate().format(dateFormatter));
        dateFromValue.getStyleClass().add("debt-date-value");
        dateFromBox.getChildren().addAll(dateFromCaption, dateFromValue);

        VBox dateToBox = new VBox(4);
        Label dateToCaption = new Label("Срок возврата");
        dateToCaption.getStyleClass().add("debt-date-label");
        Label dateToValue = new Label(debt.getDeadline().format(dateFormatter));
        dateToValue.getStyleClass().add("debt-date-value");
        dateToBox.getChildren().addAll(dateToCaption, dateToValue);

        datesRow.getChildren().addAll(dateFromBox, dateToBox);

        // Status
        long daysRemaining = debt.getDaysRemaining();
        Label statusLabel;
        if (daysRemaining >= 0) {
            statusLabel = new Label("Осталось " + daysRemaining + " дней");
            statusLabel.getStyleClass().add("debt-status-active");
        } else {
            statusLabel = new Label("Просрочено на " + Math.abs(daysRemaining) + " дней");
            statusLabel.getStyleClass().add("debt-status-overdue");
        }

        // Actions
        HBox actionsRow = new HBox(12);
        actionsRow.setAlignment(Pos.CENTER_LEFT);
        actionsRow.getStyleClass().add("debt-actions-row");

        Button editBtn = new Button("Редактировать");
        editBtn.getStyleClass().add("debt-action-button");
        editBtn.setOnAction(e -> showComingSoon("Редактирование долга"));

        Button deleteBtn = new Button("Удалить");
        deleteBtn.getStyleClass().add("debt-action-button");
        deleteBtn.setOnAction(e -> showComingSoon("Удаление долга"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button payBtn = new Button("Погасить");
        payBtn.getStyleClass().add("debt-pay-button");
        payBtn.setOnAction(e -> showComingSoon("Погашение долга"));

        actionsRow.getChildren().addAll(editBtn, deleteBtn, spacer, payBtn);

        card.getChildren().addAll(header, amountLabel, datesRow, statusLabel, actionsRow);
        return card;
    }

    private HBox createPaidDebtRow(Debt debt) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("paid-debt-row");

        boolean isBorrowed = debt.getType() == Debt.DebtType.BORROWED;

        StackPane avatar = new StackPane();
        avatar.getStyleClass().add(isBorrowed ? "debt-avatar-borrowed" : "debt-avatar-lent");
        avatar.setStyle("-fx-pref-width: 36px; -fx-pref-height: 36px; -fx-min-width: 36px; -fx-min-height: 36px; -fx-max-width: 36px; -fx-max-height: 36px;");
        FontAwesomeIconView avatarIcon = new FontAwesomeIconView();
        avatarIcon.setGlyphName("USER");
        avatarIcon.setSize("14");
        avatarIcon.setStyle("-fx-fill: " + (isBorrowed ? "#DC2626" : "#16A34A") + ";");
        avatar.getChildren().add(avatarIcon);

        VBox infoBox = new VBox(2);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        Label personLabel = new Label(debt.getPerson());
        personLabel.getStyleClass().add("paid-debt-person");
        Label dateLabel = new Label("Погашен " + debt.getDeadline().format(dateFormatter));
        dateLabel.getStyleClass().add("paid-debt-date");
        infoBox.getChildren().addAll(personLabel, dateLabel);

        Label amountLabel = new Label(formatCurrency(debt.getAmount()));
        amountLabel.getStyleClass().add("paid-debt-amount");

        Label badge = new Label("Погашен");
        badge.getStyleClass().add("paid-debt-badge");

        row.getChildren().addAll(avatar, infoBox, amountLabel, badge);
        return row;
    }

    // ========== GOAL CARD (unchanged from original) ==========

    private VBox createGoalCard(Goal goal) {
        VBox card = new VBox(16);
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

        StackPane progressPane = createCircularProgress(goal);
        VBox.setVgrow(progressPane, Priority.NEVER);

        HBox metricsRow = new HBox();
        metricsRow.setAlignment(Pos.CENTER_LEFT);

        VBox savedBox = new VBox(4);
        Label savedCaption = new Label("Накоплено");
        savedCaption.getStyleClass().add("goal-metric-caption");
        Label savedValue = new Label(formatCurrency(goal.getCurrentAmount()));
        savedValue.getStyleClass().addAll("goal-metric-value", "goal-metric-saved");
        savedBox.getChildren().addAll(savedCaption, savedValue);

        Region metricsSpacer = new Region();
        HBox.setHgrow(metricsSpacer, Priority.ALWAYS);

        VBox remainingBox = new VBox(4);
        Label remainingCaption = new Label("Осталось");
        remainingCaption.getStyleClass().add("goal-metric-caption");
        Label remainingValue = new Label(formatCurrency(goal.getRemaining()));
        remainingValue.getStyleClass().add("goal-metric-value");
        remainingBox.getChildren().addAll(remainingCaption, remainingValue);

        metricsRow.getChildren().addAll(savedBox, metricsSpacer, remainingBox);

        VBox targetBox = new VBox(4);
        targetBox.getStyleClass().add("goal-separator");
        Label targetCaption = new Label("Целевая сумма");
        targetCaption.getStyleClass().add("goal-metric-caption");
        Label targetValue = new Label(formatCurrency(goal.getTargetAmount()));
        targetValue.getStyleClass().addAll("goal-metric-value", "goal-metric-target");
        targetBox.getChildren().addAll(targetCaption, targetValue);

        Button topUpButton = new Button("Пополнить");
        topUpButton.getStyleClass().add("goal-topup-button");
        topUpButton.setStyle("-fx-background-color: " + goal.getColor() + ";");
        topUpButton.setMaxWidth(Double.MAX_VALUE);
        topUpButton.setOnAction(e -> showComingSoon("Пополнение цели"));

        card.getChildren().addAll(header, progressPane, metricsRow, targetBox, topUpButton);
        return card;
    }

    private StackPane createCircularProgress(Goal goal) {
        StackPane wrapper = new StackPane();
        wrapper.setPrefHeight(192);
        wrapper.setMaxHeight(192);

        Pane ringPane = new Pane();
        ringPane.setPrefSize(160, 160);
        ringPane.setMaxSize(160, 160);

        double progress = Math.max(0.0, Math.min(goal.getProgress(), 100.0)) / 100.0;
        double center = 80;
        double radius = 70;
        double strokeWidth = 12;

        Arc track = new Arc(center, center, radius, radius, 90, -360);
        track.setFill(null);
        track.setStrokeWidth(strokeWidth);
        track.setStrokeLineCap(StrokeLineCap.ROUND);
        track.setType(ArcType.OPEN);
        track.getStyleClass().add("goal-ring-track");

        double fillAngle = 360.0 * progress;
        Arc fill = new Arc(center, center, radius, radius, 90, -fillAngle);
        fill.setFill(null);
        fill.setStrokeWidth(strokeWidth);
        fill.setStrokeLineCap(StrokeLineCap.ROUND);
        fill.setType(ArcType.OPEN);
        fill.setStyle("-fx-stroke: " + goal.getColor() + ";");

        ringPane.getChildren().addAll(track, fill);

        VBox centerContent = new VBox(2);
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

    private String formatCurrency(double amount) {
        return String.format("₽ %,.0f", amount);
    }

    // ========== ACTIONS ==========

    @FXML
    private void handleAddGoal() {
        showComingSoon("Добавление цели");
    }

    @FXML
    private void handleAddDebt() {
        showComingSoon("Добавление долга");
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

    @FXML private void handleDashboardClick() { SceneManager.switchScene("dashboard"); }
    @FXML private void handleOperationsClick() { SceneManager.switchScene("operations"); }
    @FXML private void handleCategoriesClick() { SceneManager.switchScene("categories"); }
    @FXML private void handlePlansLimitsClick() { SceneManager.switchScene("plans-limits"); }
    @FXML private void handleGoalsDebtsClick() { /* Already here */ }
    @FXML private void handleSettingsClick() { SceneManager.switchScene("settings"); }

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
