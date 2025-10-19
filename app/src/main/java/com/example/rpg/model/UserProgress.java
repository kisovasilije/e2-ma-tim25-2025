package com.example.rpg.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_progresses")
public class UserProgress {
    @PrimaryKey
    public long id;

    public int xp;

    public int level;

    /**
     * Previous level xp cap
     */
    public int plXpCap;

    /**
     * Power points
     */
    public int pp;

    /**
     * Previous level power points cap
     */
    public int plPpCap;

    public String title;

    public UserProgress(long id, int xp, int level, int plXpCap, int pp, int plPpCap, String title) {
        this.id = id;
        this.xp = xp;
        this.level = level;
        this.plXpCap = plXpCap;
        this.pp = pp;
        this.plPpCap = plPpCap;
        this.title = title;
    }

    public static UserProgress getDefault(long id) {
        return new UserProgress(id, 0, 1, 0, 0, 0, "Title1");
    }
}
