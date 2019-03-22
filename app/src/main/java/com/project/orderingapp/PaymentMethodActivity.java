package com.project.orderingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.project.orderingapp.Async.Checkout;
import com.project.orderingapp.Models.PaymentMethod;
import com.project.orderingapp.SharedPreferences.PaymentMethods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaymentMethodActivity extends AppCompatActivity {

    GridView gvPaymentMethod;

    PaymentMethod[] paymentMethodObject = new PaymentMethod[]{};

    List<PaymentMethod> paymentMethodList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        Toolbar toolbar = findViewById(R.id.toolbar);
        gvPaymentMethod = findViewById(R.id.gvPaymentMethod);

        paymentMethodObject = new Gson().fromJson(PaymentMethods.getPaymentMethodsJosnObject(PaymentMethodActivity.this), PaymentMethod[].class);

        paymentMethodList = Arrays.asList(paymentMethodObject);

        setSupportActionBar(toolbar);

        gvPaymentMethod.setAdapter(new ImageAdapter(PaymentMethodActivity.this));

        gvPaymentMethod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                new Checkout(PaymentMethodActivity.this, paymentMethodList.get(i).id).execute();

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    class ImageAdapter extends BaseAdapter {

        Context context;

        public ImageAdapter(Context context) {
            this.context = context;
        }


        @Override
        public int getCount() {
            return paymentMethodList.size();
        }

        @Override
        public Object getItem(int position) {
            return paymentMethodObject[position].name;
        }

        @Override
        public long getItemId(int itemId) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View gridView;

            if (view == null) {

                gridView = new View(context);

                gridView = layoutInflater.inflate(R.layout.paymentmethod_layout, null);

                TextView lblPaymentMethod = gridView.findViewById(R.id.lblPaymentMethod);

                ImageView imgPaymentMethod = gridView.findViewById(R.id.imgPaymentMethod);

                lblPaymentMethod.setText(paymentMethodList.get(position).name);

                Picasso.get().load(paymentMethodList.get(position).imgSrc).into(imgPaymentMethod);
            } else {
                gridView = view;
            }

            return gridView;
        }
    }
}
