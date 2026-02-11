package com.financetracker.models;

/**
 * Модель лимита расходов по категории.
 */
public class SpendingLimit {

    private String id;
    private String category;
    private String monthLabel;
    private double spent;
    private double limitAmount;

    public SpendingLimit(String id, String category, String monthLabel, double spent, double limitAmount) {
        this.id = id;
        this.category = category;
        this.monthLabel = monthLabel;
        this.spent = spent;
        this.limitAmount = limitAmount;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public double getSpent() {
        return spent;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public double getRemaining() {
        return limitAmount - spent;
    }

    public double getUsagePercent() {
        if (limitAmount <= 0) {
            return 0;
        }
        return (spent / limitAmount) * 100;
    }

    public double getProgressValue() {
        double progress = getUsagePercent() / 100.0;
        return Math.max(0.0, Math.min(progress, 1.0));
    }

    public boolean isExceeded() {
        return getUsagePercent() > 100;
    }

    public boolean isNearLimit() {
        return !isExceeded() && getUsagePercent() >= 80;
    }
}
