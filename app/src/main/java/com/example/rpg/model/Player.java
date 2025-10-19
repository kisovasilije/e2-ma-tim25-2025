package com.example.rpg.model;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

import com.example.rpg.database.daos.TaskDao;

import java.util.List;

@Entity(tableName = "players")
public class Player {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public int level;

    @ColumnInfo
    public int pp;

    @ColumnInfo
    public int coins;

    @Ignore
    public List<Equipment> equipment;

    public Player(String name, int level, int pp, int coins) {
        this.name = name;
        this.level = level;
        this.pp = pp;
        this.coins = coins;
    }

    @Ignore
    public int getTotalPP() {
        int bonus = 0;
        if (equipment != null) {
            for (Equipment e : equipment) bonus += e.ppBonus;
        }
        return pp + bonus;
    }
    @Ignore
    private static final int FIRST_BOSS_HP = 200;


    @Ignore
    public Boss getCurrentBoss() {
        int currentLevel = this.level > 0 ? this.level : 1;
        int hp = FIRST_BOSS_HP;

        for (int i = 1; i < currentLevel; i++) {
            hp = hp * 2 + hp / 2;
        }

        return new Boss("Boss " + currentLevel, hp, getCurrentBoss().rewardCoins);
    }
    @Ignore
    public double getSuccessRate(TaskDao taskDao, long stageId) {
        if (taskDao == null) return 0;

        List<Task> tasks = taskDao.getAllTasksForPlayerAndStage(this.id, stageId);
        //List<Task> tasks = taskDao.getAll();

        if (tasks == null || tasks.isEmpty()) return 0;

        int total = tasks.size();
        int done = 0;

        for (Task t : tasks) {
            if ("done".equalsIgnoreCase(t.status) || "completed".equalsIgnoreCase(t.status)) done++;
        }

        return ((double) done / total) * 100.0;

    }
}
