package com.financetracker.controllers;

import com.financetracker.models.SpendingLimit;
import com.financetracker.models.User;
import com.financetracker.services.AuthService;
import com.financetracker.services.PlansLimitsService;
import com.financetracker.utils.SceneManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Контроллер для экрана лимитов по категориям.
 */
public class PlansLimitsController {

    // Top bar
    @FXML private TextField searchField;

    // User profile
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;

    // Page header
    @FXML private Label totalLimitLabel;
    @FXML private Label spentLabel;
    @FXML private Label remainingLabel;

    // Limits grid
    @FXML private GridPane limitsGrid;

    // Menu items
    @FXML private Button dashboardMenuItem;
    @FXML private Button operationsMenuItem;
    @FXML private Button categoriesMenuItem;
    @FXML private Button plansLimitsMenuItem;
    @FXML private Button goalsDebtsMenuItem;
    @FXML private Button settingsMenuItem;

    private final AuthService authService;
    private final PlansLimitsService plansLimitsService;

    public PlansLimitsController() {
        this.authService = AuthService.getInstance();
        this.plansLimitsService = PlansLimitsService.getInstance();
    }

    @FXML
    public void initialize() {
        loadUserInfo();
        loadSummary();
        loadLimitsCards();
    }

    private void loadUserInfo() {
        User user = authService.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getName());
            userEmailLabel.setText(user.getEmail());
        }
    }

    private void loadSummary() {
        totalLimitLabel.setText(formatCurrency(plansLimitsService.getTotalLimit()));
        spentLabel.setText(formatCurrency(plansLimitsService.getTotalSpent()));
        remainingLabel.setText(formatCurrency(plansLimitsService.getTotalRemaining()));
    }

    private void loadLimitsCards() {
        limitsGrid.getChildren().clear();

        List<SpendingLimit> limits = plansLimitsService.getAllLimits();

        int columns = 2;
        int row = 0;
        int col = 0;

        for (SpendingLimit limit : limits) {
            VBox card = createLimitCard(limit);
            limitsGrid.add(card, col, row);
            GridPane.setHgrow(card, Priority.ALWAYS);

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createLimitCard(SpendingLimit limit) {
        VBox card = new VBox(14);
        card.getStyleClass().add("limit-card");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label categoryLabel = new Label(limit.getCategory());
        categoryLabel.getStyleClass().add("limit-card-title");

        Label monthLabel = new Label(limit.getMonthLabel());
        monthLabel.getStyleClass().add("limit-card-subtitle");
        titleBox.getChildren().addAll(categoryLabel, monthLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = new Label();
        statusBadge.getStyleClass().add("limit-status-badge");

        if (limit.isExceeded()) {
            statusBadge.setText("⚠ Превышен");
            statusBadge.getStyleClass().add("limit-status-exceeded");
        } else if (limit.isNearLimit()) {
            statusBadge.setText("⚠ Близко к лимиту");
            statusBadge.getStyleClass().add("limit-status-warning");
        } else {
            statusBadge.setText("✓ В пределах");
            statusBadge.getStyleClass().add("limit-status-normal");
        }

        Button editButton = new Button();
        editButton.getStyleClass().add("limit-edit-button");
        FontAwesomeIconView editIcon = new FontAwesomeIconView();
        editIcon.setGlyphName("PENCIL_SQUARE");
        editIcon.setSize("12");
        editIcon.getStyleClass().add("limit-edit-icon");
        editButton.setGraphic(editIcon);
        editButton.setOnAction(e -> showComingSoon("Редактирование лимита"));

        header.getChildren().addAll(titleBox, spacer, statusBadge, editButton);

        // Limit values line
        HBox valuesLine = new HBox(8);
        valuesLine.setAlignment(Pos.CENTER_LEFT);

        Label usedLimitLabel = new Label(formatCurrency(limit.getSpent()) + " / " + formatCurrency(limit.getLimitAmount()));
        usedLimitLabel.getStyleClass().add("limit-value-line");

        Region valuesSpacer = new Region();
        HBox.setHgrow(valuesSpacer, Priority.ALWAYS);

        Label percentLabel = new Label(String.format("%.1f%%", limit.getUsagePercent()));
        percentLabel.getStyleClass().add("limit-percent");
        if (limit.isExceeded()) {
            percentLabel.getStyleClass().add("limit-percent-exceeded");
        } else {
            percentLabel.getStyleClass().add("limit-percent-warning");
        }

        valuesLine.getChildren().addAll(usedLimitLabel, valuesSpacer, percentLabel);

        // Progress
        ProgressBar progressBar = new ProgressBar(limit.getProgressValue());
        progressBar.getStyleClass().add("limit-progress");
        if (limit.isExceeded()) {
            progressBar.getStyleClass().add("limit-progress-exceeded");
        } else if (limit.isNearLimit()) {
            progressBar.getStyleClass().add("limit-progress-warning");
        } else {
            progressBar.getStyleClass().add("limit-progress-normal");
        }
        progressBar.setMaxWidth(Double.MAX_VALUE);

        // Bottom metrics
        HBox metricsRow = new HBox(30);
        metricsRow.setAlignment(Pos.CENTER_LEFT);

        VBox spentBox = new VBox(4);
        Label spentCaption = new Label("Потрачено");
        spentCaption.getStyleClass().add("limit-metric-caption");
        Label spentValue = new Label(formatCurrency(limit.getSpent()));
        spentValue.getStyleClass().addAll("limit-metric-value", "limit-metric-spent");
        spentBox.getChildren().addAll(spentCaption, spentValue);

        VBox remainingBox = new VBox(4);
        Label remainingCaption = new Label("Осталось");
        remainingCaption.getStyleClass().add("limit-metric-caption");
        Label remainingValue = new Label(formatCurrency(limit.getRemaining()));
        remainingValue.getStyleClass().add("limit-metric-value");
        remainingValue.getStyleClass().add(limit.getRemaining() < 0 ? "limit-metric-over" : "limit-metric-remaining");
        remainingBox.getChildren().addAll(remainingCaption, remainingValue);

        metricsRow.getChildren().addAll(spentBox, remainingBox);

        // Footer hint
        Label hintLabel = new Label();
        hintLabel.getStyleClass().add("limit-hint");
        hintLabel.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(hintLabel, new Insets(2, 0, 0, 0));

        if (limit.getRemaining() >= 0) {
            hintLabel.setText("Осталось только " + formatCurrency(limit.getRemaining()) + " до лимита");
            hintLabel.getStyleClass().add("limit-hint-warning");
        } else {
            hintLabel.setText("Лимит превышен на " + formatCurrency(Math.abs(limit.getRemaining())));
            hintLabel.getStyleClass().add("limit-hint-danger");
        }

        card.getChildren().addAll(header, valuesLine, progressBar, metricsRow, hintLabel);
        return card;
    }

    private String formatCurrency(double amount) {
        return String.format("₽ %,.0f", amount);
    }

    // ========== ACTIONS ==========

    @FXML
    private void handleAddLimit() {
        showComingSoon("Добавление лимита");
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
        // Уже на экране лимитов
    }

    @FXML
    private void handleGoalsDebtsClick() {
        showComingSoon("Goals & Debts");
    }

    @FXML
    private void handleSettingsClick() {
        showComingSoon("Settings");
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
