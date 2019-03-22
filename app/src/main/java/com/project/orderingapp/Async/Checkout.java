package com.project.orderingapp.Async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.project.orderingapp.HomeActivity;
import com.project.orderingapp.Models.ApiResponseObject;
import com.project.orderingapp.Models.CartList;
import com.project.orderingapp.R;
import com.project.orderingapp.SharedPreferences.Cart;
import com.project.orderingapp.SharedPreferences.User;
import com.project.orderingapp.helpers.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Checkout extends AsyncTask<Void, Void, Void> {

    Context context;
    ProgressDialog progressDialog;
    int paymentMethodId;

    Boolean asyncSuccess = false;

    public Checkout(Context context, int paymentMethodId) {
        this.context = context;
        this.paymentMethodId = paymentMethodId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = ProgressDialog.show(context, context.getString(R.string.app_name), "Loading...");

    }

    @Override
    protected Void doInBackground(Void... voids) {

        Util util = new Util();

        try {

            CartList[] cartListObject = new CartList[]{};

            cartListObject = new Gson().fromJson(Cart.getCartJsonObject(), CartList[].class);

            HashMap<String, Object> orderInfo = new HashMap<>();
            orderInfo.put("userId", User.getUserId(context));
            orderInfo.put("paymentMethodId", paymentMethodId);

            List<CartItem> cartItemList = new ArrayList<>();

            for (CartList cartItems : cartListObject) {

                CartItem item = new CartItem();

                item.productId = cartItems.productId;
                item.quantity = cartItems.quantity;
                item.unitPrice = cartItems.unitPrice;

                cartItemList.add(item);

            }

            HashMap<String, Object> content = new HashMap<>();
            content.put("orderInfo", orderInfo);
            content.put("orderItems", cartItemList);

            HashMap<String, Object> body = new HashMap<>();
            body.put("key", "Veronica");
            body.put("content", content);

            String jsonObject = new Gson().toJson(body);

            String jsonResponse = util.callApi(context.getString(R.string.apiUrl) + "checkout", jsonObject, 1);

            ApiResponseObject apiResponse = new Gson().fromJson(jsonResponse, ApiResponseObject.class);

            if (apiResponse.code == 200) {
                Cart.clearPreference();
                asyncSuccess = true;
            }


        } catch (Exception ex) {

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        progressDialog.dismiss();
        if (asyncSuccess) {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        }

    }

    class CartItem {
        public int productId;
        public int quantity;
        public Double unitPrice;
    }
}
