package com.financetracker.controllers;

import com.financetracker.models.User;
import com.financetracker.services.AuthService;
import com.financetracker.services.DashboardService;
import com.financetracker.utils.SceneManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.text.NumberFormat;
import java.util.*;

/**
 * Контроллер для главного экрана Dashboard
 */
public class
DashboardController {
    
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

    // Bottom section
    @FXML private VBox topCategoriesContainer;
    @FXML private VBox recentOperationsContainer;
    
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
        setupTopCategories();
        setupRecentOperations();
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
     * Настройка блока "Топ категорий расходов"
     */
    private void setupTopCategories() {
        topCategoriesContainer.getChildren().clear();

        List<Map<String, Object>> topCategories = new ArrayList<>();
        topCategories.add(Map.of("name", "Продукты", "amount", 18500.0, "percent", 24.7, "trend", "up"));
        topCategories.add(Map.of("name", "Здоровье", "amount", 15200.0, "percent", 20.3, "trend", "down"));
        topCategories.add(Map.of("name", "Транспорт", "amount", 12300.0, "percent", 16.4, "trend", "up"));
        topCategories.add(Map.of("name", "Прочее", "amount", 12070.0, "percent", 16.1, "trend", "same"));
        topCategories.add(Map.of("name", "Развлечения", "amount", 8900.0, "percent", 11.9, "trend", "up"));

        for (int i = 0; i < topCategories.size(); i++) {
            Map<String, Object> cat = topCategories.get(i);
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);

            // Rank number
            StackPane rankPane = new StackPane();
            rankPane.getStyleClass().add("top-category-rank");
            Label rankLabel = new Label(String.valueOf(i + 1));
            rankLabel.getStyleClass().add("top-category-rank-text");
            rankPane.getChildren().add(rankLabel);

            // Content
            VBox contentBox = new VBox(4);
            HBox.setHgrow(contentBox, Priority.ALWAYS);

            HBox nameAmountRow = new HBox();
            nameAmountRow.setAlignment(Pos.CENTER_LEFT);
            Label nameLabel = new Label((String) cat.get("name"));
            nameLabel.getStyleClass().add("top-category-name");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label amountLabel = new Label(formatCurrency((Double) cat.get("amount")));
            amountLabel.getStyleClass().add("top-category-amount");
            nameAmountRow.getChildren().addAll(nameLabel, spacer, amountLabel);

            HBox progressRow = new HBox(8);
            progressRow.setAlignment(Pos.CENTER_LEFT);
            StackPane progressTrack = new StackPane();
            progressTrack.getStyleClass().add("top-category-progress-track");
            HBox.setHgrow(progressTrack, Priority.ALWAYS);
            Region progressBar = new Region();
            progressBar.getStyleClass().add("top-category-progress-bar");
            double percent = (Double) cat.get("percent");
            progressBar.maxWidthProperty().bind(progressTrack.widthProperty().multiply(percent / 100.0));
            progressTrack.getChildren().add(progressBar);
            StackPane.setAlignment(progressBar, Pos.CENTER_LEFT);
            Label percentLabel = new Label(String.format("%.1f%%", percent));
            percentLabel.getStyleClass().add("top-category-percent");

            progressRow.getChildren().addAll(progressTrack, percentLabel);
            contentBox.getChildren().addAll(nameAmountRow, progressRow);

            // Trend arrow
            String trend = (String) cat.get("trend");
            if ("up".equals(trend)) {
                FontAwesomeIconView arrow = new FontAwesomeIconView();
                arrow.setGlyphName("ARROW_UP");
                arrow.setSize("14");
                arrow.setStyle("-fx-fill: #EF4444;");
                row.getChildren().addAll(rankPane, contentBox, arrow);
            } else if ("down".equals(trend)) {
                FontAwesomeIconView arrow = new FontAwesomeIconView();
                arrow.setGlyphName("ARROW_DOWN");
                arrow.setSize("14");
                arrow.setStyle("-fx-fill: #10B981;");
                row.getChildren().addAll(rankPane, contentBox, arrow);
            } else {
                row.getChildren().addAll(rankPane, contentBox);
            }

            topCategoriesContainer.getChildren().add(row);
        }
    }

    /**
     * Настройка блока "Последние операции"
     */
    private void setupRecentOperations() {
        recentOperationsContainer.getChildren().clear();

        List<Map<String, Object>> operations = new ArrayList<>();
        operations.add(Map.of("type", "expense", "category", "Продукты", "amount", 3450.0, "date", "2026-02-04", "time", "14:30", "comment", "Супермаркет Пятёрочка"));
        operations.add(Map.of("type", "income", "category", "Зарплата", "amount", 85000.0, "date", "2026-02-03", "time", "09:00", "comment", "Ежемесячная зарплата"));
        operations.add(Map.of("type", "expense", "category", "Транспорт", "amount", 1200.0, "date", "2026-02-03", "time", "08:15", "comment", "Заправка автомобиля"));
        operations.add(Map.of("type", "expense", "category", "Развлечения", "amount", 2800.0, "date", "2026-02-02", "time", "19:45", "comment", "Кино с семьёй"));
        operations.add(Map.of("type", "income", "category", "Фриланс", "amount", 15000.0, "date", "2026-02-01", "time", "16:20", "comment", "Проект веб-дизайна"));
        operations.add(Map.of("type", "expense", "category", "Здоровье", "amount", 4500.0, "date", "2026-02-01", "time", "11:00", "comment", "Стоматолог"));

        for (Map<String, Object> op : operations) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("recent-op-row");
            row.setPadding(new Insets(10, 12, 10, 12));

            // Type icon
            String type = (String) op.get("type");
            boolean isIncome = "income".equals(type);
            StackPane iconPane = new StackPane();
            iconPane.getStyleClass().add(isIncome ? "recent-op-icon-income" : "recent-op-icon-expense");
            FontAwesomeIconView arrow = new FontAwesomeIconView();
            arrow.setGlyphName(isIncome ? "ARROW_UP" : "ARROW_DOWN");
            arrow.setSize("16");
            arrow.setStyle("-fx-fill: " + (isIncome ? "#10B981" : "#EF4444") + ";");
            iconPane.getChildren().add(arrow);

            // Content
            VBox contentBox = new VBox(2);
            HBox.setHgrow(contentBox, Priority.ALWAYS);

            HBox catAmountRow = new HBox();
            catAmountRow.setAlignment(Pos.CENTER_LEFT);
            Label catLabel = new Label((String) op.get("category"));
            catLabel.getStyleClass().add("recent-op-category");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            double amount = (Double) op.get("amount");
            Label amountLabel = new Label((isIncome ? "+" : "-") + formatCurrency(amount));
            amountLabel.getStyleClass().add(isIncome ? "recent-op-amount-income" : "recent-op-amount-expense");
            catAmountRow.getChildren().addAll(catLabel, spacer, amountLabel);

            Label commentLabel = new Label((String) op.get("comment"));
            commentLabel.getStyleClass().add("recent-op-comment");

            contentBox.getChildren().addAll(catAmountRow, commentLabel);

            // Date/time
            VBox dateBox = new VBox(2);
            dateBox.setAlignment(Pos.CENTER_RIGHT);
            Label dateLabel = new Label((String) op.get("date"));
            dateLabel.getStyleClass().add("recent-op-date");
            Label timeLabel = new Label((String) op.get("time"));
            timeLabel.getStyleClass().add("recent-op-time");
            dateBox.getChildren().addAll(dateLabel, timeLabel);

            row.getChildren().addAll(iconPane, contentBox, dateBox);
            recentOperationsContainer.getChildren().add(row);
        }
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
