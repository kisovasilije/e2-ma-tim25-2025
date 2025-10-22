package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.rpg.model.UserEquipment;

@Dao
public interface UserEquipmentDao {
    @Insert
    long insert(UserEquipment equipment);
}
