package com.example.sparepart2.Order_Status;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


import com.example.sparepart2.R;

public class OrderDetails extends AppCompatActivity {

    private ImageButton chatButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        chatButton = findViewById(R.id.chatButton);





        TextView orderIdTextView = findViewById(R.id.orderIdTextView);
        TextView carTypeTextView = findViewById(R.id.carTypeTextView);
        TextView sparePartTextView = findViewById(R.id.sparePartTextView);
        TextView priceRangeTextView = findViewById(R.id.priceRangeTextView);
        TextView orderTimeTextView = findViewById(R.id.orderTimeTextView);
        TextView orderStatusTextView = findViewById(R.id.orderStatusTextView);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView phoneNumberTextView = findViewById(R.id.PhoneNumber);

        Intent intent = getIntent();

        String orderId = intent.getStringExtra("orderId");
        String carType = intent.getStringExtra("carType");
        String sparePart = intent.getStringExtra("sparePart");
        String priceRange = intent.getStringExtra("priceRange");
        String orderTime = intent.getStringExtra("orderTime");
        String orderStatus = intent.getStringExtra("orderStatus");
        String PhoneNumber = intent.getStringExtra("phone_number");

        orderIdTextView.setText("Order ID: " + orderId);
        carTypeTextView.setText("Car Type: " + carType);
        sparePartTextView.setText("Spare Part: " + sparePart);
        priceRangeTextView.setText("Price Range: " + priceRange);
        orderTimeTextView.setText("Order Time: " + orderTime);
        orderStatusTextView.setText("Order Status: " + orderStatus);
        phoneNumberTextView.setText("Phone Number " + PhoneNumber);
    }
}