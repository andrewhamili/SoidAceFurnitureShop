package com.project.orderingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.project.orderingapp.Async.Checkout;

public class PaymentMethodDetailsActivity extends AppCompatActivity {

    Button btnProceed;
    TextView lblPaymentMethodName;
    EditText txtPaymentMethodNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String paymentMethodName = getIntent().getStringExtra("paymentMethodName");
        final int paymentMethodId = getIntent().getIntExtra("paymentMethodId", 0);

        btnProceed = findViewById(R.id.btnProceed);
        lblPaymentMethodName = findViewById(R.id.lblPaymentMethodName);
        txtPaymentMethodNumber = findViewById(R.id.txtPaymentMethodNumber);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lblPaymentMethodName.setText(paymentMethodName);

                String paymentMethodNumber = txtPaymentMethodNumber.getText().toString();

                if (paymentMethodNumber.length() < 16) {
                    txtPaymentMethodNumber.setError("Insufficient Number");
                } else {

                    new Checkout(PaymentMethodDetailsActivity.this, paymentMethodId, paymentMethodNumber).execute();

                }

            }
        });

    }

}
