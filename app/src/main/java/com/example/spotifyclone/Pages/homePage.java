package com.example.spotifyclone.Pages;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.spotifyclone.BuildConfig;
import com.example.spotifyclone.Adapter.HomePageAdapter;
import com.example.spotifyclone.R;
import com.example.spotifyclone.api.DiscogsResponse;
import com.example.spotifyclone.api.MusicDataCallback;
import com.example.spotifyclone.api.MusicFetcher;
import com.example.spotifyclone.helpers.bottom_navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class homePage extends AppCompatActivity implements HomePageAdapter.OnClickItemListener {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    private ImageButton settingsBtn, musicRandomizerBtn;

    //POP
    private RecyclerView popRecyclerView;
    private HomePageAdapter popAdapter;

    private List<DiscogsResponse.Result> popList = new ArrayList<>();

    //ROCK
    private RecyclerView rockRecyclerView;
    private HomePageAdapter rockAdapter;
    private List<DiscogsResponse.Result> rockList = new ArrayList<>();

    //EDM
    private RecyclerView hiphopRecyclerView;
    private HomePageAdapter hiphopAdapter;
    private List<DiscogsResponse.Result> hiphopList = new ArrayList<>();



    private static final String DiscogsToken = BuildConfig.DISCOGS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        settingsBtn = findViewById(R.id.settingsBtn);
        musicRandomizerBtn = findViewById(R.id.music_randomizer);

        musicRandomizerBtn.setOnClickListener(v -> {
            randomizerFeature();
        });


        popRecyclerView = findViewById(R.id.popularPop);
        popAdapter = new HomePageAdapter(this, popList);
        setUpRecyclers(popRecyclerView, popAdapter);


        rockRecyclerView = findViewById(R.id.popularRock);
        rockAdapter = new HomePageAdapter(this, rockList);
        setUpRecyclers(rockRecyclerView, rockAdapter);

        hiphopRecyclerView = findViewById(R.id.popularHiphop);
        hiphopAdapter = new HomePageAdapter(this, hiphopList);
        setUpRecyclers(hiphopRecyclerView, hiphopAdapter);



        BottomNavigationView botNav = findViewById(R.id.bottom_navigation_layout);
        bottom_navigation.setupBottomNav(this, botNav, R.id.nav_home);

        loadAllSections();
        settingsBtn.setOnClickListener(v ->{
            Intent intent = new Intent(this, settingsPage.class);
            startActivity(intent);
        });

    }

    @Override
    public void onItemClick(DiscogsResponse.Result result) {

        Intent intent = new Intent(this, albumDetailsPage.class);
        Log.d("HomePage", "Item clicked title: " + result.title);
        intent.putExtra("albumTitle", result.title);
        intent.putExtra("masterId", result.masterId);
        intent.putExtra("imageUrl", result.coverImage);
        startActivity(intent);
    }

    private void setUpRecyclers(RecyclerView recycler, HomePageAdapter adapter){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        Log.d("HomePage", "Setting up recycler view...");
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
    }

    private void loadAllSections(){
        MusicFetcher fetcher = new MusicFetcher();
        Log.d("HomePage", "Loading music sections data...");
        loadMusic(fetcher, "Pop", popList, popAdapter);
        loadMusic(fetcher, "Rock", rockList, rockAdapter);
        loadMusic(fetcher, "Hip Hop", hiphopList, hiphopAdapter);
    }


    private void loadMusic(MusicFetcher fetcher, String genre, List<DiscogsResponse.Result> list, HomePageAdapter adapter){

        if (!list.isEmpty()){
            return;
        }

        fetcher.fetchByGenre(genre, DiscogsToken, new MusicDataCallback(){
            @Override
            public void onSuccess(List<DiscogsResponse.Result> results) {
                if (results != null){
                    Log.d("HomePage", "Loaded " + results.size() + " items for genre: " + genre);
                    list.clear();
                    list.addAll(results);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(String errorMessage) {

                Log.e("HomePage", "Error loading data: " + errorMessage);
            }

        });
    }

    private void randomizerFeature(){
        Toast.makeText(this, "Shuffling...", Toast.LENGTH_SHORT).show();

        String[] genres = {"Rock", "Pop", "Jazz", "Hip Hop", "Electronic", "1980", "1999", "2010", "Love", "Night"};

        int randomIndex = new Random().nextInt(genres.length);
        String searchQuery = genres[randomIndex];

        MusicFetcher fetcher = new MusicFetcher();
        fetcher.fetchSearched(searchQuery, DiscogsToken, new MusicDataCallback() {
            @Override
            public void onSuccess(List<DiscogsResponse.Result> results) {

                if(results != null && !results.isEmpty()){
                    int randomAlbumIndex = new Random().nextInt(results.size());
                    DiscogsResponse.Result random = results.get(randomAlbumIndex);


                    Intent intent = new Intent(homePage.this, albumDetailsPage.class);
                    intent.putExtra("albumTitle", random.title);
                    intent.putExtra("masterId", random.id);
                    intent.putExtra("imageUrl", random.coverImage);
                    intent.putExtra("type", random.type);
                    startActivity(intent);
                } else {
                    Log.e("HomePage", "No results found for the random genre.");
                }

            }

            @Override
            public void onError(String errorMessage) {
                Log.e("HomePage", "Error loading random data: " + errorMessage);
            }
        });
    }



}
