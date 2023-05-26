package com.example.sparepart2;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VehiclePartsRecognitionTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "VehiclePartsRecognition";
    private static final String API_URL = "https://zylalabs.com/api/1583/vehicle+parts+recognizer+api/1271/recognize";
    private static final String API_KEY = "1302|MBGv6b4Z9epPC6GaQ3Ta9nWaROWpTUbuQyXFdJAj";

    private VehiclePartsRecognitionListener listener;

    public VehiclePartsRecognitionTask(VehiclePartsRecognitionListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... urls) {
        if (urls.length == 0) {
            return null;
        }

        String imageUrl = urls[0];

        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url(API_URL + "?inputurl=" + imageUrl)
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                Log.e(TAG, "Request failed: " + response.code() + " - " + response.message());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error making API request: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            listener.onRecognitionSuccess(result);
        } else {
            listener.onRecognitionFailure();
        }
    }

    public interface VehiclePartsRecognitionListener {
        void onRecognitionSuccess(String response);
        void onRecognitionFailure();
    }
}
