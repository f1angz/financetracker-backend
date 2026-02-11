package com.financetracker.controllers;

import com.financetracker.models.User;
import com.financetracker.services.AuthService;
import com.financetracker.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Контроллер экрана настроек.
 */
public class SettingsController {

    // Top bar
    @FXML private TextField searchField;

    // Sidebar profile
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;

    // Main profile card
    @FXML private Label profileInitialsLabel;
    @FXML private Label profileNameLabel;
    @FXML private Label profileEmailLabel;

    // Settings fields
    @FXML private CheckBox darkThemeToggle;
    @FXML private CheckBox notificationsToggle;
    @FXML private ChoiceBox<String> currencyChoice;
    @FXML private ChoiceBox<String> dateFormatChoice;

    // Security fields
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    // Menu
    @FXML private Button dashboardMenuItem;
    @FXML private Button operationsMenuItem;
    @FXML private Button categoriesMenuItem;
    @FXML private Button plansLimitsMenuItem;
    @FXML private Button goalsDebtsMenuItem;
    @FXML private Button settingsMenuItem;

    private final AuthService authService;

    public SettingsController() {
        this.authService = AuthService.getInstance();
    }

    @FXML
    public void initialize() {
        loadUserInfo();
        setupDefaults();
    }

    private void loadUserInfo() {
        User user = authService.getCurrentUser();
        if (user == null) {
            return;
        }

        userNameLabel.setText(user.getName());
        userEmailLabel.setText(user.getEmail());
        profileNameLabel.setText(user.getName());
        profileEmailLabel.setText(user.getEmail());
        profileInitialsLabel.setText(createInitials(user.getName()));
    }

    private void setupDefaults() {
        currencyChoice.getItems().setAll(
            "₽ Российский рубль (RUB)",
            "$ Доллар США (USD)",
            "€ Евро (EUR)"
        );
        currencyChoice.setValue("₽ Российский рубль (RUB)");

        dateFormatChoice.getItems().setAll(
            "ДД.MM.ГГГГ (04.02.2026)",
            "MM/DD/YYYY (02/04/2026)",
            "YYYY-MM-DD (2026-02-04)"
        );
        dateFormatChoice.setValue("ДД.MM.ГГГГ (04.02.2026)");

        darkThemeToggle.setSelected(false);
        notificationsToggle.setSelected(true);

        currentPasswordField.setText("••••••••");
        newPasswordField.setText("••••••••");
        confirmPasswordField.setText("••••••••");
    }

    private String createInitials(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase() + parts[1].charAt(0);
        }
        if (parts.length == 1 && !parts[0].isEmpty()) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase();
        }
        return "ИП";
    }

    // ========== SETTINGS ACTIONS ==========

    @FXML
    private void handleUpdatePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            showWarning("Не все поля заполнены", "Заполните все поля для смены пароля.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showWarning("Пароли не совпадают", "Новый пароль и подтверждение должны совпадать.");
            return;
        }

        showInfo("Пароль обновлён", "Пароль успешно изменён (mock-сценарий).");
    }

    @FXML
    private void handleEditProfile() {
        showComingSoon("Редактирование профиля");
    }

    @FXML
    private void handleChangePassword() {
        showComingSoon("Изменение пароля");
    }

    @FXML
    private void handleLogoutAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Выход");
        alert.setHeaderText(null);
        alert.setContentText("Выйти из аккаунта?");

        ButtonType logoutButton = new ButtonType("Выйти");
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(logoutButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == logoutButton) {
                authService.logout();
                SceneManager.switchScene("login");
            }
        });
    }

    // ========== TOP BAR ACTIONS ==========

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
        handleLogoutAction();
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
        SceneManager.switchScene("goals-debts");
    }

    @FXML
    private void handleSettingsClick() {
        // Уже на Settings
    }

    private void showInfo(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void showWarning(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void showComingSoon(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("В разработке");
        alert.setHeaderText(feature);
        alert.setContentText("Эта функция будет доступна в следующей версии.");
        alert.showAndWait();
    }
}
