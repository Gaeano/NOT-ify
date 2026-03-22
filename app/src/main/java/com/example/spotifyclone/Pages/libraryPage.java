package com.example.spotifyclone.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.Adapter.LibraryAdapter;
import com.example.spotifyclone.R;
import com.example.spotifyclone.api.DiscogsResponse;
import com.example.spotifyclone.helpers.bottom_navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class libraryPage extends AppCompatActivity implements LibraryAdapter.OnItemClickListenerLib {

    private RecyclerView libaryRecycler;

     private LibraryAdapter adapter;

     private ProgressBar loadingIndicator;

     private TextView noLibraryText;
     private List<DiscogsResponse.Result> resultList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_library_page);

        //set up bottomNav
        BottomNavigationView botNav = findViewById(R.id.bottom_navigation_layout);
        bottom_navigation.setupBottomNav(this, botNav, R.id.nav_library);

        //set up recyclerView and adapter
        libaryRecycler = findViewById(R.id.libraryRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        libaryRecycler.setLayoutManager(layoutManager);
        adapter = new LibraryAdapter(this, resultList);
        libaryRecycler.setAdapter(adapter);

        loadingIndicator = findViewById(R.id.loading_indicator);
        noLibraryText = findViewById(R.id.noLibraryPrompt);

        populateLibrary();

    }

    @Override
    public void onItemClick(int position){
        Intent intent = new Intent(this, albumDetailsPage.class);
        intent.putExtra("albumTitle", resultList.get(position).title);
        intent.putExtra("masterId", resultList.get(position).id);
        intent.putExtra("imageUrl", resultList.get(position).coverImage);
        startActivity(intent);
    }


    private void populateLibrary(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            db.collection("users")
                    .document(userId)
                    .collection("savedPlaylists")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        loadingIndicator.setVisibility(View.GONE);
                        libaryRecycler.setVisibility(View.VISIBLE);

                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                DiscogsResponse.Result result = new DiscogsResponse.Result();

                                if (document.contains("id")) {
                                    result.id = document.getLong("id").intValue();
                                }

                                result.coverImage = document.getString("imageUrl");
                                result.title = document.getString("title");

                                Log.d("LibraryPage", "Result: " + result.title);
                                resultList.add(result);

                            }

                            adapter.notifyDataSetChanged();

                        }
                    })
                    .addOnFailureListener(e ->{
                        loadingIndicator.setVisibility(View.GONE);
                        noLibraryText.setVisibility(View.VISIBLE);
                        libaryRecycler.setVisibility(View.GONE);

                        Log.e("LibraryPage", "Error getting documents: ", e);
                    });
        }
    }
}