package com.example.sparepart2.Registration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.sparepart2.Apis.SignUpApi;
import com.example.sparepart2.IntroActivity;
import com.example.sparepart2.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;


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

        LoginTextview = findViewById(R.id.LoginTextview);

        LoginTextview.setOnClickListener(v -> startActivity(new Intent(SignUpPage.this, LoginPage.class)));


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
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        // Initialize views
        usernameEditText = findViewById(R.id.Username);
        emailEditText = findViewById(R.id.Email);
        passwordEditText = findViewById(R.id.Password);
        phoneEditText = findViewById(R.id.PhoneNumber);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(v -> {
            String username = Objects.requireNonNull(usernameEditText.getEditText()).getText().toString();
            String email = Objects.requireNonNull(emailEditText.getEditText()).getText().toString();
            String password = Objects.requireNonNull(passwordEditText.getEditText()).getText().toString();
            String phone = Objects.requireNonNull(phoneEditText.getEditText()).getText().toString();


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
            new SignUpApi().registerUser(username, password, phone, email, response -> {
                // Handle the signup response here, similar to your existing onPostExecute() method.
                if (response != null) {
                    response = response.trim();

                    while (response.charAt(0) != '{' && response.length() > 1) {
                        response = response.substring(1);
                    }

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString("message");

                        Toast.makeText(SignUpPage.this, message, Toast.LENGTH_SHORT).show();

                        if (message.equals("User registration successful.")) {
                            String userId = jsonResponse.getString("id");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(LoginPage.USER_ID, userId);
                            editor.apply();

                            Intent intent = new Intent(SignUpPage.this, IntroActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SignUpPage.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpPage.this, "Null response received", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}



