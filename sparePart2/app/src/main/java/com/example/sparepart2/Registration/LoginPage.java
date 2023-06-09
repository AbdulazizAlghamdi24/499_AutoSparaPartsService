package com.example.sparepart2.Registration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.sparepart2.MainActivity;
import com.example.sparepart2.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;


public class LoginPage extends AppCompatActivity {

    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTextInput;
    private Button loginButton;
    private TextView signUpTextView;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String USER_ID = "id";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";

    public static final String Phone_Number = "phone_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        try {
            checkBox();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Initialize the views
        emailTextInput = findViewById(R.id.Email);
        passwordTextInput = findViewById(R.id.Password);
        loginButton = findViewById(R.id.LoginButton);
        signUpTextView = findViewById(R.id.signUpTextView);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the user input from the TextInputLayout fields
                String email = emailTextInput.getEditText() != null ? emailTextInput.getEditText().getText().toString().trim() : "";
                String password = passwordTextInput.getEditText() != null ? passwordTextInput.getEditText().getText().toString().trim() : "";

                // Perform input validations
                if (TextUtils.isEmpty(email)) {
                    emailTextInput.setError("Email is required");
                    emailTextInput.requestFocus();
                    return;
                }


                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (!email.matches(emailPattern)) {
                    emailTextInput.setError("Invalid email format");
                    emailTextInput.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordTextInput.setError("Password is required");
                    passwordTextInput.requestFocus();
                    return;
                }


                LoginTask loginTask = new LoginTask();
                loginTask.execute(email, password);
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, SignUpPage.class);
                startActivity(intent);
            }
        });
    }



    private void checkBox() throws GeneralSecurityException, IOException {
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                SHARED_PREFS,
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        // indicates if the user is logged in and USER_ID is a string that stores the user's ID.
        boolean isLoggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN, false);
        String userId = sharedPreferences.getString(USER_ID, "0");
        // checking if the user is already logged in
        if (isLoggedIn) {
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            intent.putExtra("id", userId);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("StaticFieldLeak")

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // // Create a JSON object to hold login credentials
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", params[0]);
                jsonObject.put("password", params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String loginUrl = "https://spare-parts-php.herokuapp.com/login.php";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(loginUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                // Set up the connection for a POST request
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Write the JSON object
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(jsonObject.toString().getBytes("UTF-8"));
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
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
                response = response.trim();

                while (response.charAt(0) != '{' && response.length() > 1) {
                    response = response.substring(1);
                }

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String message = jsonResponse.getString("message");

                    Toast.makeText(LoginPage.this, message, Toast.LENGTH_SHORT).show();

                    if (message.equals("Login successful.")) {
                        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

                        JSONObject userJson = jsonResponse.getJSONObject("user");
                        String userId = userJson.getString("id");
                        String username = userJson.getString("username");
                        String email = userJson.getString("email");
                        String phone_number = userJson.getString("phone_number");



                        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                                SHARED_PREFS,
                                masterKeyAlias,
                                LoginPage.this,
                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        );
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        // Save the user's login status, ID, username, email, and phone number in the SharedPreferences
                        editor.putBoolean(IS_LOGGED_IN, true);
                        editor.putString(USER_ID, userId);
                        editor.putString(USERNAME, username);
                        editor.putString(EMAIL, email);
                        editor.putString(Phone_Number,phone_number);
                        editor.apply();

                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException | GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginPage.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginPage.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }
    }
}