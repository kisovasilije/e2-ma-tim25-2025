package com.example.rpg.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.rpg.database.daos.CategoryDao;
import com.example.rpg.database.daos.TaskDao;
import com.example.rpg.database.daos.UserDao;
import com.example.rpg.database.daos.PlayerDao;
import com.example.rpg.database.daos.BossDao;
import com.example.rpg.database.daos.EquipmentDao;
import com.example.rpg.model.User;
import com.example.rpg.model.Task;
import com.example.rpg.model.Category;
import com.example.rpg.model.Player;
import com.example.rpg.model.Boss;
import com.example.rpg.model.Equipment;

@Database(
        entities = {User.class, Category.class, Task.class, Player.class, Boss.class, Equipment.class},
        version = 3,
        exportSchema = true
)
@TypeConverters({Task.DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public static final String DATABASE_NAME = "rpg";

    public abstract UserDao userDao();
    public abstract TaskDao taskDao();
    public abstract CategoryDao categoryDao();
    public abstract PlayerDao playerDao();
    public abstract BossDao bossDao();
    public abstract EquipmentDao equipmentDao();

    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration() // wipes old DB if schema changed
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
