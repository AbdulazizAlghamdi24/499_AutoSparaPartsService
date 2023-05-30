package com.example.sparepart2.Order_Status;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sparepart2.OrderHandling.Order;
import com.example.sparepart2.OrderHandling.OrderAdapter;
import com.example.sparepart2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Ongoing_Orders extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private Button loadMoreButton;
    private int currentPage = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_orders);

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        loadMoreButton = findViewById(R.id.loadMoreButton);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FetchOrdersTask fetchOrdersTask = new FetchOrdersTask();
        fetchOrdersTask.execute(currentPage);

        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchOrdersTask fetchMoreOrdersTask = new FetchOrdersTask();
                fetchMoreOrdersTask.execute(++currentPage);
            }
        });
    }

    private class FetchOrdersTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            try {
                URL url = new URL("http://192.168.0.248/499_spareparts/ongoing_orders.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("page", params[0]);
                String jsonData = jsonObject.toString();

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(jsonData);
                wr.flush();
                wr.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    response = response.trim();
                    while (!response.startsWith("{") && response.length() > 1) {
                        response = response.substring(1);
                    }

                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.has("ongoing_orders")) {
                        JSONArray ordersArray = jsonResponse.getJSONArray("ongoing_orders");
                        List<Order> newOrders = new ArrayList<>();

                        for (int i = 0; i < ordersArray.length(); i++) {
                            JSONObject orderObject = ordersArray.getJSONObject(i);
                            String orderId = orderObject.getString("id");
                            String carType = orderObject.getString("type");
                            String CarModel = orderObject.getString("model");
                            String sparePart = orderObject.getString("spare_part");
                            String ExtraDetails = orderObject.getString("extra_details");
                            String CarYear = orderObject.getString("year");
                            String priceRange = orderObject.getString("price_range");
                            String orderTime = orderObject.getString("created_at");
                            String orderStatus = orderObject.getString("status");
                            String userPhoneNumber = orderObject.getString("phone_number");
                            Order order = new Order(orderId, carType,CarModel, CarYear, sparePart,"", priceRange, orderTime, orderStatus, userPhoneNumber);
                            newOrders.add(order);
                        }

                        if (orderAdapter == null) {
                            orderAdapter = new OrderAdapter(newOrders);
                            orderAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Order order) {
                                    Intent intent = new Intent(Ongoing_Orders.this, OrderDetails.class);
                                    intent.putExtra("orderId", order.getOrderId());
                                    intent.putExtra("carType", order.getCarType());
                                    intent.putExtra("CarModel",order.getCarModel());
                                    intent.putExtra("CarYear", order.getCarYear());
                                    intent.putExtra("sparePart", order.getSparePart());
                                    intent.putExtra("ExtraDetails",order.getExtraDetails());
                                    intent.putExtra("priceRange", order.getPriceRange());
                                    intent.putExtra("orderTime", order.getOrderTime());
                                    intent.putExtra("orderStatus", order.getOrderStatus());
                                    intent.putExtra("phone_number" , order.getUserPhoneNumber());
                                    startActivity(intent);
                                }
                            });
                            orderRecyclerView.setAdapter(orderAdapter);
                        } else {
                            orderAdapter.addOrders(newOrders);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Ongoing_Orders.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Ongoing_Orders.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }
    }
}