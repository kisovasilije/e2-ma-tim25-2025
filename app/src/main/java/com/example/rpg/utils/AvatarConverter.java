package com.example.rpg.utils;

import androidx.room.TypeConverter;

import com.example.rpg.model.Avatar;

public class AvatarConverter {
    @TypeConverter
    public static String fromAvatar(Avatar a) {
        return a == null ? null : a.name();
    }

    @TypeConverter
    public static Avatar toAvatar(String s) {
        return s == null ? null : Avatar.valueOf(s);
    }
}
