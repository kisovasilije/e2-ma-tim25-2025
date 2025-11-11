
package com.example.rpg.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(
        tableName = "tasks",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "category_id",
                onDelete = ForeignKey.SET_NULL
        ),
        indices = {@Index("category_id")}
)
@TypeConverters(Task.DateConverter.class)
public class Task {
    public static String ACTIVE = "active";

    public static String DONE = "done";

    public static String CANCELED = "canceled";

    public static String PAUSED = "paused";

    public static String UNFINISHED = "unfinished";


    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String description;

    @ColumnInfo(name = "category_id")
    public Long categoryId;

    public long userId;

    @ColumnInfo(name = "stageId", index = true)
    public Long stageId;

    @ColumnInfo(name = "is_repeating")
    public boolean isRepeating;

    @ColumnInfo(name = "repeat_interval")
    public int repeatInterval;

    @ColumnInfo(name = "repeat_unit")
    public String repeatUnit;

    @ColumnInfo(name = "repeat_start")
    public Date repeatStart;

    @ColumnInfo(name = "repeat_end")
    public Date repeatEnd;

    @ColumnInfo(name = "difficulty_xp")
    public int difficultyXP;

    @ColumnInfo(name = "importance_xp")
    public int importanceXP;

    @ColumnInfo(name = "total_xp")
    public int totalXP;

    @ColumnInfo(name = "execution_time")
    public Date executionTime;

    @ColumnInfo(name = "status")
    public String status; // "active", "done", "canceled", "paused", "unfinished"

    public Task(String name, String description, Long categoryId, long userId, Long stageId,
                boolean isRepeating, int repeatInterval, String repeatUnit, Date repeatStart, Date repeatEnd,
                int difficultyXP, int importanceXP, Date executionTime) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.userId = userId;
        this.stageId = stageId;
        this.isRepeating = isRepeating;
        this.repeatInterval = repeatInterval;
        this.repeatUnit = repeatUnit;
        this.repeatStart = repeatStart;
        this.repeatEnd = repeatEnd;
        this.difficultyXP = difficultyXP;
        this.importanceXP = importanceXP;
        this.totalXP = difficultyXP + importanceXP;
        this.executionTime = executionTime;
        this.status = "active";
    }

    public static class DateConverter {
        @TypeConverter
        public static Long fromDate(Date date) {
            return date == null ? null : date.getTime();
        }

        @TypeConverter
        public static Date toDate(Long timestamp) {
            return timestamp == null ? null : new Date(timestamp);
        }
    }

    public int getTotalXpForLevel(int level) {
        int importanceXp = importanceXP;
        int difficultyXp = difficultyXP;

        for (int i = 1; i < level; i++) {
            importanceXp = (int) Math.ceil(importanceXp * 3.0 / 2.0);
            difficultyXp = (int) Math.ceil(difficultyXp * 3.0 / 2.0);
        }

        return importanceXp + difficultyXp;
    }
}
