package com.financetracker.services;

import com.financetracker.models.Operation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Сервис для работы с операциями
 * Сейчас использует mock данные, в будущем будет работать с REST API
 */
public class OperationsService {
    
    private static OperationsService instance;
    private final List<Operation> operations;
    
    private OperationsService() {
        this.operations = new ArrayList<>();
        initializeMockOperations();
    }
    
    public static synchronized OperationsService getInstance() {
        if (instance == null) {
            instance = new OperationsService();
        }
        return instance;
    }
    
    /**
     * Инициализация тестовых операций
     */
    private void initializeMockOperations() {
        // Операция 1 - Расход
        Operation op1 = new Operation(
            "1",
            LocalDate.of(2026, 2, 4),
            Operation.OperationType.EXPENSE,
            "Продукты",
            "Супермаркет Пятёрочка",
            3450.0
        );
        op1.addTag("продукты");
        op1.addTag("еда");
        operations.add(op1);
        
        // Операция 2 - Доход
        Operation op2 = new Operation(
            "2",
            LocalDate.of(2026, 2, 3),
            Operation.OperationType.INCOME,
            "Зарплата",
            "Ежемесячная зарплата",
            85000.0
        );
        op2.addTag("работа");
        operations.add(op2);
        
        // Операция 3 - Расход
        Operation op3 = new Operation(
            "3",
            LocalDate.of(2026, 2, 3),
            Operation.OperationType.EXPENSE,
            "Транспорт",
            "Заправка автомобиля",
            1200.0
        );
        op3.addTag("авто");
        op3.addTag("бензин");
        operations.add(op3);
        
        // Операция 4 - Расход
        Operation op4 = new Operation(
            "4",
            LocalDate.of(2026, 2, 2),
            Operation.OperationType.EXPENSE,
            "Развлечения",
            "Кино с семьей",
            2800.0
        );
        op4.addTag("досуг");
        op4.addTag("семья");
        operations.add(op4);
        
        // Операция 5 - Доход
        Operation op5 = new Operation(
            "5",
            LocalDate.of(2026, 2, 1),
            Operation.OperationType.INCOME,
            "Фриланс",
            "Проект веб-дизайна",
            15000.0
        );
        op5.addTag("работа");
        op5.addTag("доход");
        operations.add(op5);
        
        // Операция 6 - Расход
        Operation op6 = new Operation(
            "6",
            LocalDate.of(2026, 2, 1),
            Operation.OperationType.EXPENSE,
            "Здоровье",
            "Стоматолог",
            4500.0
        );
        op6.addTag("здоровье");
        op6.addTag("врач");
        operations.add(op6);
        
        // Операция 7 - Расход
        Operation op7 = new Operation(
            "7",
            LocalDate.of(2026, 1, 31),
            Operation.OperationType.EXPENSE,
            "Одежда",
            "Зимняя куртка",
            7800.0
        );
        op7.addTag("одежда");
        op7.addTag("зима");
        operations.add(op7);
        
        // Операция 8 - Доход
        Operation op8 = new Operation(
            "8",
            LocalDate.of(2026, 1, 30),
            Operation.OperationType.INCOME,
            "Зарплата",
            "Премия за квартал",
            25000.0
        );
        op8.addTag("работа");
        operations.add(op8);
        
        // Операция 9 - Расход
        Operation op9 = new Operation(
            "9",
            LocalDate.of(2026, 1, 29),
            Operation.OperationType.EXPENSE,
            "Продукты",
            "Рынок выходного дня",
            1850.0
        );
        op9.addTag("продукты");
        operations.add(op9);
        
        // Операция 10 - Расход
        Operation op10 = new Operation(
            "10",
            LocalDate.of(2026, 1, 28),
            Operation.OperationType.EXPENSE,
            "Транспорт",
            "Метро (пополнение карты)",
            2000.0
        );
        op10.addTag("транспорт");
        operations.add(op10);
        
        // Операция 11 - Расход
        Operation op11 = new Operation(
            "11",
            LocalDate.of(2026, 1, 27),
            Operation.OperationType.EXPENSE,
            "Развлечения",
            "Концерт",
            3500.0
        );
        op11.addTag("досуг");
        operations.add(op11);
        
        // Операция 12 - Доход
        Operation op12 = new Operation(
            "12",
            LocalDate.of(2026, 1, 25),
            Operation.OperationType.INCOME,
            "Фриланс",
            "Консультация клиента",
            8000.0
        );
        op12.addTag("работа");
        operations.add(op12);
    }
    
    /**
     * Получение всех операций
     */
    public List<Operation> getAllOperations() {
        return new ArrayList<>(operations);
        
        /* Будущая реализация с API:
        try {
            Response<List<Operation>> response = apiService.getOperations();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения операций", e);
        }
        */
    }
    
    /**
     * Получение операции по ID
     */
    public Operation getOperationById(String id) {
        return operations.stream()
            .filter(op -> op.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Добавление новой операции
     */
    public boolean addOperation(Operation operation) {
        operations.add(operation);
        return true;
        
        /* Будущая реализация с API:
        try {
            Response<Operation> response = apiService.createOperation(operation);
            return response.isSuccessful();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания операции", e);
        }
        */
    }
    
    /**
     * Обновление операции
     */
    public boolean updateOperation(Operation operation) {
        for (int i = 0; i < operations.size(); i++) {
            if (operations.get(i).getId().equals(operation.getId())) {
                operations.set(i, operation);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Удаление операции
     */
    public boolean deleteOperation(String id) {
        return operations.removeIf(op -> op.getId().equals(id));
    }
    
    /**
     * Получение операций за период
     */
    public List<Operation> getOperationsByDateRange(LocalDate startDate, LocalDate endDate) {
        return operations.stream()
            .filter(op -> !op.getDate().isBefore(startDate) && !op.getDate().isAfter(endDate))
            .toList();
    }
    
    /**
     * Получение операций по типу
     */
    public List<Operation> getOperationsByType(Operation.OperationType type) {
        return operations.stream()
            .filter(op -> op.getType() == type)
            .toList();
    }
    
    /**
     * Получение операций по категории
     */
    public List<Operation> getOperationsByCategory(String category) {
        return operations.stream()
            .filter(op -> op.getCategory().equals(category))
            .toList();
    }
}
