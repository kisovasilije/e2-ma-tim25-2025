package com.example.rpg.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(index = true)
    public String name;

    @ColumnInfo
    public int color;

    public Category(String name, int color) {
        this.name = name;
        this.color = color;
    }
}

