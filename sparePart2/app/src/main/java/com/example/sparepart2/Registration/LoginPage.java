package com.example.sparepart2.Registration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sparepart2.IntroActivity;
import com.example.sparepart2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginPage extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView signUpTextView = findViewById(R.id.signUpTextView);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                // For example, start the SignUpActivity
                Intent intent = new Intent(LoginPage.this, SignUpPage.class);
                startActivity(intent);
            }
        });

        // Initialize the views
        etEmail = findViewById(R.id.Email);
        etPassword = findViewById(R.id.Password);
        btnLogin = findViewById(R.id.LoginButton);

        // Set the click listener for the login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the user input from the EditText fields
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString();

                // Perform input validations
                if (email.isEmpty()) {
                    etEmail.setError("Email is required");
                    etEmail.requestFocus();
                    return;
                }

                // Validate email format using a regular expression
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (!email.matches(emailPattern)) {
                    etEmail.setError("Invalid email format");
                    etEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                    return;
                }

                // If all validations pass, proceed with login process
                LoginTask loginTask = new LoginTask();
                loginTask.execute(email, password);
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // Create a JSON object with the login credentials
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", params[0]);
                jsonObject.put("password", params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Make an HTTP request to the PHP server
            String loginUrl = "http://192.168.0.248/499_spareparts/login.php";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(loginUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(jsonObject.toString().getBytes("UTF-8"));
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response from the server
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    inputStream.close();

                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
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
                    Toast.makeText(LoginPage.this, message, Toast.LENGTH_SHORT).show();

                    // Check if the registration was successful
                    if (message.equals("Login successful.")) {
                        // Start the desired activity after successful registration
                        Intent intent = new Intent(LoginPage.this, IntroActivity.class);
                        startActivity(intent);
                        finish(); // Optional: Close the SignUpActivity so that pressing the back button won't bring it back
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    Toast.makeText(LoginPage.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle null response error
                Toast.makeText(LoginPage.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }
    }
}