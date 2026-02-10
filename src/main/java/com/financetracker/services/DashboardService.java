package com.financetracker.services;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Сервис для работы с данными Dashboard
 * Сейчас использует mock данные, в будущем будет работать с REST API
 */
public class DashboardService {
    
    private static DashboardService instance;
    
    private DashboardService() {
    }
    
    public static synchronized DashboardService getInstance() {
        if (instance == null) {
            instance = new DashboardService();
        }
        return instance;
    }
    
    /**
     * Получение баланса
     */
    public double getBalance() {
        return 245680.0;
    }
    
    /**
     * Получение доходов
     */
    public double getIncome() {
        return 320450.0;
    }
    
    /**
     * Получение расходов
     */
    public double getExpenses() {
        return 74770.0;
    }
    
    /**
     * Получение экономии
     */
    public double getSavings() {
        return 245680.0;
    }
    
    /**
     * Получение расходов по категориям
     */
    public Map<String, Double> getCategoryExpenses() {
        Map<String, Double> categories = new LinkedHashMap<>();
        
        categories.put("Продукты", 18000.0);
        categories.put("Транспорт", 12000.0);
        categories.put("Развлечения", 9000.0);
        categories.put("Здоровье", 15000.0);
        categories.put("Одежда", 8000.0);
        categories.put("Прочее", 12770.0);
        
        return categories;
        
        /* Будущая реализация с API:
        try {
            Response<Map<String, Double>> response = apiService.getCategoryExpenses();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            return new HashMap<>();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения данных", e);
        }
        */
    }
    
    /**
     * Получение доходов по месяцам
     */
    public Map<String, Double> getMonthlyIncome() {
        Map<String, Double> income = new LinkedHashMap<>();
        
        income.put("Янв", 280000.0);
        income.put("Фев", 295000.0);
        income.put("Мар", 310000.0);
        income.put("Апр", 305000.0);
        income.put("Май", 320450.0);
        
        return income;
    }
    
    /**
     * Получение расходов по месяцам
     */
    public Map<String, Double> getMonthlyExpenses() {
        Map<String, Double> expenses = new LinkedHashMap<>();
        
        expenses.put("Янв", 65000.0);
        expenses.put("Фев", 68000.0);
        expenses.put("Мар", 70000.0);
        expenses.put("Апр", 72000.0);
        expenses.put("Май", 74770.0);
        
        return expenses;
    }
    
    /**
     * Получение изменения баланса (в процентах)
     */
    public double getBalanceChange() {
        return 12.5;
    }
    
    /**
     * Получение изменения доходов (в процентах)
     */
    public double getIncomeChange() {
        return 8.2;
    }
    
    /**
     * Получение изменения расходов (в процентах)
     */
    public double getExpensesChange() {
        return -3.1;
    }
    
    /**
     * Получение изменения экономии (в процентах)
     */
    public double getSavingsChange() {
        return 15.8;
    }
}
