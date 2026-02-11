package com.financetracker.services;

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

    private GoalsService() {
        this.goals = new ArrayList<>();
        initializeMockGoals();
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

    public List<Goal> getAllGoals() {
        return new ArrayList<>(goals);
    }
}
