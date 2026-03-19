package com.example.spotifyclone;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.Adapter.TracklistAdapter;
import com.example.spotifyclone.api.MasterReleaseResponse;
import com.example.spotifyclone.api.MusicFetcher;
import com.example.spotifyclone.api.TracklistCallback;
import com.example.spotifyclone.helpers.bottom_navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class albumDetailsPage extends AppCompatActivity {

    Button backBtn;

    TextView albumTitleTv, artistsNameTv;

    ImageButton favoriteBtn;
    ImageView coverImage;

    RecyclerView trackListRecycler;

    List<MasterReleaseResponse.Track> songList = new ArrayList<>();

    TracklistAdapter songListAdapter;



    private static final String DiscogsToken = BuildConfig.DISCOGS_TOKEN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_album_details_page);

        //get passed data
        String fullTitle = getIntent().getStringExtra("albumTitle");
        int masterId = getIntent().getIntExtra("masterId", 0);
        String imageUrl = getIntent().getStringExtra("imageUrl");

        if (masterId == 0) {
            Log.e("album_details_activity", "Master ID is 0. The Intent didn't pass the data correctly");
        }

        String albumTitle = "Unknown Album";
        String artistsName = "Unknown Artist";

        if (fullTitle != null && fullTitle.contains(" -" )){
            String[] parts = fullTitle.split(" - ", 2);
            artistsName = parts[0];
            albumTitle = parts[1];
        } else if (fullTitle != null){
            albumTitle = fullTitle;
        }


        //link attributes from xml to java
        albumTitleTv = findViewById(R.id.album_title);
        artistsNameTv  = findViewById(R.id.album_artist);
        favoriteBtn = findViewById(R.id.saveAlbumBtn);
        coverImage = findViewById(R.id.album_cover);

        //set up header data
        albumTitleTv.setText(fullTitle);
        artistsNameTv.setText(artistsName);




        if (imageUrl != null){
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(coverImage);
        }

        //set up bottom Nav
        BottomNavigationView botNav = findViewById(R.id.bottom_navigation_layout);
        bottom_navigation.setupBottomNav(this, botNav, R.id.nav_library);

        // back buttonn func
        backBtn = findViewById(R.id.backBtn);
        backButtonFunctionality(backBtn);

        //set up recycler
        trackListRecycler = findViewById(R.id.tracklistRecyclerView);
        songListAdapter = new TracklistAdapter(songList, artistsName);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        trackListRecycler.setLayoutManager(linearLayoutManager);
        trackListRecycler.setAdapter(songListAdapter);

        populateTrackList(new MusicFetcher(), songListAdapter, songList, masterId);






    }

    private void backButtonFunctionality(Button backBtn){
        backBtn.setOnClickListener(v -> finish());
    }

    private void populateTrackList(MusicFetcher fetcher, TracklistAdapter adapter, List<MasterReleaseResponse.Track> trackList, int masterId){
            fetcher.fetchTrackList(DiscogsToken, masterId, new TracklistCallback() {
                @Override
                public void onSuccess(List<MasterReleaseResponse.Track> results) {
                    if (results != null) {
                        Log.d("album_details_activity", "Track list fetched successfully");
                        trackList.clear();
                        trackList.addAll(results);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("album_details_activity", errorMessage);
                    // Handle error
                }
            });
    }

    }
