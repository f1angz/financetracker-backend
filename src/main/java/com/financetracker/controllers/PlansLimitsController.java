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

        double totalSpent = plansLimitsService.getTotalSpent();
        spentLabel.setText(formatCurrency(totalSpent));
        spentLabel.getStyleClass().add("limits-summary-spent");

        double totalRemaining = plansLimitsService.getTotalRemaining();
        remainingLabel.setText(formatCurrency(totalRemaining));
        remainingLabel.getStyleClass().add("limits-summary-remaining");
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
        VBox card = new VBox(0);
        card.getStyleClass().add("limit-card");

        // === Header section ===
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("limit-card-header");

        VBox titleBox = new VBox(4);
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
            statusBadge.setText("✓ В пределах нормы");
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

        // === Content section ===
        VBox content = new VBox(16);
        content.getStyleClass().add("limit-card-content");

        // Progress subsection
        VBox progressSection = new VBox(8);

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

        progressSection.getChildren().addAll(valuesLine, progressBar);

        // Metrics with separator
        HBox metricsRow = new HBox();
        metricsRow.setAlignment(Pos.CENTER_LEFT);
        metricsRow.getStyleClass().add("limit-metrics-separator");

        VBox spentBox = new VBox(4);
        HBox.setHgrow(spentBox, Priority.ALWAYS);
        Label spentCaption = new Label("Потрачено");
        spentCaption.getStyleClass().add("limit-metric-caption");
        Label spentValue = new Label(formatCurrency(limit.getSpent()));
        spentValue.getStyleClass().addAll("limit-metric-value", "limit-metric-spent");
        spentBox.getChildren().addAll(spentCaption, spentValue);

        VBox remainingBox = new VBox(4);
        HBox.setHgrow(remainingBox, Priority.ALWAYS);
        Label remainingCaption = new Label("Осталось");
        remainingCaption.getStyleClass().add("limit-metric-caption");
        Label remainingValue = new Label(formatCurrency(limit.getRemaining()));
        remainingValue.getStyleClass().add("limit-metric-value");
        remainingValue.getStyleClass().add(limit.getRemaining() < 0 ? "limit-metric-over" : "limit-metric-remaining");
        remainingBox.getChildren().addAll(remainingCaption, remainingValue);

        metricsRow.getChildren().addAll(spentBox, remainingBox);

        content.getChildren().addAll(progressSection, metricsRow);

        // Footer hint (only for warning/exceeded)
        if (limit.isExceeded() || limit.isNearLimit()) {
            Label hintLabel = new Label();
            hintLabel.getStyleClass().add("limit-hint");
            hintLabel.setMaxWidth(Double.MAX_VALUE);

            if (limit.getRemaining() >= 0) {
                hintLabel.setText("Осталось только " + formatCurrency(limit.getRemaining()) + " до лимита");
                hintLabel.getStyleClass().add("limit-hint-warning");
            } else {
                hintLabel.setText("Лимит превышен на " + formatCurrency(Math.abs(limit.getRemaining())));
                hintLabel.getStyleClass().add("limit-hint-danger");
            }

            content.getChildren().add(hintLabel);
        }

        card.getChildren().addAll(header, content);
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
        SceneManager.switchScene("goals-debts");
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
