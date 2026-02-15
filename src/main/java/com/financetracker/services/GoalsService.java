package com.financetracker.services;

import com.financetracker.models.Debt;
import com.financetracker.models.Goal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис целей.
 * Сейчас использует mock данные, в будущем будет работать с REST API.
 */
public class GoalsService {

    private static GoalsService instance;
    private final List<Goal> goals;
    private final List<Debt> debts;

    private GoalsService() {
        this.goals = new ArrayList<>();
        this.debts = new ArrayList<>();
        initializeMockGoals();
        initializeMockDebts();
    }

    public static synchronized GoalsService getInstance() {
        if (instance == null) {
            instance = new GoalsService();
        }
        return instance;
    }

    private void initializeMockGoals() {
        goals.add(new Goal(
            "1",
            "Отпуск в Европе",
            "🏖️",
            Goal.GoalType.GOAL,
            200_000,
            145_000,
            LocalDate.of(2026, 7, 15),
            161,
            "#3B82F6"
        ));

        goals.add(new Goal(
            "2",
            "Новый MacBook Pro",
            "💻",
            Goal.GoalType.GOAL,
            250_000,
            180_000,
            LocalDate.of(2026, 5, 30),
            117,
            "#8B5CF6"
        ));
    }

    private void initializeMockDebts() {
        debts.add(new Debt(
            "d1", Debt.DebtType.BORROWED, "Алексей Сидоров", 25000,
            LocalDate.of(2025, 12, 15), LocalDate.of(2026, 3, 15),
            Debt.DebtStatus.ACTIVE, "Займ на ремонт"
        ));
        debts.add(new Debt(
            "d2", Debt.DebtType.LENT, "Мария Козлова", 15000,
            LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10),
            Debt.DebtStatus.ACTIVE, "Помощь с переездом"
        ));
        debts.add(new Debt(
            "d3", Debt.DebtType.BORROWED, "Дмитрий Волков", 10000,
            LocalDate.of(2025, 10, 1), LocalDate.of(2025, 12, 31),
            Debt.DebtStatus.PAID, "Займ на учёбу"
        ));
        debts.add(new Debt(
            "d4", Debt.DebtType.LENT, "Елена Новикова", 5000,
            LocalDate.of(2025, 11, 20), LocalDate.of(2026, 1, 20),
            Debt.DebtStatus.PAID, "Помощь с покупкой"
        ));
    }

    public List<Goal> getAllGoals() {
        return new ArrayList<>(goals);
    }

    public List<Debt> getAllDebts() {
        return new ArrayList<>(debts);
    }
}
