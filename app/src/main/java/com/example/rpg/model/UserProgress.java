package com.example.rpg.model;

import android.annotation.SuppressLint;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_progresses")
public class UserProgress {
    @PrimaryKey
    public long id;

    public int xp;

    public int level;

    public int xpCap;

    /**
     * Power points
     */
    public int pp;

    public int ppCap;

    public String title;

    public UserProgress(long id, int xp, int level, int xpCap, int pp, int ppCap, String title) {
        this.id = id;
        this.xp = xp;
        this.level = level;
        this.xpCap = xpCap;
        this.pp = pp;
        this.ppCap = ppCap;
        this.title = title;
    }

    public static UserProgress getDefault(long id) {
        return new UserProgress(id, 0, 1, 200, 0, 40, "Title1");
    }

    @SuppressLint("DefaultLocale")
    public void update(Task task) {
        xp += task.xp;
        pp += 20;

        if (xp < xpCap && pp < ppCap) {
            return;
        }

        if (xp >= xpCap) {
            xpCap = ((int) Math.ceil((double) (xpCap * 5) / 2 / 100.0)) * 100;
            level++;
            title = String.format("Title%d", level);
        }

        if (pp >= ppCap) {
            ppCap = ((int) Math.ceil((double) (ppCap * 7) / 4 / 10.0)) * 10;
        }
    }
}
