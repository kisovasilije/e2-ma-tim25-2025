package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.rpg.model.equipment.Potion;

import java.util.List;

@Dao
public interface PotionDao {
    @Query("select * from potions")
    List<Potion> getAll();
}
