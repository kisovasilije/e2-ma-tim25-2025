package com.example.rpg.database.repository;

import com.example.rpg.database.AppDatabase;
import com.example.rpg.model.UserEquipment;

import java.util.List;

public class UserEquipmentRepository {
    private final AppDatabase db;

    private final EquipmentRepository equipmentRepository;

    public UserEquipmentRepository(AppDatabase db) {
        this.db = db;
        equipmentRepository = new EquipmentRepository(db);
    }

//    public List<UserEquipment> getActivateEquipment(long userId) {
//        var ues = db.userEquipmentDao().getByUserId(userId);
//
//        for (var ue : ues) {
//            ue.equipment = equipmentRepository.getById(ue.equipmentId);
//        }
//    }
}
