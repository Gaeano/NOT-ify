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

    private Button backBtn;

    private TextView albumTitleTv, artistsNameTv, noResultPrompt;

    private ImageButton favoriteBtn;
    private ImageView coverImage;

    private ProgressBar loadingIcon;
    private RecyclerView trackListRecycler;

    private List<MasterReleaseResponse.Track> songList = new ArrayList<>();

    private TracklistAdapter songListAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private Boolean isAlbumSaved = false;
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
        //check if album is saved
        checkAlbumIfSaved(favoriteBtn, masterId);

        //save album functionality
        setupFavoriteToggle(favoriteBtn, fullTitle, masterId, imageUrl);

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
                    noResultPrompt.setVisibility(View.GONE);
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


    private void setupFavoriteToggle(ImageButton favoriteBtn, String albumTitle, int id, String imageUrl) {
        favoriteBtn.setOnClickListener(v -> {
            if (user == null) return;
            String userId = user.getUid();

            if (isAlbumSaved) {
                // SCENARIO 1: It is currently saved, so we want to REMOVE it.
                isAlbumSaved = false; // Update memory instantly
                favoriteBtn.setImageResource(R.drawable.favorite_off_24px); // Turn heart gray

                db.collection("users").document(userId).collection("savedPlaylists").document(String.valueOf(id))
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d("album_details", "Removed!"))
                        .addOnFailureListener(e -> {
                            // If the internet fails, revert the button!
                            isAlbumSaved = true;
                            favoriteBtn.setImageResource(R.drawable.favorite_on_24px);
                        });

            } else {
                // SCENARIO 2: It is NOT saved, so we want to ADD it.
                isAlbumSaved = true; // Update memory instantly
                favoriteBtn.setImageResource(R.drawable.favorite_on_24px); // Turn heart green

                Map<String, Object> albumData = new HashMap<>();
                albumData.put("id", id);
                albumData.put("title", albumTitle);
                albumData.put("imageUrl", imageUrl);
                albumData.put("timestamp", FieldValue.serverTimestamp());

                db.collection("users").document(userId).collection("savedPlaylists").document(String.valueOf(id))
                        .set(albumData)
                        .addOnSuccessListener(aVoid -> Log.d("album_details", "Saved!"))
                        .addOnFailureListener(e -> {
                            // If the internet fails, revert the button!
                            isAlbumSaved = false;
                            favoriteBtn.setImageResource(R.drawable.favorite_off_24px);
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
                            isAlbumSaved = true;
                        } else {
                            favoriteBtn.setImageResource(R.drawable.favorite_off_24px);
                            isAlbumSaved = false;
                        }
                    });
        }

    }
}
