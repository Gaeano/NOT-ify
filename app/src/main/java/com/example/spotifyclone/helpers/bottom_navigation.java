package com.example.spotifyclone.helpers;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.spotifyclone.Pages.homePage;
import com.example.spotifyclone.Pages.libraryPage;
import com.example.spotifyclone.Pages.searchPage;
import com.example.spotifyclone.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class bottom_navigation {

    public static void setupBottomNav(final Activity context, BottomNavigationView bottomNav, int currentItemId) {

        bottomNav.setSelectedItemId(currentItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(context, homePage.class);
                context.startActivity(intent);
                context.overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_search) {
                Intent intent = new Intent(context, searchPage.class);
                context.startActivity(intent);
                context.overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_library) {
                Intent intent = new Intent(context, libraryPage.class);
                context.startActivity(intent);
                context.overridePendingTransition(0, 0);
                return true;
            }
            return false;
            });
        }
    }



