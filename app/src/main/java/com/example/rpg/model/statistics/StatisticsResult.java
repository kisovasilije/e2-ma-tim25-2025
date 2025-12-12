package com.example.rpg.model.statistics;

public class StatisticsResult {
    public int activeDays;

    public TaskStatusStats taskStatusStats;

    public StatisticsResult() {
        activeDays = 0;
        taskStatusStats = new TaskStatusStats();
    }
}
