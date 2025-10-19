package com.example.rpg.database.daos;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rpg.model.User;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Nullable
    @Query("SELECT * FROM users WHERE username = :username")
    User getByUsername(String username);

    @Update
    int update(User user);
}
