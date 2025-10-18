package com.example.rpg.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.rpg.database.daos.UserDao;
import com.example.rpg.model.User;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public static final String databaseName = "rpg";

    public abstract UserDao userDao();

    public static AppDatabase get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(ctx, AppDatabase.class, databaseName).build();
                }
            }
        }

        return INSTANCE;
    }
}
