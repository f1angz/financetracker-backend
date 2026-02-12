package com.financetracker.controllers;

import com.financetracker.models.Operation;
import com.financetracker.models.User;
import com.financetracker.services.AuthService;
import com.financetracker.services.OperationsService;
import com.financetracker.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для экрана операций
 */
public class OperationsController {
    
    // Top bar
    @FXML private TextField searchField;
    
    // User profile
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    
    // Filters
    @FXML private TextField tableSearchField;
    @FXML private ComboBox<String> typeFilterComboBox;
    @FXML private ComboBox<String> categoryFilterComboBox;
    
    // Table
    @FXML private TableView<Operation> operationsTable;
    @FXML private TableColumn<Operation, LocalDate> dateColumn;
    @FXML private TableColumn<Operation, Operation.OperationType> typeColumn;
    @FXML private TableColumn<Operation, String> categoryColumn;
    @FXML private TableColumn<Operation, String> commentColumn;
    @FXML private TableColumn<Operation, Double> amountColumn;
    
    // Pagination
    @FXML private Label pageLabel;
    
    // Menu items
    @FXML private Button dashboardMenuItem;
    @FXML private Button operationsMenuItem;
    @FXML private Button categoriesMenuItem;
    @FXML private Button plansLimitsMenuItem;
    @FXML private Button goalsDebtsMenuItem;
    @FXML private Button settingsMenuItem;
    
    private final AuthService authService;
    private final OperationsService operationsService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private ObservableList<Operation> allOperations;
    private ObservableList<Operation> filteredOperations;
    
    private int currentPage = 1;
    private int itemsPerPage = 10;
    private int totalPages = 1;
    
    public OperationsController() {
        this.authService = AuthService.getInstance();
        this.operationsService = OperationsService.getInstance();
        this.allOperations = FXCollections.observableArrayList();
        this.filteredOperations = FXCollections.observableArrayList();
    }
    
