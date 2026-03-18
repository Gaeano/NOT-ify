package com.example.spotifyclone;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spotifyclone.helpers.bottom_navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class albumDetailsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_album_details_page);

        BottomNavigationView botNav = findViewById(R.id.bottom_navigation_layout);
        bottom_navigation.setupBottomNav(this, botNav, R.id.nav_library);
    }
}