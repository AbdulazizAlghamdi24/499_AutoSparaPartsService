package com.example.sparepart2.bottomNav;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.sparepart2.MainActivity;
import com.example.sparepart2.Profile;
import com.example.sparepart2.R;
import com.example.sparepart2.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationHelper {

    public static void setupBottomNavigation(BottomNavigationView bottomNavigationView, Context context) {

        bottomNavigationView.setSelectedItemId(R.id.settings);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.profile:
                        context.startActivity(new Intent(context, Profile.class));
                        if (context instanceof Activity) {
                            ((Activity) context).overridePendingTransition(0,0);
                        }
                        return true;


                    case R.id.settings:
                        context.startActivity(new Intent(context, Settings.class));
                        if (context instanceof Activity) {
                            ((Activity) context).overridePendingTransition(0,0);
                        }
                        return true;

                    case R.id.home:
                        context.startActivity(new Intent(context, MainActivity.class));
                        if (context instanceof Activity) {
                            ((Activity) context).overridePendingTransition(0,0);
                        }
                        return true;

                }
                return false;
            }
        });
    }
}
