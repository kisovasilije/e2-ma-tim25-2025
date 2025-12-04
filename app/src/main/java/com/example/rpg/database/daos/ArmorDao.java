package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.rpg.model.equipment.Armor;

import java.util.List;

@Dao
public interface ArmorDao {
    @Query("select * from armors")
    List<Armor> getAll();
}
