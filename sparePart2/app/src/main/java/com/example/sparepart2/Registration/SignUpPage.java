package com.example.sparepart2.Registration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;


import com.example.sparepart2.IntroActivity;
import com.example.sparepart2.MainActivity;
import com.example.sparepart2.R;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;


public class SignUpPage extends AppCompatActivity {
    private TextInputLayout usernameEditText, emailEditText, passwordEditText, phoneEditText;
    private TextView LoginTextview;
    private Button signUpButton;

    private SharedPreferences sharedPreferences;
    private String masterKeyAlias = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

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

        LoginTextview = findViewById(R.id.LoginTextview);
        LoginTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpPage.this, LoginPage.class);
                startActivity(intent);
            }
        });

        // Initialize views
        usernameEditText = findViewById(R.id.Username);
        emailEditText = findViewById(R.id.Email);
        passwordEditText = findViewById(R.id.Password);
        phoneEditText = findViewById(R.id.PhoneNumber);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getEditText().getText().toString();
                String email = emailEditText.getEditText().getText().toString();
                String password = passwordEditText.getEditText().getText().toString();
                String phone = phoneEditText.getEditText().getText().toString();


                if (username.isEmpty()) {
                    usernameEditText.setError("Username is required");
                    usernameEditText.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                }

                // Validate email format using a regular expression
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (!email.matches(emailPattern)) {
                    emailEditText.setError("Invalid email format");
                    emailEditText.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }

                // Check if password length is less than 6 characters
                if (password.length() < 6) {
                    passwordEditText.setError("Password should be at least 6 characters long");
                    passwordEditText.requestFocus();
                    return;
                }

                if (phone.isEmpty()) {
                    phoneEditText.setError("Phone number is required");
                    phoneEditText.requestFocus();
                    return;
                }

                // Validate phone number format using a regular expression for Saudi numbers
                String phonePattern = "^\\+9665\\d{8}$";
                if (!phone.matches(phonePattern)) {
                    phoneEditText.setError("Invalid Saudi phone number. Must be in format +9665XXXXXXXX");
                    phoneEditText.requestFocus();
                    return;
                }

                // If all validations pass, proceed with signup process
                // ...

                // Create an instance of SignupTask and execute it
                SignupTask signupTask = new SignupTask();
                signupTask.execute(username, password, phone, email);
            }
        });
    }
    private class SignupTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String phone_number = params[2];
            String email = params[3];

            try {
                // Set up the connection to the PHP file
                URL url = new URL("http://192.168.0.248/499_spareparts/signup.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON data to send to the PHP file
                JSONObject requestData = new JSONObject();
                requestData.put("username", username);
                requestData.put("password", password);
                requestData.put("phone_number", phone_number);
                requestData.put("email", email);

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
                    Toast.makeText(SignUpPage.this, message, Toast.LENGTH_SHORT).show();

                    // Check if the registration was successful
                    if (message.equals("User registration successful.")) {
                        // Save user id to shared preferences
                        String userId = jsonResponse.getString("id");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(LoginPage.USER_ID, userId);
                        editor.apply();

                        // Start the desired activity after successful registration
                        Intent intent = new Intent(SignUpPage.this, IntroActivity.class);
                        startActivity(intent);
                        finish(); // Optional: Close the SignUpActivity so that pressing the back button won't bring it back
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    Toast.makeText(SignUpPage.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle null response error
                Toast.makeText(SignUpPage.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



