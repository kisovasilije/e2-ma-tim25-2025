package com.example.rpg.services;

import com.example.rpg.database.AppDatabase;
import com.example.rpg.model.statistics.CategoryCount;
import com.example.rpg.model.statistics.StatisticsResult;
import com.example.rpg.model.statistics.TaskStatusStats;
import com.example.rpg.utils.DateUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatisticsService {
    private final AppDatabase db;

    private long userId;

    public StatisticsService(AppDatabase db) {
        this.db = db;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public StatisticsResult calculate() {
        // Create initial result envelope
        var result = new StatisticsResult();

        // 1. Number of active days
        result.activeDays = calculateActiveDays();

        // 2. Number of tasks per status
        result.taskStatusStats = calculateTaskStatusStats();

        // 3. Longest streak of done tasks
        result.doneTasksLongestStreak = calculateLongestDoneTasksStreak();

        // 4. Done tasks count per category
        result.doneTasksPerCategory = countDoneTasksPerCategory();

        // 5. Average done tasks difficulty
        result.avgDoneTasksDifficulty = calculateAverageDoneTaskDifficulty();

        // 6. Xp earned in past 7 days
        result.xpPerDay = calculateXpPerDay();

        // Return result
        return result;
    }

    private int calculateActiveDays() {
        var consecutive = 0;
        var day = DateUtil.toDayKey(new Date());

        while(dailyStatExistsForDay(day)) {
            consecutive++;
            day = DateUtil.toDayKey(Date.from(Instant.now().minus(consecutive, ChronoUnit.DAYS)));
        }

        return consecutive;
    }

    private boolean dailyStatExistsForDay(String day) {
        return db.dailyStatisticsDao().getForDay(userId, day) != null;
    }

    private TaskStatusStats calculateTaskStatusStats() {
        var stats = new TaskStatusStats();

        var dailyStats = db.dailyStatisticsDao().getAllByUserId(userId);
        for (var ds : dailyStats) {
            stats.tasksCreated += ds.tasksCreated;
            stats.tasksDone += ds.tasksDone;
            stats.tasksUnfinished += ds.tasksUnfinished;
            stats.tasksCanceled += ds.tasksCanceled;
        }

        return stats;
    }

    private int calculateLongestDoneTasksStreak() {
        int current = 0, longest = 0;

        var dailyStats = db.dailyStatisticsDao().getAllByUserId(userId);
        for (var stat : dailyStats) {
            if (stat.tasksUnfinished > 0) {
                current = 0;
                continue;
            }

            if (stat.tasksDone > 0) {
                current++;
                longest = Math.max(longest, current);
            }
        }

        return longest;
    }

    private List<CategoryCount> countDoneTasksPerCategory() {
        return db.taskDao().getDoneTaskCountByCategory(userId);
    }

    private Map<String, Float> calculateAverageDoneTaskDifficulty() {
        Map<String, Integer> sum = new HashMap<>();
        Map<String, Integer> count = new HashMap<>();

        var tasks = db.taskDao().getAllDoneByUserId(userId);
        for (var t : tasks) {
            var day = DateUtil.toDayKey(t.completionTime);

            sum.put(day, sum.getOrDefault(day, 0) + t.difficultyXP);
            count.put(day, count.getOrDefault(day, 0) + 1);
        }

        Map<String, Float> avg = new TreeMap<>();
        for (var day : sum.keySet()) {
            avg.put(day, sum.get(day) / (float) count.get(day));
        }

        return avg;
    }

    private Map<String, Integer> calculateXpPerDay() {
        Map<String, Integer> xpPerDay = new LinkedHashMap<>();

        var stats = db.dailyStatisticsDao().getFromDayByUserId(
                userId,
                DateUtil.toDayKey(Date.from(Instant.now().minus(6, ChronoUnit.DAYS)))
        );

        for (int i = 6; i >= 0; i--) {
            var day = DateUtil.toDayKey(Date.from(Instant.now().minus(i, ChronoUnit.DAYS)));
            xpPerDay.put(day, 0);
        }

        for (var s : stats) {
            xpPerDay.put(s.day, s.xpEarned);
        }

        return xpPerDay;
    }
}
