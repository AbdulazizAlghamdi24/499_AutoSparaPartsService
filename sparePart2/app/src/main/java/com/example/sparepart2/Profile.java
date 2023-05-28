package com.example.sparepart2;


import static com.example.sparepart2.Settings.SHARED_PREFS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.bumptech.glide.Glide;
import com.example.sparepart2.Registration.LoginPage;
import com.example.sparepart2.bottomNav.BottomNavigationHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Profile extends AppCompatActivity {
    private TextView usernameTextView;
    private TextView emailTextView;
    private ImageView profileImg;
    private StorageReference storageReference;  // Firebase storage reference

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        //BottomNavigationHelper.setupBottomNavigation(bottomNavigationView, this);

        // Initialize views
        usernameTextView = findViewById(R.id.profile_name);
        emailTextView = findViewById(R.id.profile_email);
        profileImg = findViewById(R.id.profile_image);

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1000);
            }
        });

        // Retrieve username and email from EncryptedSharedPreferences
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
            String email = sharedPreferences.getString(LoginPage.EMAIL, "default_email");

            // Set the username and email in the TextViews
            usernameTextView.setText(username);
            emailTextView.setText(email);

            // Load the profile image from Firebase Storage
            loadProfileImage(username);

        } catch (GeneralSecurityException | IOException e) {
            Toast.makeText(Profile.this, "Error retrieving user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileImage(String username) {
        StorageReference profileImgRef = storageReference.child("profile_images/" + username + ".jpg");
        profileImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(Profile.this)
                        .load(uri)
                        .into(profileImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Profile.this, "Error loading profile image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri imageUri = data.getData();
                profileImg.setImageURI(imageUri);

                // Get username from SharedPreferences
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

                    // Upload profile image to Firebase Storage
                    uploadProfileImage(username, imageUri);

                } catch (GeneralSecurityException | IOException e) {
                    Toast.makeText(Profile.this, "Error retrieving username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadProfileImage(String username, Uri imageUri) {
        StorageReference profileImgRef = storageReference.child("profile_images/" + username + ".jpg");
        profileImgRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(Profile.this, "Profile image uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Profile.this, "Error uploading profile image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


