package com.example.rpg.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;

    public String name;

    public int xp;

    public boolean isPassed;

    public Task(long userId, String name, int xp, boolean isPassed) {
        this.userId = userId;
        this.name = name;
        this.xp = xp;
        this.isPassed = isPassed;
    }
}
