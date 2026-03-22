package com.example.spotifyclone.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class userProfilePage extends AppCompatActivity implements LibraryAdapter.OnItemClickListenerLib {

    private Button backBtn;
    private ImageButton settingsBtn;

    private TextView displayName, noLibraryText;

    private RecyclerView libraryRecycler;
    private LibraryAdapter adapter;
    private ProgressBar loadingIndicator;

    private List<DiscogsResponse.Result> resultList = new ArrayList<>();

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_library_page);

        //set up navBar
        BottomNavigationView botNav = findViewById(R.id.bottom_navigation_layout);
        bottom_navigation.setupBottomNav(this, botNav, R.id.nav_library);

        //set up recycler and adapter
        libraryRecycler = findViewById(R.id.library_recycler_view);
        adapter = new LibraryAdapter(this, resultList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        libraryRecycler.setLayoutManager(layoutManager);
        libraryRecycler.setAdapter(adapter);

        displayName= findViewById(R.id.display_name);
        noLibraryText = findViewById(R.id.no_library_prompt);
        loadingIndicator = findViewById(R.id.loading_indicator);

        //set up buttons
        backBtn = findViewById(R.id.back_btn);
        settingsBtn = findViewById(R.id.settings_btn);


        displayName.setText(user.getDisplayName());
        populateLibrary();


        backBtn.setOnClickListener(v -> finish());


        settingsBtn.setOnClickListener(v ->{
            Intent intent = new Intent(this, settingsPage.class);
            startActivity(intent);
        });

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, albumDetailsPage.class);
        intent.putExtra("albumTitle", resultList.get(position).title);
        intent.putExtra("masterId", resultList.get(position).id);
        intent.putExtra("imageUrl", resultList.get(position).coverImage);
        startActivity(intent);
    }



    private void populateLibrary(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        if (user != null) {
            String userId = user.getUid();

            db.collection("users")
                    .document(userId)
                    .collection("savedPlaylists")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        loadingIndicator.setVisibility(View.GONE);
                        libraryRecycler.setVisibility(View.VISIBLE);

                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                DiscogsResponse.Result result = new DiscogsResponse.Result();

                                if (document.contains("id")) {
                                    result.id = document.getLong("id").intValue();
                                }

                                result.coverImage = document.getString("imageUrl");
                                result.title = document.getString("title");

                                Log.d("userProfilePage", "Result: " + result.title);
                                resultList.add(result);

                            }

                            adapter.notifyDataSetChanged();

                        }
                    })
                    .addOnFailureListener(e ->{
                        loadingIndicator.setVisibility(View.GONE);
                        noLibraryText.setVisibility(View.VISIBLE);
                        libraryRecycler.setVisibility(View.GONE);

                        Log.e("userProfilePage", "Error getting documents: ", e);
                    });
        }
    }

}