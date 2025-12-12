package com.example.rpg.services;

import com.example.rpg.database.AppDatabase;
import com.example.rpg.model.statistics.StatisticsResult;
import com.example.rpg.model.statistics.TaskStatusStats;
import com.example.rpg.utils.DateUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
}
