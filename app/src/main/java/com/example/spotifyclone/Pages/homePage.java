package com.example.spotifyclone.Pages;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.spotifyclone.Authentication.loginAcc;
import com.example.spotifyclone.BuildConfig;
import com.example.spotifyclone.Adapter.ImageAdapter;
import com.example.spotifyclone.R;
import com.example.spotifyclone.api.DiscogsResponse;
import com.example.spotifyclone.api.MusicDataCallback;
import com.example.spotifyclone.api.MusicFetcher;
import com.example.spotifyclone.helpers.bottom_navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class homePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    private List<DiscogsResponse.Result>musicList;

    Button logOutButton;
    private static final String DiscogsToken = BuildConfig.DISCOGS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        logOutButton = findViewById(R.id.logout);


        recyclerView = findViewById(R.id.recentArtists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        musicList = new ArrayList<>();

        adapter = new ImageAdapter(this, musicList);
        recyclerView.setAdapter(adapter);

        BottomNavigationView botNav = findViewById(R.id.bottom_navigation_layout);
        bottom_navigation.setupBottomNav(this, botNav, R.id.nav_home);

    loadMusic();
    logout(logOutButton);




    }

    private void loadMusic(){
        MusicFetcher musicfetch = new MusicFetcher();

        musicfetch.fetchMusicData("popular", DiscogsToken, new MusicDataCallback(){
            @Override
            public void onSuccess(List<DiscogsResponse.Result> results) {
                if (results != null){
                    musicList.clear();
                    musicList.addAll(results);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(String errorMessage) {
                Snackbar.make(recyclerView, errorMessage, Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void logout (Button logoutBtn){
        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(homePage.this, loginAcc.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
