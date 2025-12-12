package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rpg.model.statistics.DailyStatistics;

import java.util.List;

@Dao
public interface DailyStatisticsDao {
    @Query("select * from daily_statistics where userId = :userId and day = :day limit 1")
    DailyStatistics getForDay(long userId, String day);

    @Insert
    long insert(DailyStatistics stat);

    @Update
    int update(DailyStatistics stat);

    @Query("select * from daily_statistics where userId = :userId")
    List<DailyStatistics> getAllByUserId(long userId);
}
