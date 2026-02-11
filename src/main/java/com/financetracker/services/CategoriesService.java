package com.financetracker.services;

import com.financetracker.models.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с категориями
 * Сейчас использует mock данные, в будущем будет работать с REST API
 */
public class CategoriesService {
    
    private static CategoriesService instance;
    private final List<Category> categories;
    
    private CategoriesService() {
        this.categories = new ArrayList<>();
        initializeMockCategories();
    }
    
    public static synchronized CategoriesService getInstance() {
        if (instance == null) {
            instance = new CategoriesService();
        }
        return instance;
    }
    
    /**
     * Инициализация тестовых категорий
     */
    private void initializeMockCategories() {
        // ========== КАТЕГОРИИ РАСХОДОВ ==========
        
        // Продукты
        categories.add(new Category(
            "1",
            "Продукты",
            "SHOPPING_CART",
            Category.CategoryType.EXPENSE,
            185,
            52300.0,
            "#3B82F6"  // синий
        ));
        
        // Транспорт
        categories.add(new Category(
            "2",
            "Транспорт",
            "CAR",
            Category.CategoryType.EXPENSE,
            92,
            28900.0,
            "#06B6D4"  // голубой
        ));
        
        // Здоровье
        categories.add(new Category(
            "3",
            "Здоровье",
            "MEDKIT",
            Category.CategoryType.EXPENSE,
            34,
            45200.0,
            "#10B981"  // зеленый
        ));
        
        // Одежда
        categories.add(new Category(
            "4",
            "Одежда",
            "SHOPPING_BAG",
            Category.CategoryType.EXPENSE,
            23,
            31400.0,
            "#EC4899"  // розовый
        ));
        
        // Развлечения
        categories.add(new Category(
            "5",
            "Развлечения",
            "GAMEPAD",
            Category.CategoryType.EXPENSE,
            67,
            38900.0,
            "#F59E0B"  // оранжевый
        ));
        
        // Коммунальные
        categories.add(new Category(
            "6",
            "Коммунальные",
            "HOME",
            Category.CategoryType.EXPENSE,
            12,
            19500.0,
            "#6366F1"  // индиго
        ));
        
        // ========== КАТЕГОРИИ ДОХОДОВ ==========
        
        // Зарплата
        categories.add(new Category(
            "7",
            "Зарплата",
            "BRIEFCASE",
            Category.CategoryType.INCOME,
            5,
            425000.0,
            "#10B981"  // зеленый
        ));
        
        // Фриланс
        categories.add(new Category(
            "8",
            "Фриланс",
            "LAPTOP",
            Category.CategoryType.INCOME,
            12,
            180000.0,
            "#3B82F6"  // синий
        ));
        
        // ========== УНИВЕРСАЛЬНЫЕ КАТЕГОРИИ ==========
        
        // Подарки
        categories.add(new Category(
            "9",
            "Подарки",
            "GIFT",
            Category.CategoryType.UNIVERSAL,
            8,
            15600.0,
            "#EC4899"  // розовый
        ));
    }
    
    /**
     * Получение всех категорий
     */
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }
    
    /**
     * Получение категорий по типу
     */
    public List<Category> getCategoriesByType(Category.CategoryType type) {
        return categories.stream()
            .filter(cat -> cat.getType() == type)
            .collect(Collectors.toList());
    }
    
    /**
     * Получение категории по ID
     */
    public Category getCategoryById(String id) {
        return categories.stream()
            .filter(cat -> cat.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Получение категории по имени
     */
    public Category getCategoryByName(String name) {
        return categories.stream()
            .filter(cat -> cat.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Добавление новой категории
     */
    public boolean addCategory(Category category) {
        categories.add(category);
        return true;
        
        /* Будущая реализация с API:
        try {
            Response<Category> response = apiService.createCategory(category);
            return response.isSuccessful();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания категории", e);
        }
        */
    }
    
    /**
     * Обновление категории
     */
    public boolean updateCategory(Category category) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(category.getId())) {
                categories.set(i, category);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Удаление категории
     */
    public boolean deleteCategory(String id) {
        return categories.removeIf(cat -> cat.getId().equals(id));
    }
}
