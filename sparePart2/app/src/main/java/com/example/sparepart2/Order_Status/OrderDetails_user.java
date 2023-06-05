package com.example.sparepart2.Order_Status;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

    private ImageView Cancellbtn, Completetbtn, Onholdbtn , Activatebtn;
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
        Completetbtn = findViewById(R.id.CheckButton);
        Onholdbtn = findViewById(R.id.Onholdbtn);
        Activatebtn = findViewById(R.id.Activatebtn);

        Intent intent = getIntent();

        orderId = intent.getStringExtra("orderId");
        String carType = intent.getStringExtra("carType");
        String sparePart = intent.getStringExtra("sparePart");
        String priceRange = intent.getStringExtra("priceRange");
        String orderTime = intent.getStringExtra("orderTime");
        String orderStatus = intent.getStringExtra("orderStatus");

        orderIdTextView.setText(orderId);
        carTypeTextView.setText("Car Type: " + carType);
        sparePartTextView.setText("Spare Part: " + sparePart);
        priceRangeTextView.setText("Price Range: " + priceRange);
        orderTimeTextView.setText("Order Time: " + orderTime);
        orderStatusTextView.setText("Order Status: " + orderStatus);
        if (orderStatus.equalsIgnoreCase("canceled")) {
            orderStatusTextView.setTextColor(Color.RED);
        } else if (orderStatus.equalsIgnoreCase("closed")) {
            orderStatusTextView.setTextColor(Color.GREEN);
        } else {
            // Default color for other status
            orderStatusTextView.setTextColor(Color.BLACK);
        }

        Cancellbtn.setOnClickListener(v -> showConfirmationDialog("Cancel", "Are you sure you want to cancel this order?", "canceled"));

        Completetbtn.setOnClickListener(v -> showConfirmationDialog("Complete", "Are you sure you want to mark this order as complete?", "completed"));

        Onholdbtn.setOnClickListener(v -> showConfirmationDialog("On Hold", "Are you sure you want to put this order on hold?", "on hold"));

        Activatebtn.setOnClickListener(v -> showConfirmationDialog("On Hold", "Are you sure you want to active the order again?", "active"));


    }
    private void showConfirmationDialog(String action, String message, final String newStatus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(action);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new UpdateStatusTask().execute(newStatus);
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class UpdateStatusTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String newStatus = params[0];

            try {
                URL url = new URL("https://spare-parts-php.herokuapp.com/update_status.php");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; utf-8");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("order_id", orderId);
                jsonObject.put("new_status", newStatus);

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