package com.example.spotifyclone.Pages;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.spotifyclone.Authentication.openingPage;
import com.example.spotifyclone.BuildConfig;
import com.example.spotifyclone.Adapter.DataAdapter;
import com.example.spotifyclone.R;
import com.example.spotifyclone.api.DiscogsResponse;
import com.example.spotifyclone.api.MusicDataCallback;
import com.example.spotifyclone.api.MusicFetcher;
import com.example.spotifyclone.helpers.bottom_navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class homePage extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    //POP
    private RecyclerView popRecyclerView;
    private DataAdapter popAdapter;

    private List<DiscogsResponse.Result> popList = new ArrayList<>();

    //ROCK
    private RecyclerView rockRecyclerView;
    private DataAdapter rockAdapter;
    private List<DiscogsResponse.Result> rockList = new ArrayList<>();

    //EDM
    private RecyclerView hiphopRecyclerView;
    private DataAdapter hiphopAdapter;
    private List<DiscogsResponse.Result> hiphopList = new ArrayList<>();



    Button logOutButton;
    private static final String DiscogsToken = BuildConfig.DISCOGS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        logOutButton = findViewById(R.id.logout);

        popRecyclerView = findViewById(R.id.popularPop);
        popAdapter = new DataAdapter(this, popList);
        setUpRecyclers(popRecyclerView, popAdapter);


        rockRecyclerView = findViewById(R.id.popularRock);
        rockAdapter = new DataAdapter(this, rockList);
        setUpRecyclers(rockRecyclerView, rockAdapter);

        hiphopRecyclerView = findViewById(R.id.popularHiphop);
        hiphopAdapter = new DataAdapter(this, hiphopList);
        setUpRecyclers(hiphopRecyclerView, hiphopAdapter);



        BottomNavigationView botNav = findViewById(R.id.bottom_navigation_layout);
        bottom_navigation.setupBottomNav(this, botNav, R.id.nav_home);

    loadAllSections();
    logout(logOutButton);

    }

    private void setUpRecyclers(RecyclerView recycler, DataAdapter adapter){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
    }

    private void loadAllSections(){
        MusicFetcher fetcher = new MusicFetcher();
        loadMusic(fetcher, "Pop", popList, popAdapter);
        loadMusic(fetcher, "Rock", rockList, rockAdapter);
        loadMusic(fetcher, "Hip Hop", hiphopList, hiphopAdapter);
    }


    private void loadMusic(MusicFetcher fetcher, String genre, List<DiscogsResponse.Result> list, DataAdapter adapter){

        if (!list.isEmpty()){
            return;
        }

        fetcher.fetchByGenre(genre, DiscogsToken, new MusicDataCallback(){
            @Override
            public void onSuccess(List<DiscogsResponse.Result> results) {
                if (results != null){
                    list.clear();
                    list.addAll(results);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(String errorMessage) {
                Snackbar.make(popRecyclerView, errorMessage, Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void logout (Button logoutBtn){
        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                auth.signOut();

                Intent intent = new Intent(homePage.this, openingPage.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
