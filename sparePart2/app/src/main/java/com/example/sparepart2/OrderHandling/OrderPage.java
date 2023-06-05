package com.example.sparepart2.OrderHandling;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import com.example.sparepart2.MainActivity;
import com.example.sparepart2.R;
import com.example.sparepart2.Registration.LoginPage;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;

public class OrderPage extends AppCompatActivity {

    private EditText car_manufacture, type, model, year, spare_parts, extra_details, price_range;
    private Button createOrderButton;
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);



        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    LoginPage.SHARED_PREFS,
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        car_manufacture = findViewById(R.id.car_manufacture);
        type = findViewById(R.id.type);
        model = findViewById(R.id.model);
        year = findViewById(R.id.year);
        spare_parts = findViewById(R.id.spare_part);
        extra_details = findViewById(R.id.extra_details);
        price_range = findViewById(R.id.price_range);
        createOrderButton = findViewById(R.id.createOrderButton);

        createOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = sharedPreferences.getString(LoginPage.USER_ID, "");

                String carManufacture = car_manufacture.getText().toString();
                String carType = type.getText().toString();
                String carModel = model.getText().toString();
                String carYear = year.getText().toString();
                String carParts = spare_parts.getText().toString();
                String extraDetails = extra_details.getText().toString();
                String priceRange = price_range.getText().toString();

                if (carManufacture.isEmpty()) {
                    car_manufacture.setError("Car Make is required");
                    car_manufacture.requestFocus();
                    return;
                }

                if (carModel.isEmpty()) {
                    model.setError("Car Model is required");
                    model.requestFocus();
                    return;
                }

                if (carParts.isEmpty()) {
                    spare_parts.setError("Car Parts is required");
                    spare_parts.requestFocus();
                    return;
                }

                // Validate car year format and range
                if (TextUtils.isEmpty(carYear)) {
                    year.setError("Car Year is required");
                    year.requestFocus();
                    return;
                }

                try {
                    int yearValue = Integer.parseInt(carYear);
                    if (yearValue < 1900 || yearValue > 2023) {
                        year.setError("Invalid car year. Please enter a year between 1900 and 2023.");
                        year.requestFocus();
                        return;
                    }

                    // Create an instance of OrderTask and execute it
                    OrderTask orderTask = new OrderTask();
                    orderTask.execute(userId, carManufacture, carType, carModel, carYear, carParts, extraDetails, priceRange);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    year.setError("Invalid car year format. Please enter a valid year.");
                    year.requestFocus();
                }
            }
        });
    }

    private class OrderTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String userId = params[0];
            String carManufacture = params[1];
            String carType = params[2];
            String carModel = params[3];
            String carYear = params[4];
            String carParts = params[5];
            String extraDetails = params[6];
            String priceRange = params[7];

            try {
                // Set up the connection to the PHP file
                URL url = new URL("https://spare-parts-php.herokuapp.com/create_order.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON data to send to the PHP file
                JSONObject requestData = new JSONObject();

                requestData.put("user_id", userId);
                requestData.put("car_manufacture", carManufacture);
                requestData.put("type", carType);
                requestData.put("model", carModel);
                requestData.put("year", carYear);
                requestData.put("spare_part", carParts);
                requestData.put("extra_details", extraDetails);
                requestData.put("price_range", priceRange);

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

                    if (message.equals("Order created successfully.")) {
                        showSuccessDialog(message);
                    } else {
                        Toast.makeText(OrderPage.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(OrderPage.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(OrderPage.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showSuccessDialog(String message) {
        new AlertDialog.Builder(OrderPage.this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(OrderPage.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
