package com.example.sparepart2.ChatHandling;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.sparepart2.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private EditText etMessage;
    private Button btnSend;
    private RecyclerView rvChat;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private RequestQueue requestQueue; // Volley request queue
    private String conversationHistory = "Pretend you are an AI assistant for cars and car maintenance and spare parts expert.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        rvChat = findViewById(R.id.rvChat);
        requestQueue = Volley.newRequestQueue(this);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        rvChat.setAdapter(chatAdapter);
        rvChat.setLayoutManager(new LinearLayoutManager(this));

        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString();
            if (!message.trim().isEmpty()) {
                // Add the user's message to the chat
                chatMessages.add(new ChatMessage(message, true));
                chatAdapter.notifyDataSetChanged();
                etMessage.setText("");

                // Send the message to the server and get a response
                sendMessageToServer(message);
            }
        });
    }

    private void sendMessageToServer(String userMessage) {
        // Append the new user message to the conversation history
        conversationHistory += "\nUser: " + userMessage + "\nAI Assistant:";

        new Thread(() -> {
            try {
                URL url = new URL("https://chatai-499-api.herokuapp.com/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("message", conversationHistory);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                BufferedReader br;
                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }

                runOnUiThread(() -> {
                    try {
                        JSONObject responseObject = new JSONObject(sb.toString());
                        String responseMessage = responseObject.getString("message");

                        // Append the AI's response to the conversation history
                        conversationHistory += "\n" + responseMessage;

                        chatMessages.add(new ChatMessage(responseMessage, false));
                        chatAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}