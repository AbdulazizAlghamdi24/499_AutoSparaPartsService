package com.example.sparepart2.Order_Status;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sparepart2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrderDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

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