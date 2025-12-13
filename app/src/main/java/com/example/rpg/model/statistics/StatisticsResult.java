package com.example.rpg.model.statistics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatisticsResult {
    public int activeDays;

    public TaskStatusStats taskStatusStats;

    public int doneTasksLongestStreak;

    public List<CategoryCount> doneTasksPerCategory;

    public Map<String, Float> avgDoneTasksDifficulty;

    public Map<String, Integer> xpPerDay;

    public StatisticsResult() {
        activeDays = 0;
        taskStatusStats = new TaskStatusStats();
        doneTasksLongestStreak = 0;
        doneTasksPerCategory = new ArrayList<>();
        avgDoneTasksDifficulty = new TreeMap<>();
        xpPerDay = new LinkedHashMap<>();
    }
}
