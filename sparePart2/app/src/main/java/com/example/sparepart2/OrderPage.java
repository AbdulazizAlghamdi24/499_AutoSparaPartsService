package com.example.sparepart2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;


import com.example.sparepart2.Data.CarSpareParts;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrderPage extends AppCompatActivity {

    private EditText carMakeEditText;
    private EditText carModelEditText;
    private EditText carYearEditText;
    private EditText carPartsEditText;
    private Button createOrderButton;

    private BottomNavigationView bottom_nav;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);


        bottom_nav = findViewById(R.id.bottom_nav);

        bottom_nav.setSelectedItemId(R.id.home);

        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(),Settings.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.home:
                        return true;

                }
                return false;
            }
        });
        // Initialize views
        carMakeEditText = findViewById(R.id.CarMake);
        carModelEditText = findViewById(R.id.CarModel);
        carYearEditText = findViewById(R.id.CarYear);
        carPartsEditText = findViewById(R.id.carPartsEditText);
        createOrderButton = findViewById(R.id.createOrderButton);

        // Set up the spinner with car parts data


        // Handle "Create Order" button click
        createOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String carMake = carMakeEditText.getText().toString();
                String carModel = carModelEditText.getText().toString();
                String carYear = carYearEditText.getText().toString();
                String carParts = carPartsEditText.getText().toString();

                if (carMake.isEmpty()) {
                    carMakeEditText.setError("Car Make is required");
                    carMakeEditText.requestFocus();
                    return;
                }

                if (carModel.isEmpty()) {
                    carModelEditText.setError("Car Model is required");
                    carModelEditText.requestFocus();
                    return;
                }

                if (carParts.isEmpty()) {
                    carPartsEditText.setError("Car Parts is required");
                    carPartsEditText.requestFocus();
                    return;
                }
                // Validate car year format and range
                if (TextUtils.isEmpty(carYear)) {
                    carYearEditText.setError("Car Year is required");
                    carYearEditText.requestFocus();
                    return;
                }

                try {
                    int year = Integer.parseInt(carYear);
                    if (year < 1900 || year > 2023) {
                        carYearEditText.setError("Invalid car year. Please enter a year between 1900 and 2023.");
                        carYearEditText.requestFocus();
                        return;
                    }


                    // Create an instance of OrderTask and execute it
                    OrderTask orderTask = new OrderTask();
                    orderTask.execute(carMake, carModel, (carYear), carParts);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    carYearEditText.setError("Invalid car year format. Please enter a valid year.");
                    carYearEditText.requestFocus();
                }
            }
        });
    }

    private class OrderTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String carMake = params[0];
            String carModel = params[1];
            String carYear = params[2];
            String carParts = params[3];


            try {
                // Set up the connection to the PHP file
                URL url = new URL("http://192.168.0.248/499_spareparts/order.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON data to send to the PHP file
                JSONObject requestData = new JSONObject();
                requestData.put("car_make", carMake);
                requestData.put("car_model", carModel);
                requestData.put("car_year", carYear);
                requestData.put("part", carParts);


                // Write the JSON data to the request body
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestData.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                // Read the response from the PHP file
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Return the response as a string
                return response.toString();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                // Trim the response string to remove leading/trailing whitespace
                response = response.trim();

                // Check if the response starts with unwanted characters
                while (response.charAt(0) != '{' && response.length() > 1) {
                    // Remove the first character from the response string
                    response = response.substring(1);
                }

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String message = jsonResponse.getString("message");

                    // Display the response message to the user
                    Toast.makeText(OrderPage.this, message, Toast.LENGTH_SHORT).show();

                    // Check if the order submission was successful
                    if (message.equals("Order submitted successfully.")) {
                        // Retrieve the order details from the JSON response
                        String carMake = carMakeEditText.getText().toString();
                        String carModel = carModelEditText.getText().toString();
                        String carYear = carYearEditText.getText().toString();
                        String carParts = carPartsEditText.getText().toString();

                        // Start the ShowOrderPage activity and pass the order details
                        Intent intent = new Intent(OrderPage.this, ShowOrderPage.class);
                        intent.putExtra("carMake", carMake);
                        intent.putExtra("carModel", carModel);
                        intent.putExtra("carYear", carYear);
                        intent.putExtra("carParts", carParts);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    Toast.makeText(OrderPage.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle null response error
                Toast.makeText(OrderPage.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }
    }
}