package com.example.sparepart2.OrderHandling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sparepart2.R;
import com.example.sparepart2.bottomNav.BottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Ongoing_Orders extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_orders);

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch the ongoing orders
        FetchOrdersTask fetchOrdersTask = new FetchOrdersTask();
        fetchOrdersTask.execute();
    }

    private class FetchOrdersTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Set up the connection to the PHP file
                URL url = new URL("http://192.168.0.248/499_spareparts/ongoing_orders.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

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
            } catch (IOException e) {
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

                    if (jsonResponse.has("ongoing_orders")) {
                        JSONArray ordersArray = jsonResponse.getJSONArray("ongoing_orders");
                        List<Order> orders = new ArrayList<>();

                        for (int i = 0; i < ordersArray.length(); i++) {
                            JSONObject orderObject = ordersArray.getJSONObject(i);
                            String orderId = orderObject.getString("id");
                            String carType = orderObject.getString("type");
                            String sparePart = orderObject.getString("spare_part");
                            String priceRange = orderObject.getString("price_range");
                            String orderTime = orderObject.getString("created_at");
                            String orderStatus = orderObject.getString("status");
                            String userPhoneNumber = orderObject.getString("phone_number");
                            Order order = new Order(orderId, carType, sparePart, priceRange, orderTime, orderStatus, userPhoneNumber);
                            orders.add(order);
                        }

                        // Set up the RecyclerView adapter
                        orderAdapter = new OrderAdapter(orders);
                        orderAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Order order) {
                                // Handle item click event
                                Intent intent = new Intent(Ongoing_Orders.this, OrderDetails.class);
                                intent.putExtra("orderId", order.getOrderId());
                                intent.putExtra("carType", order.getCarType());
                                intent.putExtra("sparePart", order.getSparePart());
                                intent.putExtra("priceRange", order.getPriceRange());
                                intent.putExtra("orderTime", order.getOrderTime());
                                intent.putExtra("orderStatus", order.getOrderStatus());
                                intent.putExtra("phone_number" , order.getUserPhoneNumber());
                                startActivity(intent);
                            }
                        });
                        orderRecyclerView.setAdapter(orderAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    Toast.makeText(Ongoing_Orders.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle null response error
                Toast.makeText(Ongoing_Orders.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }
    }
}