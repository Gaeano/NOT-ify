package com.example.spotifyclone.Pages;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.Adapter.DataAdapter;
import com.example.spotifyclone.Adapter.SearchAdapter;
import com.example.spotifyclone.BuildConfig;
import com.example.spotifyclone.R;
import com.example.spotifyclone.albumDetailsPage;
import com.example.spotifyclone.api.DiscogsApiService;
import com.example.spotifyclone.api.DiscogsResponse;
import com.example.spotifyclone.api.MusicDataCallback;
import com.example.spotifyclone.api.MusicFetcher;
import com.example.spotifyclone.helpers.bottom_navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class searchPage extends AppCompatActivity implements SearchAdapter.OnClickItemListener {

    LinearLayout recentSearchLayout;
    RecyclerView recentSearchesRecycler;
    RecyclerView searchRecycler;
    SearchAdapter searchAdapter, recentSearchesAdapter;


    private static final String TAG = "SearchActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;


    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private static final String DiscogsToken = BuildConfig.DISCOGS_TOKEN;

    List<DiscogsResponse.Result> resultList = new ArrayList<>();
    List<DiscogsResponse.Result> recentSearchesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_page);

        BottomNavigationView botNav = findViewById(R.id.bottom_navigation_layout);
        bottom_navigation.setupBottomNav(this, botNav, R.id.nav_search);
        recentSearchLayout = findViewById(R.id.recentSearchesLayout);

        SearchView searchView = findViewById(R.id.search_bar_container);


        //set up searchResults
        searchRecycler = findViewById(R.id.searchResultsRecyclerView);
        searchAdapter = new SearchAdapter(this, resultList);
        setUpRecyclers(searchRecycler, searchAdapter);

        //recent Searches
        recentSearchesRecycler = findViewById(R.id.recent_searchesRecylerView);
        recentSearchesAdapter = new SearchAdapter(this, recentSearchesList);
        setUpRecyclers(recentSearchesRecycler, recentSearchesAdapter);


        showRecentSearches(recentSearchesList, recentSearchesAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchRunnable != null){
                    handler.removeCallbacks(searchRunnable);
                }

                if (newText.trim().isEmpty()){
                    recentSearchLayout.setVisibility(View.VISIBLE);
                    searchRecycler.setVisibility(View.GONE);


                    showRecentSearches(recentSearchesList, recentSearchesAdapter);

                } else {
                    searchRunnable = new Runnable() {
                        @Override
                        public void run() {
                            recentSearchLayout.setVisibility(View.GONE);
                            searchRecycler.setVisibility(View.VISIBLE);
                            fetchSearchedMusic(new MusicFetcher(), newText, resultList, searchAdapter);

                        }
                    };

                    handler.postDelayed(searchRunnable, 500);
                }
                return false;
            }
        });




    }

    @Override
    public void onItemClick(int position){
        DiscogsResponse.Result clickedItem;

        if (recentSearchLayout.getVisibility() == View.VISIBLE) {
            clickedItem = recentSearchesList.get(position);
        } else {
            clickedItem = resultList.get(position);
        }

        addRecentSearchToDb(clickedItem);

        Log.d("SearchActivity", "Click worked! Position" + position);

        Intent intent = new Intent(this, albumDetailsPage.class);
        intent.putExtra("albumTitle",clickedItem.title);
        intent.putExtra("masterId", clickedItem.id);
        intent.putExtra("imageUrl", clickedItem.coverImage);
        startActivity(intent);
    }

    private void setUpRecyclers(RecyclerView recycler, SearchAdapter adapter){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
    }

    private void fetchSearchedMusic(MusicFetcher fetcher, String query, List<DiscogsResponse.Result> resultList, SearchAdapter adapter){

        fetcher.fetchSearched(query, DiscogsToken, new MusicDataCallback() {
            @Override
            public void onSuccess(List<DiscogsResponse.Result> results) {
                if (results != null){
                    resultList.clear();
                    resultList.addAll(results);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Snackbar.make(searchRecycler, errorMessage, Snackbar.LENGTH_SHORT).show();

            }
        });
    }

    private void showRecentSearches(List<DiscogsResponse.Result> recentList, SearchAdapter adapter){

        TextView nosearchesWarning = findViewById(R.id.recentSearchWarning);
        ProgressBar progressBar = findViewById(R.id.recentSearchesProgressBar);


        user = auth.getCurrentUser();
        if (user == null){
            Log.e(TAG, "user not logged in");
            return;
        }

        String userId = user.getUid();
        Log.d(TAG, "User email is: " + user.getEmail());

        recentSearchLayout.setVisibility(View.VISIBLE);
        searchRecycler.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE); // Show Spinner
        recentSearchesRecycler.setVisibility(View.GONE); // Hide List
        nosearchesWarning.setVisibility(View.GONE); // Hide Warning

        db.collection("users")
                .document(userId)
                .collection("recentSearches")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    recentList.clear();
                    Log.d(TAG, "Snapshot count: " + queryDocumentSnapshots.size()); // ADD THIS





                    if (queryDocumentSnapshots.isEmpty()){
                        nosearchesWarning.setVisibility(View.VISIBLE);
                        recentSearchesRecycler.setVisibility(View.GONE);
                    } else {
                        nosearchesWarning.setVisibility(View.GONE);
                        recentSearchesRecycler.setVisibility(View.VISIBLE);

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                            DiscogsResponse.Result newResult = new DiscogsResponse.Result();

                                if (document.contains("itemId")){
                                    newResult.id = document.getLong("itemId").intValue();
                                }

                                newResult.coverImage = document.getString("imageUrl");

                                newResult.title = document.getString("title");

                                recentList.add(newResult);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("SearchActivity", "Error fetching recent searches", e);
                });
    }

    private void addRecentSearchToDb(DiscogsResponse.Result item){
        user = auth.getCurrentUser();
        if (user == null){
            return;
        }

        String userId = user.getUid();

        String title = item.title;
        String imageUrl = item.coverImage;

        Map<String, Object> recentSearch = new HashMap<>();
        recentSearch.put("itemId", item.id);
        recentSearch.put("title", title);
        recentSearch.put("imageUrl", imageUrl);
        recentSearch.put("timestamp", FieldValue.serverTimestamp());


        db.collection("users")
                .document(userId)
                .collection("recentSearches")
                .document(String.valueOf(item.id))
                .set(recentSearch)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully added recent search to Firestore" + item.title);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding recent search to Firestore", e);
                });
    }


}


