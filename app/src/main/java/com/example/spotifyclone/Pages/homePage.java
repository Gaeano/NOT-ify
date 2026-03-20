package com.example.spotifyclone.Pages;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.spotifyclone.Authentication.openingPage;
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

public class homePage extends AppCompatActivity implements HomePageAdapter.OnClickItemListener {

    FirebaseAuth auth = FirebaseAuth.getInstance();

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



    Button logOutButton;
    private static final String DiscogsToken = BuildConfig.DISCOGS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        logOutButton = findViewById(R.id.logout);

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
    logout(logOutButton);

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
    public void logout (Button logoutBtn){
        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                auth.signOut();
                Log.d("HomePage", "User logged out");
                Intent intent = new Intent(homePage.this, openingPage.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
