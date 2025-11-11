package com.example.rpg.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.rpg.database.daos.BossDao;
import com.example.rpg.database.daos.CategoryDao;
import com.example.rpg.database.daos.EquipmentDao;
import com.example.rpg.database.daos.TaskDao;
import com.example.rpg.database.daos.UserDao;
import com.example.rpg.database.daos.UserEquipmentDao;
import com.example.rpg.database.daos.UserProgressDao;
import com.example.rpg.model.Boss;
import com.example.rpg.model.Category;
import com.example.rpg.model.Equipment;
import com.example.rpg.model.Task;
import com.example.rpg.model.User;
import com.example.rpg.model.UserEquipment;
import com.example.rpg.model.UserProgress;

@Database(
        entities = {
                User.class,
                Task.class,
                UserProgress.class,
                Equipment.class,
                UserEquipment.class,
                Boss.class,
                Category.class
        },
        version = 8
)
@TypeConverters({Task.DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    private static final String databaseName = "rpg";

    public abstract UserDao userDao();

    public abstract TaskDao taskDao();

    public abstract UserProgressDao userProgressDao();

    public abstract EquipmentDao equipmentDao();

    public abstract UserEquipmentDao userEquipmentDao();

    public abstract CategoryDao categoryDao();

    public abstract BossDao bossDao();

    public static AppDatabase get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(ctx, AppDatabase.class, databaseName)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}
