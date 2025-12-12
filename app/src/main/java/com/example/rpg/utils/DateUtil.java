package com.example.rpg.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static final SimpleDateFormat DAY_FMT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private DateUtil() {}

    public static String toDayKey(Date date) {
        return DAY_FMT.format(date);
    }
}
