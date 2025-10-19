package com.example.rpg.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.rpg.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories")
    List<Category> getAll();

    @Query("SELECT * FROM categories WHERE color = :color LIMIT 1")
    Category getByColor(int color);

    @Query("SELECT COUNT(*) FROM categories WHERE color = :color")
    int countByColor(int color);
}
