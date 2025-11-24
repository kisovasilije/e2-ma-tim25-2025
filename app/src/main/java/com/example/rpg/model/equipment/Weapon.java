package com.example.rpg.model.equipment;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(
        tableName = "weapons",
        indices = @Index(value = "name", unique = true)
)
public class Weapon extends Equipment {
    /**
     * It is the weapon type that determines behaviour of the <u>bonus</u> field of the Equipment base class
     */
    private WeaponType weaponType;

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(WeaponType type) {
        weaponType = type;
    }
}
