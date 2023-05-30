package com.example.sparepart2.Order_Status;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.sparepart2.R;

public class OrderDetails extends AppCompatActivity {

    private ImageButton chatButton;
    private ImageButton callButton;
    private static final int REQUEST_PHONE_CALL = 1;
    private String PhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        chatButton = findViewById(R.id.chatButton);
        callButton = findViewById(R.id.callButton);

        TextView orderIdTextView = findViewById(R.id.orderIdTextView);
        TextView carTypeTextView = findViewById(R.id.carTypeTextView);
        TextView sparePartTextView = findViewById(R.id.sparePartTextView);
        TextView priceRangeTextView = findViewById(R.id.priceRangeTextView);
        TextView orderTimeTextView = findViewById(R.id.orderTimeTextView);
        TextView orderStatusTextView = findViewById(R.id.orderStatusTextView);
        TextView phoneNumberTextView = findViewById(R.id.PhoneNumber);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView extra_detailsTextView = findViewById(R.id.extra_detailsTextView);

        Intent intent = getIntent();

        String orderId = intent.getStringExtra("orderId");
        String carType = intent.getStringExtra("carType");
        String sparePart = intent.getStringExtra("sparePart");
        String ExtraDetails = intent.getStringExtra("ExtraDetails");
        String priceRange = intent.getStringExtra("priceRange");
        String orderTime = intent.getStringExtra("orderTime");
        String orderStatus = intent.getStringExtra("orderStatus");
        PhoneNumber = intent.getStringExtra("phone_number");

        orderIdTextView.setText("Order ID: " + orderId);
        carTypeTextView.setText("Car Type: " + carType);
        sparePartTextView.setText("Spare Part: " + sparePart);
        extra_detailsTextView.setText("Extra Details:  "+ExtraDetails);
        priceRangeTextView.setText("Price Range: " + priceRange);
        orderTimeTextView.setText("Order Time: " + orderTime);
        orderStatusTextView.setText("Order Status: " + orderStatus);
        phoneNumberTextView.setText("Phone Number " + PhoneNumber);

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://wa.me/" +"966"+ PhoneNumber; // PhoneNumber should be in international format without + or 00
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
    }

    private void makePhoneCall() {
        if (PhoneNumber.length() > 0) {
            if (ContextCompat.checkSelfPermission(OrderDetails.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(OrderDetails.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            } else {
                String dial = "tel:" + PhoneNumber;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(OrderDetails.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}