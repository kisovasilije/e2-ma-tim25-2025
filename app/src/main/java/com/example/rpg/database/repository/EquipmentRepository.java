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
        return gatherEquipment();
    }

    public List<Equipment> getBuyable() {
        var potions = db.potionDao().getAll();
        var armors = db.armorDao().getAll();

        List<Equipment> equipments = new ArrayList<>();
        equipments.addAll(potions);
        equipments.addAll(armors);

        return equipments;
    }

    public Equipment getById(String id) {
        var equipments = gatherEquipment();

        return equipments.stream()
                .filter(equipment -> equipment.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private List<Equipment> gatherEquipment() {
        var potions = db.potionDao().getAll();
        var armors = db.armorDao().getAll();
        var weapons = db.weaponDao().getAll();

        List<Equipment> equipments = new ArrayList<>();
        equipments.addAll(potions);
        equipments.addAll(armors);
        equipments.addAll(weapons);

        return equipments;
    }
}
