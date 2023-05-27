package com.example.sparepart2.Order_Status;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.sparepart2.R;

public class OrderDetails_user extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_user);

        TextView orderIdTextView = findViewById(R.id.orderIdTextView);
        TextView carTypeTextView = findViewById(R.id.carTypeTextView);
        TextView sparePartTextView = findViewById(R.id.sparePartTextView);
        TextView priceRangeTextView = findViewById(R.id.priceRangeTextView);
        TextView orderTimeTextView = findViewById(R.id.orderTimeTextView);
        TextView orderStatusTextView = findViewById(R.id.orderStatusTextView);


        Intent intent = getIntent();

        String orderId = intent.getStringExtra("orderId");
        String carType = intent.getStringExtra("carType");
        String sparePart = intent.getStringExtra("sparePart");
        String priceRange = intent.getStringExtra("priceRange");
        String orderTime = intent.getStringExtra("orderTime");
        String orderStatus = intent.getStringExtra("orderStatus");


        orderIdTextView.setText("Order ID: " + orderId);
        carTypeTextView.setText("Car Type: " + carType);
        sparePartTextView.setText("Spare Part: " + sparePart);
        priceRangeTextView.setText("Price Range: " + priceRange);
        orderTimeTextView.setText("Order Time: " + orderTime);
        orderStatusTextView.setText("Order Status: " + orderStatus);

    }
}