package com.example.rpg.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.rpg.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("select * from tasks where userId = :userId")
    List<Task> getByUserId(long userId);

    @Query("update tasks set isPassed = 1 where id = :id")
    int passTask(long id);
}
