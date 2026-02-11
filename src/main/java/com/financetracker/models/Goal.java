package com.financetracker.models;

import java.time.LocalDate;

/**
 * Модель цели/долга
 */
public class Goal {
    
    private String id;
    private String name;
    private String icon;
    private GoalType type;
    private double targetAmount;
    private double currentAmount;
    private LocalDate deadline;
    private int daysRemaining;
    private String color;
    
    public enum GoalType {
        GOAL("Цель"),
        DEBT("Долг");
        
        private final String displayName;
        
        GoalType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public Goal() {
    }
    
    public Goal(String id, String name, String icon, GoalType type,
               double targetAmount, double currentAmount, LocalDate deadline,
               int daysRemaining, String color) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.type = type;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.daysRemaining = daysRemaining;
        this.color = color;
    }
    
    /**
     * Расчет процента выполнения
     */
    public double getProgress() {
        if (targetAmount == 0) return 0;
        return (currentAmount / targetAmount) * 100;
    }
    
    /**
     * Остаток до цели
     */
    public double getRemaining() {
        return targetAmount - currentAmount;
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
    
    public GoalType getType() {
        return type;
    }
    
    public void setType(GoalType type) {
        this.type = type;
    }
    
    public double getTargetAmount() {
        return targetAmount;
    }
    
    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }
    
    public double getCurrentAmount() {
        return currentAmount;
    }
    
    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }
    
    public LocalDate getDeadline() {
        return deadline;
    }
    
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
    
    public int getDaysRemaining() {
        return daysRemaining;
    }
    
    public void setDaysRemaining(int daysRemaining) {
        this.daysRemaining = daysRemaining;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return "Goal{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", progress=" + String.format("%.1f%%", getProgress()) +
                ", current=" + currentAmount +
                ", target=" + targetAmount +
                '}';
    }
}
