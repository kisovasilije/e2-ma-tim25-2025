package com.example.rpg.model;

import android.annotation.SuppressLint;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.rpg.database.daos.TaskDao;

import java.util.List;

@Entity(tableName = "user_progresses")
public class UserProgress {
    private final static int INIT_XP_CAP = 200;

    private final static int INIT_PP_UPGRADE = 40;

    private final static int INIT_LEVEL = 1;

    private final static int INIT_PP = 0;

    private final static int INIT_XP = 0;

    private final static String LEVEL_1_TITLE = "Warden of the Shattered Vale";

    private final static String LEVEL_2_TITLE = "Bladebound Heir of Emberfall";

    private final static String LEVEL_3_TITLE = "Harbinger of the Hollow Star";

    @PrimaryKey
    public long id;

    public int xp;

    public int level;

    /**
     * Power points
     */
    public int pp;

    public String title;

    public int coins;

    public UserProgress(long id, int xp, int level, int pp, String title) {
        this.id = id;
        this.xp = xp;
        this.level = level;
        this.pp = pp;
        this.title = title;
    }

    public static UserProgress getDefault(long id) {
        return new UserProgress(id, INIT_XP, INIT_LEVEL, INIT_PP, LEVEL_1_TITLE);
    }

    @SuppressLint("DefaultLocale")
    public void update(Task task) {
        xp += task.totalXP;
        if (!isLevelPassed()) {
            return;
        }

        upgradePp();

        level++;
        upgradeTitle();
    }

    private boolean isLevelPassed() {
        return xp > getCurrentXpCap();
    }

    private int getCurrentXpCap() {
        int cap = INIT_XP_CAP;

        for (int i = 1; i < level; i++) {
            cap = (int) Math.ceil(cap * 5.0 / 2.0);
        }

        return cap;
    }

    private void upgradePp() {
        pp += getNewPp();
    }

    private int getNewPp() {
        int bonus = INIT_PP_UPGRADE;

        for (int i = 1; i < level; i++) {
            bonus = (int) Math.ceil(bonus * 7.0 / 4.0);
        }

        return bonus;
    }

    private void upgradeTitle() {
        title = getNewTitle();
    }

    private String getNewTitle() {
        switch (level) {
            case 1:
                return LEVEL_1_TITLE;

            case 2:
                return LEVEL_2_TITLE;

            default:
                return LEVEL_3_TITLE;
        }
    }
}
