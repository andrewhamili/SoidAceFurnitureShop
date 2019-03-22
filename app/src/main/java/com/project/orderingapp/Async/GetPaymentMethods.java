package com.project.orderingapp.Async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.project.orderingapp.Models.ApiResponseArray;
import com.project.orderingapp.R;
import com.project.orderingapp.SharedPreferences.PaymentMethods;
import com.project.orderingapp.helpers.Util;

public class GetPaymentMethods extends AsyncTask<Void, Void, Void> {

    ProgressDialog progressDialog;

    Context context;

    Boolean asyncSuccess = false;

    public GetPaymentMethods(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //progressDialog = ProgressDialog.show(PaymentMethodActivity.this, PaymentMethodActivity.this.getResources().getString(R.string.app_name), "Loading...", true);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {


            Util util = new Util();

            String jsonResponse = util.callApi(context.getString(R.string.apiUrl) + "getPaymentMethods", null, 0);

            ApiResponseArray apiResponse = new Gson().fromJson(jsonResponse, ApiResponseArray.class);

            if (apiResponse.code == 200) {

                String paymentMethodsListArray = apiResponse.content.toString();

                PaymentMethods paymentMethods = new PaymentMethods();

                paymentMethods.paymentMethodsJsonObject = paymentMethodsListArray;

                PaymentMethods.setPreference(paymentMethods, context);

                asyncSuccess = true;

            }

        } catch (Exception ex) {
            asyncSuccess = false;
        }

        return null;
    }
}