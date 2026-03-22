package com.example.spotifyclone.Pages;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.Adapter.TracklistAdapter;
import com.example.spotifyclone.BuildConfig;
import com.example.spotifyclone.R;
import com.example.spotifyclone.api.DiscogsResponse;
import com.example.spotifyclone.api.MasterReleaseResponse;
import com.example.spotifyclone.api.MusicFetcher;
import com.example.spotifyclone.api.TracklistCallback;
import com.example.spotifyclone.helpers.bottom_navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class albumDetailsPage extends AppCompatActivity {

    Button backBtn;

    TextView albumTitleTv, artistsNameTv, noResultPrompt;

    ImageButton favoriteBtn;
    ImageView coverImage;

    ProgressBar loadingIcon;
    RecyclerView trackListRecycler;

    List<MasterReleaseResponse.Track> songList = new ArrayList<>();

    TracklistAdapter songListAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    private static final String DiscogsToken = BuildConfig.DISCOGS_TOKEN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_album_details_page);

        loadingIcon = findViewById(R.id.tracklistProgressBar);
        noResultPrompt = findViewById(R.id.no_list_prompt);

        //get passed data
        String fullTitle = getIntent().getStringExtra("albumTitle");
        int masterId = getIntent().getIntExtra("masterId", 0);
        String imageUrl = getIntent().getStringExtra("imageUrl");

        if (masterId == 0) {
            Log.e("album_details_activity", "Master ID is 0. The Intent didn't pass the data correctly");
        }

        String albumTitle = "Unknown Album";
        String artistsName = "Unknown Artist";

        if (fullTitle != null && fullTitle.contains(" -")) {
            String[] parts = fullTitle.split(" - ", 2);
            artistsName = parts[0];
            albumTitle = parts[1];
        } else if (fullTitle != null) {
            albumTitle = fullTitle;
        }


        //link attributes from xml to java
        albumTitleTv = findViewById(R.id.album_title);
        artistsNameTv = findViewById(R.id.album_artist);
        favoriteBtn = findViewById(R.id.saveAlbumBtn);
        coverImage = findViewById(R.id.album_cover);

        //set up header data
        albumTitleTv.setText(fullTitle);
        artistsNameTv.setText(artistsName);


        if (imageUrl != null) {
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


        if(songList.isEmpty()){
            loadingIcon.setVisibility(View.GONE);
            noResultPrompt.setVisibility(View.VISIBLE);
        }
        //check if album is saved
        checkAlbumIfSaved(favoriteBtn, masterId);

        //save album functionality
        savedAlbumFunctionality(favoriteBtn, fullTitle, masterId, imageUrl);

    }


    private void backButtonFunctionality(Button backBtn) {
        backBtn.setOnClickListener(v -> finish());
    }

    private void populateTrackList(MusicFetcher fetcher, TracklistAdapter adapter, List<MasterReleaseResponse.Track> trackList, int masterId) {
        fetcher.fetchTrackList(DiscogsToken, masterId, new TracklistCallback() {
            @Override
            public void onSuccess(List<MasterReleaseResponse.Track> results) {
                if (results != null) {
                    loadingIcon.setVisibility(View.GONE);
                    trackListRecycler.setVisibility(View.VISIBLE);
                    Log.d("album_details_activity", "Track list fetched successfully");
                    trackList.clear();
                    trackList.addAll(results);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String errorMessage) {
                loadingIcon.setVisibility(View.GONE);
                trackListRecycler.setVisibility(View.GONE);
                noResultPrompt.setVisibility(View.VISIBLE);
                Log.e("album_details_activity", errorMessage);
                // Handle error
            }
        });
    }

    private void savedAlbumFunctionality(ImageButton favoriteBtn, String albumTitle, int id, String imageUrl) {
        favoriteBtn.setOnClickListener(v -> {


            if (user != null) {
                String userId = user.getUid();
                favoriteBtn.setImageResource(R.drawable.favorite_on_24px);

                Map<String, Object> albumData = new HashMap<>();
                albumData.put("id", id);
                albumData.put("title", albumTitle);
                albumData.put("imageUrl", imageUrl);
                albumData.put("timestamp", FieldValue.serverTimestamp());

                db.collection("users")
                        .document(userId)
                        .collection("savedPlaylists")
                        .document(String.valueOf(id))
                        .set(albumData)
                        .addOnSuccessListener(aVoid ->{
                            Log.d("album_details_activity", "Album saved successfully to database title: " + albumTitle);

                        })
                        .addOnFailureListener(errorMessage ->{
                            Log.e("album_details_activity", "Failed to save album to database: " + errorMessage.getMessage());
                        });
            }
        });
    }

    private void checkAlbumIfSaved(ImageButton favoriteBtn, int id){
        List<DiscogsResponse.Result> savedList = new ArrayList<>();

        if (user != null){
            String userId = user.getUid();

            db.collection("users")
                    .document(userId)
                    .collection("savedPlaylists")
                    .document(String.valueOf(id))
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()){
                            favoriteBtn.setImageResource(R.drawable.favorite_on_24px);
                        } else {
                            favoriteBtn.setImageResource(R.drawable.favorite_off_24px);
                        }
                    });
        }

    }
}
