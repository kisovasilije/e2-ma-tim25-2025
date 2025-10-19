package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.rpg.model.Task;

import java.util.Date;
import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks")
    List<Task> getAll();

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    Task getById(long id);

    // Fetch tasks for XP quota checks
    @Query("SELECT * FROM tasks WHERE difficulty_xp = :difficulty AND importance_xp = :importance AND execution_time BETWEEN :start AND :end")
    List<Task> getTasksForQuota(int difficulty, int importance, Date start, Date end);

    // Fetch only active or future tasks (for list view)
    @Query("SELECT * FROM tasks WHERE status = 'active' OR (execution_time >= :today)")
    List<Task> getCurrentAndFutureTasks(Date today);

    @Query("SELECT * FROM tasks WHERE stageId = :stageId AND status = 'active'")
    List<Task> getActiveTasksForStage(long stageId);

    @Query("SELECT * FROM tasks WHERE playerOwnerId = :playerId AND stageId = :stageId")
    List<Task> getAllTasksForPlayerAndStage(long playerId, long stageId);

    // Fetch past tasks (for calendar)
    @Query("SELECT * FROM tasks WHERE execution_time < :today")
    List<Task> getPastTasks(Date today);

    // Fetch repeating tasks
    @Query("SELECT * FROM tasks WHERE is_repeating = 1 AND (status = 'active' OR execution_time >= :today)")
    List<Task> getRepeatingTasks(Date today);

    // Fetch one-time tasks
    @Query("SELECT * FROM tasks WHERE is_repeating = 0 AND (status = 'active' OR execution_time >= :today)")
    List<Task> getOneTimeTasks(Date today);

    // Update status by task id
    @Query("UPDATE tasks SET status = :newStatus WHERE id = :taskId")
    void updateStatus(long taskId, String newStatus);
}