    /**
     * Инициализация контроллера
     */
    @FXML
    public void initialize() {
        loadUserInfo();
        setupFilters();
        setupTable();
        loadOperations();
        setupSearch();
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
     * Настройка фильтров
     */
    private void setupFilters() {
        // Фильтр по типу операции
        typeFilterComboBox.getItems().addAll("Все операции", "Доходы", "Расходы");
        typeFilterComboBox.setValue("Все операции");
        typeFilterComboBox.setOnAction(e -> applyFilters());
        
        // Фильтр по категории
        categoryFilterComboBox.getItems().addAll(
            "Все категории",
            "Продукты",
            "Транспорт",
            "Развлечения",
            "Здоровье",
            "Одежда",
            "Зарплата",
            "Фриланс"
        );
        categoryFilterComboBox.setValue("Все категории");
        categoryFilterComboBox.setOnAction(e -> applyFilters());
    }
    
    /**
     * Настройка таблицы
     */
    private void setupTable() {
        // Настройка колонок
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(column -> new TableCell<Operation, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateFormatter));
                }
            }
        });
        
        // Колонка типа с иконками
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setCellFactory(column -> new TableCell<Operation, Operation.OperationType>() {
            @Override
            protected void updateItem(Operation.OperationType type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox hbox = createTypeCell(type);
                    setGraphic(hbox);
                    setText(null);
                }
            }
        });
        
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        
        // Колонка комментария с тегами
        commentColumn.setCellFactory(column -> new TableCell<Operation, String>() {
            @Override
            protected void updateItem(String comment, boolean empty) {
                super.updateItem(comment, empty);
                if (empty || comment == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Operation operation = getTableView().getItems().get(getIndex());
                    setGraphic(createCommentCell(operation));
                    setText(null);
                }
            }
        });
        
        // Колонка суммы с цветом
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setCellFactory(column -> new TableCell<Operation, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Operation operation = getTableView().getItems().get(getIndex());
                    String sign = operation.getType() == Operation.OperationType.INCOME ? "+" : "-";
                    String color = operation.getType() == Operation.OperationType.INCOME ? 
                                 "#10B981" : "#EF4444";
                    
                    setText(sign + "₽ " + String.format("%,.0f", Math.abs(amount)));
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: 600;");
                }
            }
        });
        
        // Стиль таблицы
        operationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    /**
     * Создание ячейки типа операции
     */
    private HBox createTypeCell(Operation.OperationType type) {
        HBox hbox = new HBox(8);
        hbox.setAlignment(Pos.CENTER_LEFT);
        
        // Иконка
        FontAwesomeIconView icon = new FontAwesomeIconView();
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(24, 24);
        iconContainer.setStyle("-fx-background-radius: 4px;");
        
        if (type == Operation.OperationType.INCOME) {
            icon.setGlyphName("ARROW_UP");
            icon.setSize("12");
            icon.setFill(Color.web("#10B981"));
            iconContainer.setStyle(iconContainer.getStyle() + "-fx-background-color: #D1FAE5;");
        } else {
            icon.setGlyphName("ARROW_DOWN");
            icon.setSize("12");
            icon.setFill(Color.web("#EF4444"));
            iconContainer.setStyle(iconContainer.getStyle() + "-fx-background-color: #FEE2E2;");
        }
        
        iconContainer.getChildren().add(icon);
        
        // Текст
        Label label = new Label(type.getDisplayName());
        label.setStyle("-fx-font-size: 13px;");
        
        hbox.getChildren().addAll(iconContainer, label);
        return hbox;
    }
    
    /**
     * Создание ячейки комментария с тегами
     */
    private HBox createCommentCell(Operation operation) {
        HBox hbox = new HBox(8);
        hbox.setAlignment(Pos.CENTER_LEFT);
        
        // Комментарий
        Label commentLabel = new Label(operation.getComment());
        commentLabel.setStyle("-fx-font-size: 13px;");
        
        hbox.getChildren().add(commentLabel);
        
        // Теги
        for (String tag : operation.getTags()) {
            Label tagLabel = new Label(tag);
            tagLabel.setStyle(
                "-fx-background-color: #EFF6FF; " +
                "-fx-text-fill: #3B82F6; " +
                "-fx-padding: 2px 8px; " +
                "-fx-background-radius: 4px; " +
                "-fx-font-size: 11px;"
            );
            hbox.getChildren().add(tagLabel);
        }
        
        return hbox;
    }
    
    /**
     * Загрузка операций
     */
    private void loadOperations() {
        List<Operation> operations = operationsService.getAllOperations();
        allOperations.setAll(operations);
        filteredOperations.setAll(operations);
        
        updatePagination();
        updateTable();
    }
    
    /**
     * Настройка поиска
     */
    private void setupSearch() {
        tableSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }
    
    /**
     * Применение фильтров
     */
    private void applyFilters() {
        String searchText = tableSearchField.getText().toLowerCase();
        String typeFilter = typeFilterComboBox.getValue();
        String categoryFilter = categoryFilterComboBox.getValue();
        
        filteredOperations.setAll(
            allOperations.stream()
                .filter(op -> {
                    // Фильтр по тексту поиска
                    boolean matchesSearch = searchText.isEmpty() ||
                        op.getComment().toLowerCase().contains(searchText) ||
                        op.getCategory().toLowerCase().contains(searchText);
                    
                    // Фильтр по типу
                    boolean matchesType = typeFilter.equals("Все операции") ||
                        (typeFilter.equals("Доходы") && op.getType() == Operation.OperationType.INCOME) ||
                        (typeFilter.equals("Расходы") && op.getType() == Operation.OperationType.EXPENSE);
                    
                    // Фильтр по категории
                    boolean matchesCategory = categoryFilter.equals("Все категории") ||
                        op.getCategory().equals(categoryFilter);
                    
                    return matchesSearch && matchesType && matchesCategory;
                })
                .collect(Collectors.toList())
        );
        
        currentPage = 1;
        updatePagination();
        updateTable();
    }
    
    /**
     * Обновление пагинации
     */
    private void updatePagination() {
        totalPages = (int) Math.ceil((double) filteredOperations.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;
        pageLabel.setText(currentPage + " / " + totalPages);
    }
    
    /**
     * Обновление таблицы
     */
    private void updateTable() {
        int fromIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, filteredOperations.size());
        
        if (fromIndex < filteredOperations.size()) {
            List<Operation> pageData = filteredOperations.subList(fromIndex, toIndex);
            operationsTable.setItems(FXCollections.observableArrayList(pageData));
        } else {
            operationsTable.setItems(FXCollections.observableArrayList());
        }
    }
    
    // ========== PAGINATION ==========
    
    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePagination();
            updateTable();
        }
    }
    
    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePagination();
            updateTable();
        }
    }
    
    // ========== ACTIONS ==========
    
    @FXML
    private void handleAddOperation() {
        showComingSoon("Добавление операции");
    }
    
    @FXML
    private void handleShowFilters() {
        showComingSoon("Расширенные фильтры");
    }
    
    @FXML
    private void handleExport() {
        showComingSoon("Экспорт данных");
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
        // Уже на Operations
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
