package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.rpg.model.equipment.Weapon;

import java.util.List;

@Dao
public interface WeaponDao {
    @Query("select * from weapons")
    List<Weapon> getAll();
}
