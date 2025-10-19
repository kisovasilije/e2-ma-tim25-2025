package com.example.rpg.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "equipment")
public class Equipment {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String type;

    @ColumnInfo
    public int ppBonus;

    @ColumnInfo(index = true)
    public long playerOwnerId;

    public Equipment(String name, String type, int ppBonus, long playerOwnerId) {
        this.name = name;
        this.type = type;
        this.ppBonus = ppBonus;
        this.playerOwnerId = playerOwnerId;
    }
}

