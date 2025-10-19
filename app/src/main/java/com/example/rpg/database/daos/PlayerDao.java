package com.example.rpg.database.daos;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rpg.model.Player;

@Dao
public interface PlayerDao {

    @Insert
    long insertPlayer(Player player);

    @Update
    void updatePlayer(Player player);

    @Query("SELECT * FROM players WHERE id = :playerId LIMIT 1")
    Player getPlayerById(long playerId);

    @Query("SELECT * FROM players LIMIT 1")
    Player getCurrentPlayer(); // Assuming one active player
}

