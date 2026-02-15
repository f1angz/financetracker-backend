package com.financetracker.controllers;

import com.financetracker.models.Category;
import com.financetracker.models.User;
import com.financetracker.services.AuthService;
import com.financetracker.services.CategoriesService;
import com.financetracker.utils.SceneManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

import javafx.scene.layout.ColumnConstraints;

/**
 * Контроллер для экрана категорий
 */
public class CategoriesController {
    
    // Top bar
    @FXML private TextField searchField;
    
    // User profile
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    
    // Grids for categories
    @FXML private GridPane expenseCategoriesGrid;
    @FXML private GridPane incomeCategoriesGrid;
    @FXML private GridPane universalCategoriesGrid;

    // Section badges
    @FXML private Label expenseBadge;
    @FXML private Label incomeBadge;
    @FXML private Label universalBadge;
    
    // Menu items
    @FXML private Button dashboardMenuItem;
    @FXML private Button operationsMenuItem;
    @FXML private Button categoriesMenuItem;
    @FXML private Button plansLimitsMenuItem;
    @FXML private Button goalsDebtsMenuItem;
    @FXML private Button settingsMenuItem;
    
    private final AuthService authService;
    private final CategoriesService categoriesService;
    
    public CategoriesController() {
        this.authService = AuthService.getInstance();
        this.categoriesService = CategoriesService.getInstance();
    }
    
    /**
     * Инициализация контроллера
     */
    @FXML
    public void initialize() {
        loadUserInfo();
        loadCategories();
    }
    
    /**
     * Загрузка информации о пользователе
     */
    private void loadUserInfo() {
        User user = authService.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getName());
            userEmailLabel.setText(user.getEmail());
        }
    }
    
    /**
     * Загрузка категорий
     */
    private void loadCategories() {
        // Очистка grid
        expenseCategoriesGrid.getChildren().clear();
        expenseCategoriesGrid.getColumnConstraints().clear();
        incomeCategoriesGrid.getChildren().clear();
        incomeCategoriesGrid.getColumnConstraints().clear();
        universalCategoriesGrid.getChildren().clear();
        universalCategoriesGrid.getColumnConstraints().clear();

        // Получение категорий по типам
        List<Category> expenseCategories = categoriesService.getCategoriesByType(Category.CategoryType.EXPENSE);
        List<Category> incomeCategories = categoriesService.getCategoriesByType(Category.CategoryType.INCOME);
        List<Category> universalCategories = categoriesService.getCategoriesByType(Category.CategoryType.UNIVERSAL);

        // Установка бейджей
        expenseBadge.setText(String.valueOf(expenseCategories.size()));
        incomeBadge.setText(String.valueOf(incomeCategories.size()));
        universalBadge.setText(String.valueOf(universalCategories.size()));

        // Настройка колонок (4 равные колонки)
        setupGridColumns(expenseCategoriesGrid);
        setupGridColumns(incomeCategoriesGrid);
        setupGridColumns(universalCategoriesGrid);

        // Заполнение grid (4 колонки)
        fillCategoriesGrid(expenseCategoriesGrid, expenseCategories);
        fillCategoriesGrid(incomeCategoriesGrid, incomeCategories);
        fillCategoriesGrid(universalCategoriesGrid, universalCategories);
    }

    private void setupGridColumns(GridPane grid) {
        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            grid.getColumnConstraints().add(cc);
        }
    }
    
    /**
     * Заполнение grid категориями
     */
    private void fillCategoriesGrid(GridPane grid, List<Category> categories) {
        int columns = 4;
        int row = 0;
        int col = 0;
        
        for (Category category : categories) {
            VBox categoryCard = createCategoryCard(category);
            
            grid.add(categoryCard, col, row);
            GridPane.setHgrow(categoryCard, Priority.ALWAYS);
            GridPane.setVgrow(categoryCard, Priority.ALWAYS);
            
            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }
    
    /**
     * Создание карточки категории
     */
    private VBox createCategoryCard(Category category) {
        VBox card = new VBox(0);
        card.getStyleClass().add("category-card");
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);

        // Иконка категории
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(48, 48);
        iconContainer.setMinSize(48, 48);
        iconContainer.setMaxSize(48, 48);
        iconContainer.setStyle(
            "-fx-background-color: " + category.getColor() + "14; " +
            "-fx-background-radius: 14px;"
        );

        FontAwesomeIconView icon = new FontAwesomeIconView(
            FontAwesomeIcon.valueOf(category.getIcon())
        );
        icon.setSize("24");
        icon.setFill(Color.web(category.getColor()));

        iconContainer.getChildren().add(icon);

        // Название категории
        Label nameLabel = new Label(category.getName());
        nameLabel.getStyleClass().add("category-name");
        VBox.setMargin(nameLabel, new Insets(16, 0, 0, 0));

        // Количество операций
        Label countLabel = new Label(category.getOperationsCount() + " операций");
        countLabel.getStyleClass().add("category-count");
        VBox.setMargin(countLabel, new Insets(4, 0, 0, 0));

        // Секция суммы с разделителем
        String totalText;
        switch (category.getType()) {
            case INCOME:
                totalText = "Всего получено";
                break;
            case UNIVERSAL:
                totalText = "Общая сумма";
                break;
            default:
                totalText = "Всего потрачено";
                break;
        }

        VBox amountSection = new VBox(4);
        amountSection.setStyle(
            "-fx-border-color: #F3F4F6; " +
            "-fx-border-width: 1 0 0 0; " +
            "-fx-padding: 13 0 0 0;"
        );
        VBox.setMargin(amountSection, new Insets(12, 0, 0, 0));

        Label totalLabel = new Label(totalText);
        totalLabel.getStyleClass().add("category-total-label");

        Label amountLabel = new Label(formatCurrency(category.getTotalAmount()));
        amountLabel.getStyleClass().add("category-amount");

        amountSection.getChildren().addAll(totalLabel, amountLabel);

        // Сборка карточки
        card.getChildren().addAll(iconContainer, nameLabel, countLabel, amountSection);

        // Клик на карточку
        card.setOnMouseClicked(event -> handleCategoryClick(category));

        return card;
    }
    
    /**
     * Форматирование валюты
     */
    private String formatCurrency(double amount) {
        return String.format("₽ %,.0f", amount);
    }
    
    /**
     * Клик на категорию
     */
    private void handleCategoryClick(Category category) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Категория: " + category.getName());
        alert.setHeaderText(null);
        alert.setContentText(
            "Операций: " + category.getOperationsCount() + "\n" +
            "Сумма: " + formatCurrency(category.getTotalAmount()) + "\n" +
            "Тип: " + category.getType().getDisplayName()
        );
        alert.showAndWait();
    }
    
    // ========== ACTIONS ==========
    
    @FXML
    private void handleAddCategory() {
        showComingSoon("Добавление категории");
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
        // Уже на Categories
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
        SceneManager.switchScene("settings");
    }
    
    /**
     * Выход из системы
     */
    private void handleLogout() {
        authService.logout();
        SceneManager.switchScene("login");
    }
    
    /**
     * Показ уведомления "В разработке"
     */
    private void showComingSoon(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("В разработке");
        alert.setHeaderText(feature);
        alert.setContentText("Эта функция будет доступна в следующей версии.");
        alert.showAndWait();
    }
}
