package com.example.rpg.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(index = true)
    public String email;

    @ColumnInfo(index = true)
    public String username;

    public String password;

    public Avatar avatar;

    public User(String email, String username, String password, Avatar avatar) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.avatar = avatar;
    }
}
