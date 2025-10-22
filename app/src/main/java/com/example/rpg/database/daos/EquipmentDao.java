package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.rpg.model.Equipment;

import java.util.List;

@Dao
public interface EquipmentDao {
    @Query("select * from equipments")
    List<Equipment> getAll();
}
