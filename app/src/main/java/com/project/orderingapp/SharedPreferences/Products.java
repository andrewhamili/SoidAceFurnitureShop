package com.project.orderingapp.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.project.orderingapp.HomeActivity;

public class Products {

    public static String credentialsStore = "Products";
    static Context applicationContext = HomeActivity.getContextOfApplication();

    public String productsJsonObject;

    public static String getProductsJsonObject(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);
        return sharedPreferences.getString("productsJsonObject", "[]");
    }

    public static void setPreference(Context context, Products param) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);

        SharedPreferences.Editor append = sharedPreferences.edit();

        append.putString("productsJsonObject", param.productsJsonObject);

        append.commit();

    }

}
