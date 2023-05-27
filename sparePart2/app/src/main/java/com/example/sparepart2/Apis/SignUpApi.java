package com.example.sparepart2.Apis;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpApi {
    private static final String SIGNUP_URL = "http://192.168.0.248/499_spareparts/signup.php";

    public interface SignUpCallback {
        void onSignUpResponse(String response);
    }

    public void registerUser(String username, String password, String phoneNumber, String email, SignUpCallback callback) {
        new SignupTask(username, password, phoneNumber, email, callback).execute();
    }

    private static class SignupTask extends AsyncTask<Void, Void, String> {
        private String username;
        private String password;
        private String phoneNumber;
        private String email;
        private SignUpCallback callback;

        SignupTask(String username, String password, String phoneNumber, String email, SignUpCallback callback) {
            this.username = username;
            this.password = password;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(SIGNUP_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject requestData = new JSONObject();
                requestData.put("username", username);
                requestData.put("password", password);
                requestData.put("phone_number", phoneNumber);
                requestData.put("email", email);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestData.toString().getBytes());
                outputStream.flush();
                outputStream.close();

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
            if (callback != null) {
                callback.onSignUpResponse(response);
            }
        }
    }
}
