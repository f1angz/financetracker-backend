package com.financetracker.controllers;

import com.financetracker.models.User;
import com.financetracker.services.AuthService;
import com.financetracker.services.DashboardService;
import com.financetracker.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Контроллер для главного экрана Dashboard
 */
public class DashboardController {
    
    // Top bar
    @FXML private TextField searchField;
    
    // User profile
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    
    // Statistics cards
    @FXML private Label balanceLabel;
    @FXML private Label incomeLabel;
    @FXML private Label expensesLabel;
    @FXML private Label savingsLabel;
    
    // Charts
    @FXML private PieChart categoryPieChart;
    @FXML private VBox categoryLegend;
    @FXML private BarChart<String, Number> monthlyBarChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    
    // Menu items
    @FXML private Button dashboardMenuItem;
    @FXML private Button operationsMenuItem;
    @FXML private Button categoriesMenuItem;
    @FXML private Button plansLimitsMenuItem;
    @FXML private Button goalsDebtsMenuItem;
    @FXML private Button settingsMenuItem;
    
    private final AuthService authService;
    private final DashboardService dashboardService;
    private final NumberFormat currencyFormat;
    
    public DashboardController() {
        this.authService = AuthService.getInstance();
        this.dashboardService = DashboardService.getInstance();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
    }
    
    /**
     * Инициализация контроллера
     */
    @FXML
    public void initialize() {
        loadUserInfo();
        loadStatistics();
        setupCategoryPieChart();
        setupMonthlyBarChart();
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
     * Загрузка статистики
     */
    private void loadStatistics() {
        // Получение данных из сервиса
        double balance = dashboardService.getBalance();
        double income = dashboardService.getIncome();
        double expenses = dashboardService.getExpenses();
        double savings = dashboardService.getSavings();
        
        // Форматирование и отображение
        balanceLabel.setText(formatCurrency(balance));
        incomeLabel.setText(formatCurrency(income));
        expensesLabel.setText(formatCurrency(expenses));
        savingsLabel.setText(formatCurrency(savings));
    }
    
    /**
     * Настройка круговой диаграммы расходов по категориям
     */
    private void setupCategoryPieChart() {
        categoryPieChart.getData().clear();
        categoryLegend.getChildren().clear();
        
        // Получение данных
        Map<String, Double> categories = dashboardService.getCategoryExpenses();
        
        // Цвета для категорий
        String[] colors = {
            "#3B82F6", // Продукты - синий
            "#8B5CF6", // Транспорт - фиолетовый
            "#EC4899", // Развлечения - розовый
            "#10B981", // Здоровье - зеленый
            "#F59E0B", // Одежда - оранжевый
            "#6B7280"  // Прочее - серый
        };
        
        int colorIndex = 0;
        
        for (Map.Entry<String, Double> entry : categories.entrySet()) {
            String category = entry.getKey();
            Double amount = entry.getValue();
            
            // Добавление данных в диаграмму
            PieChart.Data slice = new PieChart.Data(category, amount);
            categoryPieChart.getData().add(slice);
            
            // Создание элемента легенды
            HBox legendItem = createLegendItem(
                category, 
                amount, 
                colors[colorIndex % colors.length]
            );
            categoryLegend.getChildren().add(legendItem);
            
            colorIndex++;
        }
        
        // Настройка внешнего вида
        categoryPieChart.setLabelsVisible(false);
        categoryPieChart.setStartAngle(90);
    }
    
    /**
     * Создание элемента легенды
     */
    private HBox createLegendItem(String label, double value, String color) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("legend-item");
        
        // Цветной индикатор
        Region colorBox = new Region();
        colorBox.setPrefSize(12, 12);
        colorBox.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3px;");
        colorBox.getStyleClass().add("legend-color");
        
        // Название категории
        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("legend-label");
        HBox.setMargin(nameLabel, new Insets(0, 0, 0, 0));
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        // Значение
        Label valueLabel = new Label(formatCurrency(value));
        valueLabel.getStyleClass().add("legend-value");
        
        item.getChildren().addAll(colorBox, nameLabel, spacer, valueLabel);
        
        return item;
    }
    
    /**
     * Настройка столбчатой диаграммы по месяцам
     */
    private void setupMonthlyBarChart() {
        monthlyBarChart.getData().clear();
        
        // Получение данных
        Map<String, Double> incomeData = dashboardService.getMonthlyIncome();
        Map<String, Double> expenseData = dashboardService.getMonthlyExpenses();
        
        // Создание серий данных
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Доходы");
        
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Расходы");
        
        // Добавление данных
        String[] months = {"Янв", "Фев", "Мар", "Апр", "Май"};
        
        for (String month : months) {
            Double income = incomeData.getOrDefault(month, 0.0);
            Double expense = expenseData.getOrDefault(month, 0.0);
            
            incomeSeries.getData().add(new XYChart.Data<>(month, income));
            expenseSeries.getData().add(new XYChart.Data<>(month, expense));
        }
        
        // Добавление серий в диаграмму
        monthlyBarChart.getData().addAll(incomeSeries, expenseSeries);
        
        // Настройка осей
        xAxis.setLabel("");
        yAxis.setLabel("");
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number value) {
                return formatCurrencyShort(value.doubleValue());
            }
        });
        
        // Настройка внешнего вида
        monthlyBarChart.setLegendVisible(true);
        monthlyBarChart.setAnimated(true);
    }
    
    /**
     * Форматирование валюты
     */
    private String formatCurrency(double amount) {
        return String.format("₽ %,.0f", amount);
    }
    
    /**
     * Форматирование валюты (короткое)
     */
    private String formatCurrencyShort(double amount) {
        if (amount >= 1000) {
            return String.format("%.0fk", amount / 1000);
        }
        return String.format("%.0f", amount);
    }
    
    // ========== MENU NAVIGATION ==========
    
    @FXML
    private void handleDashboardClick() {
        // Уже на Dashboard
        setActiveMenuItem(dashboardMenuItem);
    }
    
    @FXML
    private void handleOperationsClick() {
        setActiveMenuItem(operationsMenuItem);
        SceneManager.switchScene("operations");
    }
    
    @FXML
    private void handleCategoriesClick() {
        setActiveMenuItem(categoriesMenuItem);
        SceneManager.switchScene("categories");
    }
    
    @FXML
    private void handlePlansLimitsClick() {
        setActiveMenuItem(plansLimitsMenuItem);
        SceneManager.switchScene("plans-limits");
    }
    
    @FXML
    private void handleGoalsDebtsClick() {
        setActiveMenuItem(goalsDebtsMenuItem);
        SceneManager.switchScene("goals-debts");
    }
    
    @FXML
    private void handleSettingsClick() {
        setActiveMenuItem(settingsMenuItem);
        SceneManager.switchScene("settings");
    }
    
    /**
     * Установка активного пункта меню
     */
    private void setActiveMenuItem(Button activeButton) {
        // Убрать активный класс со всех кнопок
        dashboardMenuItem.getStyleClass().remove("menu-item-active");
        operationsMenuItem.getStyleClass().remove("menu-item-active");
        categoriesMenuItem.getStyleClass().remove("menu-item-active");
        plansLimitsMenuItem.getStyleClass().remove("menu-item-active");
        goalsDebtsMenuItem.getStyleClass().remove("menu-item-active");
        settingsMenuItem.getStyleClass().remove("menu-item-active");
        
        // Добавить активный класс к выбранной кнопке
        if (!activeButton.getStyleClass().contains("menu-item-active")) {
            activeButton.getStyleClass().add("menu-item-active");
        }
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
