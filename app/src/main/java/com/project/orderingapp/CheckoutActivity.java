package com.project.orderingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.project.orderingapp.Models.ApiResponseObject;
import com.project.orderingapp.SharedPreferences.User;
import com.project.orderingapp.helpers.Util;

import java.util.HashMap;

public class CheckoutActivity extends AppCompatActivity {

    EditText txtDeliveryAddress, txtMobileNumber;
    Button btnProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = findViewById(R.id.toolbar);

        txtDeliveryAddress = findViewById(R.id.txtDeliveryAddress);
        txtMobileNumber = findViewById(R.id.txtMobileNumber);
        btnProceed = findViewById(R.id.btnProceed);

        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtDeliveryAddress.getText().toString().equals("")) {

                    txtDeliveryAddress.setError("Field Required!");

                } else if (txtMobileNumber.getText().toString().equals("")) {
                    txtDeliveryAddress.setError("Field Required!");
                } else {

                    if (txtMobileNumber.getText().toString().length() == 13) {
                        if (!txtMobileNumber.getText().toString().substring(0, 3).equals("+63")) {
                            txtMobileNumber.setError("Invalid mobile number");
                        }

                        new SetCustomerInfo(txtDeliveryAddress.getText().toString(), txtMobileNumber.getText().toString()).execute();

                    } else {
                        txtMobileNumber.setError("Invalid mobile number length");
                    }
                }

            }
        });

        txtMobileNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (txtMobileNumber.getText().toString().equals("")) {
                    txtMobileNumber.setText("+63");
                }
            }
        });

        txtMobileNumber.setText("+63");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    class SetCustomerInfo extends AsyncTask<Void, Void, Void> {

        String deliveryAddress, mobileNumber;

        Boolean asyncSuccess = false;

        ProgressDialog progressDialog;

        public SetCustomerInfo(String deliveryAddress, String mobileNumber) {
            this.deliveryAddress = deliveryAddress;
            this.mobileNumber = mobileNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CheckoutActivity.this, CheckoutActivity.this.getApplicationContext().getResources().getString(R.string.app_name), "Loading...", true);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Util util = new Util();

            try {

                HashMap<String, Object> jsonContent = new HashMap<>();
                jsonContent.put("userId", User.getUserId(CheckoutActivity.this));
                jsonContent.put("deliveryAddress", deliveryAddress);
                jsonContent.put("mobileNumber", mobileNumber);

                HashMap<String, Object> jsonBody = new HashMap<>();

                jsonBody.put("key", "Veronica");
                jsonBody.put("content", jsonContent);

                String jsonObject = new Gson().toJson(jsonBody);

                String jsonResponse = util.callApi(CheckoutActivity.this.getString(R.string.apiUrl) + "setCustomerInfo", jsonObject, 1);

                ApiResponseObject apiResponse = new Gson().fromJson(jsonResponse, ApiResponseObject.class);

                if (apiResponse.code == 200) {
                    asyncSuccess = true;
                }

            } catch (Exception e) {

                Log.d("setCustomerInfo", e.toString());

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            if (asyncSuccess) {
                Intent intent = new Intent(CheckoutActivity.this, HomeActivity.class);

                startActivity(intent);
            }

        }
    }

}
