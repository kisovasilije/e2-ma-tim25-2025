package com.example.rpg.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bosses")
public class Boss {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public int level;

    @ColumnInfo
    public int hp;
    @ColumnInfo
    public int rewardCoins;

    public Boss(String name, int level, int rewardCoins) {
        this.name = name;
        this.level = level;
        this.hp = calculateHP(level);
        this.rewardCoins = rewardCoins;
    }

    private int calculateHP(int level) {
        if (level == 1) return 200;
        int prevHp = 200;
        for (int i = 2; i <= level; i++) {
            prevHp = prevHp * 2 + prevHp / 2;
        }
        return prevHp;
    }
}
