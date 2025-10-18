package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.rpg.model.User;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);
}
