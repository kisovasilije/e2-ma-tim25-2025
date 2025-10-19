package com.example.rpg.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.rpg.database.daos.TaskDao;
import com.example.rpg.database.daos.UserDao;
import com.example.rpg.model.Task;
import com.example.rpg.model.User;

@Database(entities = {User.class, Task.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    private static final String databaseName = "rpg";

    public abstract UserDao userDao();
    public abstract TaskDao taskDao();

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
