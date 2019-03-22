package com.project.orderingapp.Async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.project.orderingapp.CheckoutActivity;
import com.project.orderingapp.Models.ApiResponseObject;
import com.project.orderingapp.Models.UserInfo;
import com.project.orderingapp.R;
import com.project.orderingapp.SharedPreferences.User;
import com.project.orderingapp.helpers.Util;

import java.util.HashMap;

public class UserVerification extends AsyncTask<Void, Void, Void> {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Boolean infoIsSet = false;
    Context context;

    Boolean asyncSuccess = false;

    ProgressDialog progressDialog;

    public UserVerification(Context context, FirebaseUser firebaseUser, FirebaseAuth firebaseAuth) {
        this.context = context;
        this.firebaseUser = firebaseUser;
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.app_name), "Loading...", true);
    }

    @Override
    protected Void doInBackground(Void... voids) {


        try {

            Util util = new Util();

            HashMap<String, Object> jsonContent = new HashMap<>();

            jsonContent.put("idTokenString", this.firebaseUser.getIdToken(false).getResult().getToken());

            HashMap<String, Object> jsonBody = new HashMap<>();

            jsonBody.put("key", "Veronica");
            jsonBody.put("content", jsonContent);

            String jsonObject = new Gson().toJson(jsonBody);

            String jsonResponse = util.callApi(context.getString(R.string.apiUrl) + "userVerification", jsonObject, 1);

            Log.d("ASYNC", "UserVerification Response = " + jsonResponse);

            ApiResponseObject apiResponse = new Gson().fromJson(jsonResponse, ApiResponseObject.class);

            if (apiResponse.code == 200) {

                UserInfo userInfo = new Gson().fromJson(apiResponse.content, UserInfo.class);

                User user = new User();

                user.userId = userInfo.userId;

                User.setPreference(user, context);

                if (userInfo.infoIsSet) {
                    infoIsSet = true;
                }

                asyncSuccess = true;

            } else {
                asyncSuccess = false;
                throw new Exception("API Response is 201");
            }

        } catch (Exception e) {

            AuthUI.getInstance()
                    .signOut(context)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        progressDialog.dismiss();

        if (asyncSuccess) {
            if (!infoIsSet) {
                Intent intent = new Intent(context, CheckoutActivity.class);
                context.startActivity(intent);
            }
        } else {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }


    }
}