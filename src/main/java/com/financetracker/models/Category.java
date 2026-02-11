package com.financetracker.models;

/**
 * Модель категории
 */
public class Category {
    
    private String id;
    private String name;
    private String icon;
    private CategoryType type;
    private int operationsCount;
    private double totalAmount;
    private String color;
    
    public enum CategoryType {
        EXPENSE("Расход"),
        INCOME("Доход"),
        UNIVERSAL("Универсальная");
        
        private final String displayName;
        
        CategoryType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public Category() {
    }
    
    public Category(String id, String name, String icon, CategoryType type, 
                   int operationsCount, double totalAmount, String color) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.type = type;
        this.operationsCount = operationsCount;
        this.totalAmount = totalAmount;
        this.color = color;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public CategoryType getType() {
        return type;
    }
    
    public void setType(CategoryType type) {
        this.type = type;
    }
    
    public int getOperationsCount() {
        return operationsCount;
    }
    
    public void setOperationsCount(int operationsCount) {
        this.operationsCount = operationsCount;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", type=" + type +
                ", operationsCount=" + operationsCount +
                ", totalAmount=" + totalAmount +
                ", color='" + color + '\'' +
                '}';
    }
}
