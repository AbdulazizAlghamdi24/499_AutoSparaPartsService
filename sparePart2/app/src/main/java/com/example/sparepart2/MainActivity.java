package com.example.sparepart2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import com.example.sparepart2.ChatHandling.ChatActivity;
import com.example.sparepart2.Order_Status.Current_User_Order;
import com.example.sparepart2.Order_Status.Ongoing_Orders;
import com.example.sparepart2.OrderHandling.OrderPage;
import com.example.sparepart2.Recogniton.Damage;
import com.example.sparepart2.Recogniton.RecoDetailsPage;
import com.example.sparepart2.Registration.LoginPage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 101;
    private ImageView CreateOrderbtn;
    private ImageView ShowOrderbtn;
    private ImageView current_userbtn;
    private ImageView Api_recognizer;
    private ImageView Profile;
    private TextView usernameTextView;
    private ImageView Chat_btn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CreateOrderbtn = findViewById(R.id.create_order_btn);
        ShowOrderbtn = findViewById(R.id.show_order_btn);
        current_userbtn = findViewById(R.id.current_user_btn);
        Profile = findViewById(R.id.Profile);
        Api_recognizer = findViewById(R.id.Api_reco);
        usernameTextView = findViewById(R.id.username);
        Chat_btn = findViewById(R.id.Chat_btn);

        CreateOrderbtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OrderPage.class)));
        ShowOrderbtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Ongoing_Orders.class)));
        current_userbtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Current_User_Order.class)));
        Profile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Profile.class)));
        Chat_btn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChatActivity.class)));








        String masterKeyAlias;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    LoginPage.SHARED_PREFS,
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String username = sharedPreferences.getString(LoginPage.USERNAME, "default_username");
            usernameTextView.setText("Hi "+username);


          } catch (GeneralSecurityException | IOException e) {
        Toast.makeText(MainActivity.this, "Error retrieving user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }




        Api_recognizer.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){

            // Get the URI of the selected image
            Uri selectedImageUri = data.getData();
            // Start the upload of the image to Firebase Storage
            uploadImage(selectedImageUri);
        }
    }

//  upload an image to Firebase Storage
    private void uploadImage(Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("images/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> new ApiRequestTask(uri.toString()).execute()))
                .addOnFailureListener(exception -> Toast.makeText(MainActivity.this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private class ApiRequestTask extends AsyncTask<Void, Void, String> {
        private String imageUrl;

        public ApiRequestTask(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n" +
                    "    \"draw_result\": true,\n" +
                    "    \"remove_background\": false,\n" +
                    "    \"image\": \""+ imageUrl + "\"\n" +
                    "}");

            Request request = new Request.Builder()
                    .url("https://vehicle-damage-assessment.p.rapidapi.com/run")
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("X-RapidAPI-Key", "1827ee75a6msh688da8bb5d1d46fp17d3bejsn9020a7831e64")
                    .addHeader("X-RapidAPI-Host", "vehicle-damage-assessment.p.rapidapi.com")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return "Request failed with error code: " + response.code();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "An error occurred: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            Pair<List<Damage>, String> result = parseJson(response);
            if (result != null) {
                List<Damage> damages = result.first;
                String outputUrl = result.second;

                String damageString = formatDamageList(damages);

                Intent intent = new Intent(MainActivity.this, RecoDetailsPage.class);
                intent.putExtra("apiResult", damageString);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("outputUrl", outputUrl);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Unqualified image", Toast.LENGTH_SHORT).show();
            }
        }

        private Pair<List<Damage>, String> parseJson(String jsonString) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject output = jsonObject.getJSONObject("output");
                JSONArray elements = output.getJSONArray("elements");

                List<Damage> damages = new ArrayList<>();
                for (int i = 0; i < elements.length(); i++) {
                    JSONObject element = elements.getJSONObject(i);
                    Damage damage = new Damage();
                    damage.setDamageCategory(element.getString("damage_category"));
                    damage.setDamageLocation(element.getString("damage_location"));
                    damage.setScore(element.getDouble("score"));
                    damages.add(damage);
                }

                String outputUrl = jsonObject.getString("output_url");

                return new Pair<>(damages, outputUrl);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String formatDamageList(List<Damage> damages) {
            StringBuilder sb = new StringBuilder();
            for (Damage damage : damages) {
                sb.append("Damage Category: ").append(damage.getDamageCategory()).append("\n");
                sb.append("Damage Location: ").append(damage.getDamageLocation()).append("\n");
                sb.append("Score: ").append(damage.getScore()).append("\n");
                sb.append("\n");
            }
            return sb.toString();
        }
    }
}







