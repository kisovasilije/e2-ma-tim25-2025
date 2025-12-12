package com.example.rpg.model.statistics;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "daily_statistics",
        indices = {@Index(value = {"userId", "day"}, unique = true)}
)
public class DailyStatistics {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;

    public String day;

    public int tasksCreated;

    public int tasksDone;

    public int tasksUnfinished;

    public int tasksCanceled;

    public int xpEarned;

    public DailyStatistics(long userId, String day) {
        this.userId = userId;
        this.day = day;
        tasksDone = 0;
        tasksUnfinished = 0;
        tasksCanceled = 0;
        xpEarned = 0;
    }
}
