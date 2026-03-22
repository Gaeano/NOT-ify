package com.example.spotifyclone.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.Adapter.ArtistDetailsAdapter;
import com.example.spotifyclone.BuildConfig;
import com.example.spotifyclone.R;
import com.example.spotifyclone.api.ArtistDetailsCallback;
import com.example.spotifyclone.api.ArtistReleaseResponse;
import com.example.spotifyclone.api.ArtistReleasesCallback;
import com.example.spotifyclone.api.ArtistSearchCallback;
import com.example.spotifyclone.api.DiscogsResponse;
import com.example.spotifyclone.api.MusicFetcher;
import com.example.spotifyclone.helpers.bottom_navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class artistDetailsPage extends AppCompatActivity implements ArtistDetailsAdapter.OnItemClickListenerArtist {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    private Button backBtn;
    private TextView artistNameTv, artistDetailsTv, readMoreBtn;
    private ImageView artistImage;

    private RecyclerView releasesRecycler;

    private ArtistDetailsAdapter adapter;

    private List<DiscogsResponse.Result> resultList = new ArrayList<>();

    private List<ArtistReleaseResponse.Release> releaseList = new ArrayList<>();
    private static final String discogsToken = BuildConfig.DISCOGS_TOKEN;

    private boolean isBioExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_artist_details);

        backBtn = findViewById(R.id.back_btnA);
        artistImage = findViewById(R.id.artist_image);
        artistNameTv = findViewById(R.id.artist_name);
        artistDetailsTv = findViewById(R.id.artist_bio_text);
        readMoreBtn = findViewById(R.id.read_more_bio_btn);

        //set up recyclers
        releasesRecycler = findViewById(R.id.releases_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        releasesRecycler.setLayoutManager(linearLayoutManager);
        adapter = new ArtistDetailsAdapter(this, releaseList);
        releasesRecycler.setAdapter(adapter);

        backBtn.setOnClickListener(v -> finish());

        //set up bottom nav
        BottomNavigationView botNav = findViewById(R.id.bottom_navigation_layout);
        bottom_navigation.setupBottomNav(this, botNav, R.id.nav_home);

        //get passed data
        String artistName = getIntent().getStringExtra("artistName");

        if (artistName != null){
            artistNameTv.setText(artistName);

            MusicFetcher fetcher = new MusicFetcher();
            fetchArtistDetails(artistName, fetcher, resultList);
        } else {
            artistNameTv.setText("Unknown Artist");
        }

        readMoreBtn.setOnClickListener(v -> {
            if (isBioExpanded){
                artistDetailsTv.setMaxLines(4);
                readMoreBtn.setText("Read more");
                isBioExpanded = false;
            } else {
                artistDetailsTv.setMaxLines(Integer.MAX_VALUE);
                readMoreBtn.setText("Show less");
                isBioExpanded = true;
            }
        });


    }

    @Override
    public void onItemClick(int position) {
        ArtistReleaseResponse.Release clickedRelease = releaseList.get(position);

        String title = releaseList.get(position).title;
        String artist = artistNameTv.getText().toString();

        String fullTitle = artist + " - " + title;
        Intent intent = new Intent(this, albumDetailsPage.class);
        intent.putExtra("albumTitle", fullTitle);
        intent.putExtra("imageUrl", clickedRelease.thumb);

        // Pass the safe ID to the next page
        intent.putExtra("type", clickedRelease.type);
        intent.putExtra("masterId", clickedRelease.id);

        startActivity(intent);
    }

    private void fetchArtistDetails(String artistName, MusicFetcher fetcher, List<DiscogsResponse.Result> resultList) {
        fetcher.fetchArtist(discogsToken, artistName, new ArtistSearchCallback() {
            @Override
            public void onSuccess(DiscogsResponse.Result artistDetails) {


                int artistID = artistDetails.id;
                fetchArtistBio(artistID, fetcher);
                fetchArtistReleases(artistID, fetcher, releaseList, adapter);

                Log.d("artistDetailsPage", "Artist details fetched successfully. AristId: " + artistDetails.id);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("artistDetailsPage", "Error fetching artist details: " + errorMessage);
            }
        });
    }

    private void fetchArtistBio(int artistId, MusicFetcher fetcher){
        fetcher.fetchArtistDetails(discogsToken, artistId, new ArtistDetailsCallback() {
            @Override
            public void onSuccess(String bio, String imageUrl) {
                artistDetailsTv.setText(bio);

                if(imageUrl != null && !imageUrl.isEmpty()){
                    Glide.with(artistDetailsPage.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(artistImage);
                }

                Log.d("artistDetailsPage", "Artist bio and image fetched successfully.");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("artistDetailsPage", "Error fetching artist bio: " + errorMessage);
            }
        });

    }

    public void fetchArtistReleases(int artistId, MusicFetcher fetcher, List<ArtistReleaseResponse.Release> releaseList, ArtistDetailsAdapter adapter){
        fetcher.fetchArtistReleases(discogsToken, artistId, new ArtistReleasesCallback() {
            @Override
            public void onSuccess(List<ArtistReleaseResponse.Release> releases) {
                Log.d("artistDetailsPage", "Releases fetched successfully. Number of releases: " + releases.size());
                releaseList.clear();
                releaseList.addAll(releases);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("artistDetailsPage", "Error fetching artist releases: " + errorMessage);
        }
        });
    }
}


