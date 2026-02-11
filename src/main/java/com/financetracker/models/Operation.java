package com.financetracker.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель операции (транзакции)
 */
public class Operation {
    
    private String id;
    private LocalDate date;
    private OperationType type;
    private String category;
    private String comment;
    private double amount;
    private List<String> tags;
    
    public enum OperationType {
        INCOME("Доход"),
        EXPENSE("Расход");
        
        private final String displayName;
        
        OperationType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public Operation() {
        this.tags = new ArrayList<>();
    }
    
    public Operation(String id, LocalDate date, OperationType type, String category, 
                    String comment, double amount) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.category = category;
        this.comment = comment;
        this.amount = amount;
        this.tags = new ArrayList<>();
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public OperationType getType() {
        return type;
    }
    
    public void setType(OperationType type) {
        this.type = type;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public void addTag(String tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        this.tags.remove(tag);
    }
    
    @Override
    public String toString() {
        return "Operation{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", type=" + type +
                ", category='" + category + '\'' +
                ", comment='" + comment + '\'' +
                ", amount=" + amount +
                ", tags=" + tags +
                '}';
    }
}
