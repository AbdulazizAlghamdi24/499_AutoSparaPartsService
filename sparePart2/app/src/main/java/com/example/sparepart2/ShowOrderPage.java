package com.example.sparepart2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShowOrderPage extends AppCompatActivity {

    private TextView orderDetailsTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order_page);

        orderDetailsTextView = findViewById(R.id.orderDetailsTextView);

        // Retrieve the order details from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String carMake = intent.getStringExtra("carMake");
            String carModel = intent.getStringExtra("carModel");
            String carYear = intent.getStringExtra("carYear");
            String carParts = intent.getStringExtra("carParts");

            // Format the order details as desired
            String orderDetails = "Order Details:\n" +
                    "Car Make: " + carMake + "\n" +
                    "Car Model: " + carModel + "\n" +
                    "Car Year: " + carYear + "\n" +
                    "Car Parts: " + carParts + "\n";

            // Display the order details in the TextView
            orderDetailsTextView.setText(orderDetails);
        }
    }
}

