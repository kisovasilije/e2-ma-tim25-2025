package com.example.rpg.prefs;

import android.content.Context;

import androidx.annotation.Nullable;

public final class AuthPrefs {
    private static final String pref = "auth";

    private static final String usernameKey = "username";

    private AuthPrefs() {}

    public static void setUser(Context ctx, String username) {
        ctx.getSharedPreferences(pref, Context.MODE_PRIVATE)
                .edit()
                .putString(usernameKey, username)
                .apply();
    }

    public static void clear(Context ctx) {
        ctx.getSharedPreferences(pref, Context.MODE_PRIVATE)
                .edit()
                .remove(usernameKey)
                .apply();
    }

    @Nullable
    public static String isAuthenticated(Context ctx) {
        return ctx.getSharedPreferences(pref, Context.MODE_PRIVATE)
                .getString(usernameKey, null);
    }
}
