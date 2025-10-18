package com.example.rpg.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.rpg.database.daos.UserDao;
import com.example.rpg.model.User;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String databaseName = "rpg";

    public abstract UserDao userDao();
}
