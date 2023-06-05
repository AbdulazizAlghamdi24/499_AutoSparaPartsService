package com.example.sparepart2.Recogniton;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sparepart2.R;
import com.squareup.picasso.Picasso;

public class RecoDetailsPage extends AppCompatActivity {

    private ImageView imageView;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco_details_page);

        imageView = findViewById(R.id.image_view);
        resultText = findViewById(R.id.result_text);

        String apiResult = getIntent().getStringExtra("apiResult");
        String imageUrl = getIntent().getStringExtra("outputUrl");

        resultText.setText(apiResult);


        Picasso.get().load(imageUrl).into(imageView);
    }
}