package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rpg.model.UserProgress;

@Dao
public interface UserProgressDao {
    @Insert
    long insert(UserProgress progress);

    @Query("select * from user_progresses where id = :id")
    UserProgress getById(long id);

    @Update
    int update(UserProgress progress);
}
