package com.example.rpg.model;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "equipments")
public class Equipment {
    public static final int POTION = 0;
    public static final int ARMOR = 1;
    public static final int WEAPON = 2;

    public static final int GLOVES_ARMOR = 0;
    public static final int BODY_ARMOR = 1;
    public static final int BOOTS_ARMOR = 2;

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    public int type;

    public int armorType;

    public int ppBonus;

    public Equipment(String name, int type, int armorType, int ppBonus) {
        this.name = name;
        this.type = type;
        this.armorType = armorType;
        this.ppBonus = ppBonus;
    }
}
