package com.project.orderingapp.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.project.orderingapp.HomeActivity;

public class PaymentMethods {

    public static String credentialsStore = "User";
    static Context applicationContext = HomeActivity.getContextOfApplication();

    public String paymentMethodsJsonObject;

    public static String getPaymentMethodsJosnObject(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);
        return sharedPreferences.getString("paymentMethodsJsonObject", "[]");
    }

    public static void setPreference(PaymentMethods param, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);
        SharedPreferences.Editor append = sharedPreferences.edit();

        append.putString("paymentMethodsJsonObject", param.paymentMethodsJsonObject);

        append.commit();
    }
}
