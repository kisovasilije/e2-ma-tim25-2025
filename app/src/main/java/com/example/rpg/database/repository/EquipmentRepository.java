package com.example.rpg.database.repository;

import com.example.rpg.database.AppDatabase;
import com.example.rpg.model.equipment.Equipment;

import java.util.ArrayList;
import java.util.List;

public class EquipmentRepository {
    private final AppDatabase db;

    public EquipmentRepository(AppDatabase db) {
        this.db = db;
    }

    public List<Equipment> getAll() {
        var potions = db.potionDao().getAll();
        var armors = db.armorDao().getAll();
        var weapons = db.weaponDao().getAll();

        List<Equipment> equipments = new ArrayList<>();
        equipments.addAll(potions);
        equipments.addAll(armors);
        equipments.addAll(weapons);

        return equipments;
    }

    public List<Equipment> getBuyable() {
        var potions = db.potionDao().getAll();
        var armors = db.armorDao().getAll();

        List<Equipment> equipments = new ArrayList<>();
        equipments.addAll(potions);
        equipments.addAll(armors);

        return equipments;
    }
}
