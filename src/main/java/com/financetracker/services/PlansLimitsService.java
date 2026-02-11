package com.financetracker.services;

import com.financetracker.models.SpendingLimit;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис лимитов по категориям.
 * Сейчас использует mock данные, в будущем будет работать с REST API.
 */
public class PlansLimitsService {

    private static PlansLimitsService instance;
    private final List<SpendingLimit> limits;

    private PlansLimitsService() {
        this.limits = new ArrayList<>();
        initializeMockLimits();
    }

    public static synchronized PlansLimitsService getInstance() {
        if (instance == null) {
            instance = new PlansLimitsService();
        }
        return instance;
    }

    private void initializeMockLimits() {
        limits.add(new SpendingLimit("1", "Продукты", "Февраль 2026", 18500, 20000));
        limits.add(new SpendingLimit("2", "Транспорт", "Февраль 2026", 12300, 15000));
        limits.add(new SpendingLimit("3", "Развлечения", "Февраль 2026", 8900, 10000));
        limits.add(new SpendingLimit("4", "Здоровье", "Февраль 2026", 15200, 15000));
    }

    public List<SpendingLimit> getAllLimits() {
        return new ArrayList<>(limits);
    }

    public double getTotalLimit() {
        return limits.stream().mapToDouble(SpendingLimit::getLimitAmount).sum();
    }

    public double getTotalSpent() {
        return limits.stream().mapToDouble(SpendingLimit::getSpent).sum();
    }

    public double getTotalRemaining() {
        return getTotalLimit() - getTotalSpent();
    }
}
