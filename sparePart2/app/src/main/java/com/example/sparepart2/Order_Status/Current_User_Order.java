package com.example.sparepart2.Order_Status;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sparepart2.MainActivity;
import com.example.sparepart2.OrderHandling.Order;
import com.example.sparepart2.OrderHandling.OrderAdapter;
import com.example.sparepart2.R;
import com.example.sparepart2.Registration.LoginPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class Current_User_Order extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_order);

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the user ID from encrypted SharedPreferences
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    LoginPage.SHARED_PREFS,
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            userId = sharedPreferences.getString(LoginPage.USER_ID, "");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        // Fetch the current user orders
        FetchOrdersTask fetchOrdersTask = new FetchOrdersTask();
        fetchOrdersTask.execute();
    }

    private class FetchOrdersTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Create JSON object to send with POST request
                JSONObject requestData = new JSONObject();
                requestData.put("user_id", userId);

                // Create connection and send the POST request
                URL url = new URL("http://192.168.0.248/499_spareparts/user_orders.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Write JSON data to the connection output stream
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(requestData.toString());
                writer.flush();
                writer.close();

                // Read the response from the PHP file
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Trim the response string to remove leading/trailing whitespace
                String jsonResponse = response.toString().trim();

                // Check if the response starts with unwanted characters
                while (!jsonResponse.startsWith("{") && jsonResponse.length() > 1) {
                    // Remove the first character from the response string
                    jsonResponse = jsonResponse.substring(1);
                }

                // Return the trimmed and cleaned response as a string
                return jsonResponse;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String message = jsonResponse.getString("message");

                    Toast.makeText(Current_User_Order.this, message, Toast.LENGTH_SHORT).show();

                    if (message.equals("Orders retrieved successfully.")) {
                        JSONArray ordersArray = jsonResponse.getJSONArray("data");
                        List<Order> orders = new ArrayList<>();

                        for (int i = 0; i < ordersArray.length(); i++) {
                            JSONObject orderObject = ordersArray.getJSONObject(i);
                            String orderId = orderObject.getString("id");
                            String carType = orderObject.getString("type");
                            String sparePart = orderObject.getString("spare_part");
                            String priceRange = orderObject.getString("price_range");
                            String orderTime = orderObject.getString("created_at");
                            String orderStatus = orderObject.getString("status");

                            Order order = new Order("", carType, sparePart, priceRange, orderTime, orderStatus,"");
                            orders.add(order);
                        }

                        // Set up the RecyclerView adapter
                        orderAdapter = new OrderAdapter(orders);
                        orderAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Order order) {
                                // Handle item click event
                                Intent intent = new Intent(Current_User_Order.this, OrderDetails_user.class);
                                intent.putExtra("orderId", order.getOrderId());
                                intent.putExtra("carType", order.getCarType());
                                intent.putExtra("sparePart", order.getSparePart());
                                intent.putExtra("priceRange", order.getPriceRange());
                                intent.putExtra("orderTime", order.getOrderTime());
                                intent.putExtra("orderStatus", order.getOrderStatus());
                                intent.putExtra("phone_number", order.getUserPhoneNumber());
                                startActivity(intent);
                            }
                        });
                        orderRecyclerView.setAdapter(orderAdapter);
                    } else {
                        // No orders found for the user
                        Toast.makeText(Current_User_Order.this, "No orders found for the user.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Current_User_Order.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Current_User_Order.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

