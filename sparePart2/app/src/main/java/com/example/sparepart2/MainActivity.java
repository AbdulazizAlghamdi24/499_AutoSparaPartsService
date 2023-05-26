package com.example.sparepart2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sparepart2.OrderHandling.Ongoing_Orders;
import com.example.sparepart2.OrderHandling.OrderPage;
import com.example.sparepart2.bottomNav.BottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ImageButton CreateOrderbtn;
    private ImageButton ShowOrderbtn;
    private Button Api_recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigationHelper.setupBottomNavigation(bottomNavigationView, this);

        CreateOrderbtn = findViewById(R.id.create_order_btn);
        ShowOrderbtn = findViewById(R.id.show_order_btn);
        Api_recognizer = findViewById(R.id.Api_recognizer);

        CreateOrderbtn.setOnClickListener(this);
        ShowOrderbtn.setOnClickListener(this);
        Api_recognizer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_order_btn:
                startActivity(new Intent(MainActivity.this, OrderPage.class));
                break;
            case R.id.show_order_btn:
                startActivity(new Intent(MainActivity.this, Ongoing_Orders.class));
                break;
            case R.id.Api_recognizer:
                recognizeVehicleParts();
                break;
        }
    }

    private void recognizeVehicleParts() {
        String imageUrl = "https://s1.cdn.autoevolution.com/images/news/history-of-automotive-headlamps-from-acetylene-to-leds-4485_1.jpg";

        VehiclePartsRecognitionTask recognitionTask = new VehiclePartsRecognitionTask(new VehiclePartsRecognitionTask.VehiclePartsRecognitionListener() {
            @Override
            public void onRecognitionSuccess(String response) {
                // Handle the successful recognition response
                Log.d(TAG, "API response: " + response);
            }

            @Override
            public void onRecognitionFailure() {
                // Handle the recognition failure
                Log.e(TAG, "API request failed.");
            }
        });

        // Execute the recognition task by passing the image URL
        recognitionTask.execute(imageUrl);
    }
}





