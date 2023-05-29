package com.example.sparepart2.ChatHandling;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sparepart2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private EditText etMessage;
    private Button btnSend;
    private RecyclerView rvChat;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private RequestQueue requestQueue; // Volley request queue

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        rvChat = findViewById(R.id.rvChat);
        requestQueue = Volley.newRequestQueue(this); // Initialize the RequestQueue

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        rvChat.setAdapter(chatAdapter);
        rvChat.setLayoutManager(new LinearLayoutManager(this));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                if (!message.trim().isEmpty()) {
                    // Add the user's message to the chat
                    chatMessages.add(new ChatMessage(message, true));
                    chatAdapter.notifyDataSetChanged();
                    etMessage.setText("");

                    // Send the message to the server and get a response
                    sendMessageToServer(message);
                }
            }
        });
    }

    private void sendMessageToServer(String message) {
        String url = "https://chatai-499-api.herokuapp.com/"; // Replace with your server URL
        JSONObject postData = new JSONObject();
        try {
            postData.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract the AI assistant's message from the response
                            String responseMessage = response.getString("message");
                            // Add the response to the chat
                            chatMessages.add(new ChatMessage(responseMessage, false));
                            chatAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(ChatActivity.this, "An error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Clear the cache
        requestQueue.getCache().clear();

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
}