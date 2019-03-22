package com.project.orderingapp.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.project.orderingapp.HomeActivity;

public class User {

    public static String credentialsStore = "User";
    static Context applicationContext = HomeActivity.getContextOfApplication();

    public int userId;

    public static int getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("userId", 0);
    }

    public static void setPreference(User param, Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);

        SharedPreferences.Editor append = sharedPreferences.edit();

        append.putInt("userId", param.userId);

        append.commit();

    }
}
