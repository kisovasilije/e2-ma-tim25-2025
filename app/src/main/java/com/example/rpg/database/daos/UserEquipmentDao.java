package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rpg.model.UserEquipment;

import java.util.List;

@Dao
public interface UserEquipmentDao {
    @Insert
    long insert(UserEquipment equipment);

    @Query("select * from user_equipments where userId = :userId")
    List<UserEquipment> getByUserId(long userId);

    @Update
    int update(UserEquipment equipment);

    @Query("update user_equipments set isActivated=0 where userId = :userId")
    void deactivateByUserId(long userId);
}
