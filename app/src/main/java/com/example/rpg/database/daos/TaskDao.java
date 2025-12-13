package com.example.rpg.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rpg.model.Task;
import com.example.rpg.model.statistics.CategoryCount;

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

    @Query("SELECT * FROM tasks WHERE status != 'done' OR (execution_time >= :today)")
    List<Task> getCurrentAndFutureTasks(Date today);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND stageId = :stageId")
    List<Task> getAllTasksForPlayerAndStage(long userId, long stageId);

    @Query("select * from tasks where userId = :userId")
    List<Task> getAllByUserId(long userId);

    @Query("select c.name as categoryName, count(*) as counter" +
            " from tasks t inner join categories c on t.category_id = c.id where " +
            " t.userId = :userId and t.status = 'done' and t.category_id is not null" +
            " group by c.name")
    List<CategoryCount> getDoneTaskCountByCategory(long userId);

    @Query("select * from tasks where userId = :userId and status = 'done'")
    List<Task> getAllDoneByUserId(long userId);
}
