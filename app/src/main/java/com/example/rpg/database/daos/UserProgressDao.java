package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.rpg.model.UserProgress;

@Dao
public interface UserProgressDao {
    @Insert
    long insert(UserProgress progress);
}
