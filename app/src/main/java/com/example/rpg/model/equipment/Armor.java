package com.example.rpg.model.equipment;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(
        tableName = "armors",
        indices = @Index(value = "name", unique = true)
)
public class Armor extends Equipment {
    /**
     * It is the armor type that determines behaviour of the <u>bonus</u> field of the Equipment base class
     */
    private ArmorType armorType;

    public ArmorType getArmorType() {
        return armorType;
    }

    public void setArmorType(ArmorType type) {
        armorType = type;
    }
}
