package com.example.sparepart2.Order_Status;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sparepart2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class OrderDetails_user extends AppCompatActivity {

    private ImageView Cancellbtn , Acceptbtn;
    private String orderId;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
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

        Cancellbtn = findViewById(R.id.CancellButton);
        Acceptbtn = findViewById(R.id.CheckButton);

        Intent intent = getIntent();

        orderId = intent.getStringExtra("orderId");
        String carType = intent.getStringExtra("carType");
        String sparePart = intent.getStringExtra("sparePart");
        String priceRange = intent.getStringExtra("priceRange");
        String orderTime = intent.getStringExtra("orderTime");
        String orderStatus = intent.getStringExtra("orderStatus");

        carTypeTextView.setText("Car Type: " + carType);
        sparePartTextView.setText("Spare Part: " + sparePart);
        priceRangeTextView.setText("Price Range: " + priceRange);
        orderTimeTextView.setText("Order Time: " + orderTime);
        orderStatusTextView.setText("Order Status: " + orderStatus);

        Cancellbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CancelOrderTask().execute();
            }
        });
    }

    private class CancelOrderTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.0.248/499_spareparts/cancel_order.php");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("order_id", orderId);
                jsonObject.put("new_status", "passive"); // added this line

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
                outputStreamWriter.write(jsonObject.toString());
                outputStreamWriter.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    // Trim the response string to remove leading/trailing whitespace
                    response = response.trim();

                    // Check if the response starts with unwanted characters
                    while (!response.startsWith("{") && response.length() > 1) {
                        // Remove the first character from the response string
                        response = response.substring(1);
                    }

                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.has("message")) {
                        Toast.makeText(OrderDetails_user.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (jsonResponse.has("error")) {
                        Toast.makeText(OrderDetails_user.this, jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    Toast.makeText(OrderDetails_user.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle null response error
                Toast.makeText(OrderDetails_user.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }
    }
}