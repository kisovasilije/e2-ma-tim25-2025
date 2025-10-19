package com.example.rpg.database.daos;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rpg.model.Equipment;

import java.util.List;

@Dao
public interface EquipmentDao {

    @Insert
    long insertEquipment(Equipment equipment);

    @Update
    void updateEquipment(Equipment equipment);

    @Query("SELECT * FROM equipment WHERE id = :equipmentId LIMIT 1")
    Equipment getEquipmentById(long equipmentId);

    @Query("SELECT * FROM equipment")
    List<Equipment> getAllEquipment();

    @Query("SELECT * FROM equipment WHERE playerOwnerId = :playerId")
    List<Equipment> getEquipmentForPlayer(long playerId);
}
