package com.project.orderingapp.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.project.orderingapp.HomeActivity;

public class Cart {

    public static String credentialsStore = "Products";
    static Context applicationContext = HomeActivity.getContextOfApplication();

    public String cartJsonObject;

    public static String getCartJsonObject() {
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);
        return sharedPreferences.getString("cartJsonObject", "[]");
    }

    public static void setPreference(Cart param) {

        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);

        SharedPreferences.Editor append = sharedPreferences.edit();

        append.putString("cartJsonObject", param.cartJsonObject);

        append.commit();

    }

    public static void clearPreference() {

        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);

        SharedPreferences.Editor append = sharedPreferences.edit();

        append.clear().apply();

    }
}
