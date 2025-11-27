package com.example.rpg.database.daos;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rpg.model.Boss;

import java.util.List;

@Dao
public interface BossDao {

    @Insert
    long insertBoss(Boss boss);

    @Update
    void updateBoss(Boss boss);

    @Query("SELECT * FROM bosses WHERE id = :bossId LIMIT 1")
    Boss getBossById(long bossId);

    @Query("SELECT * FROM bosses WHERE level = :playerLevel LIMIT 1")
    Boss getCurrentBoss(long playerLevel);

    @Query("SELECT * FROM bosses ORDER BY id ASC")
    List<Boss> getAllBosses();

    @Query("SELECT rewardCoins FROM bosses where level = :level LIMIT 1")
    Integer getPreviousLevelRewardCoins(int level);
}