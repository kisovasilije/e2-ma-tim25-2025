package com.example.rpg.model.equipment;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(
        tableName = "potions",
        indices = @Index(value = "name", unique = true)
)
public class Potion extends Equipment {
    /**
     * It is the potion type that determines behaviour of <u>bonus</u> field of Equipment base class
     */
    private PotionType pType;

    public PotionType getPType() {
        return pType;
    }

    public void setPType(PotionType type) {
        pType = type;
    }
}
