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

    // ========== CONCERT VIEWS ==========
    private TextView concertVenue1, concertDate1, concertPrice1;
    private TextView concertVenue2, concertDate2, concertPrice2;
    private TextView concertVenue3, concertDate3, concertPrice3;

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

        // ========== LINK CONCERT VIEWS ==========
        concertVenue1 = findViewById(R.id.concert_venue_1);
        concertDate1  = findViewById(R.id.concert_date_1);
        concertPrice1 = findViewById(R.id.concert_price_1);

        concertVenue2 = findViewById(R.id.concert_venue_2);
        concertDate2  = findViewById(R.id.concert_date_2);
        concertPrice2 = findViewById(R.id.concert_price_2);

        concertVenue3 = findViewById(R.id.concert_venue_3);
        concertDate3  = findViewById(R.id.concert_date_3);
        concertPrice3 = findViewById(R.id.concert_price_3);

        // ========== POPULATE CONCERT DATA ==========
        // TODO: replace with real API data when tour is announced
        concertVenue1.setText("Madison Square Garden, New York");
        concertDate1.setText("📅  Dec 5, 2026");
        concertPrice1.setText("🎟  ₱150 - ₱500");

        concertVenue2.setText("O2 Arena, London");
        concertDate2.setText("📅  Dec 12, 2026");
        concertPrice2.setText("🎟  ₱120 - ₱450");

        concertVenue3.setText("Rod Laver Arena, Melbourne");
        concertDate3.setText("📅  Jan 8, 2027");
        concertPrice3.setText("🎟  ₱100 - ₱400");

        // set up recyclers
        releasesRecycler = findViewById(R.id.releases_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        releasesRecycler.setLayoutManager(linearLayoutManager);
        adapter = new ArtistDetailsAdapter(this, releaseList);
        releasesRecycler.setAdapter(adapter);

        backBtn.setOnClickListener(v -> finish());

        // set up bottom nav
        BottomNavigationView botNav = findViewById(R.id.bottom_navigation_layout);
        bottom_navigation.setupBottomNav(this, botNav, R.id.nav_home);

        // get passed data
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

    // ... rest of your methods stay exactly the same ...

    @Override
    public void onItemClick(int position) {
        ArtistReleaseResponse.Release clickedRelease = releaseList.get(position);

        String title = releaseList.get(position).title;
        String artist = artistNameTv.getText().toString();

        String fullTitle = artist + " - " + title;
        Intent intent = new Intent(this, albumDetailsPage.class);
        intent.putExtra("albumTitle", fullTitle);
        intent.putExtra("imageUrl", clickedRelease.thumb);
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
                Log.d("artistDetailsPage", "Artist details fetched successfully. ArtistId: " + artistDetails.id);
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


