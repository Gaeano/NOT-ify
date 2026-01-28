package com.example.spotifyclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<imageItem> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        imageList = new ArrayList<>();
        populateImageList();


        adapter = new ImageAdapter(this, imageList, new ImageAdapter.onitemClickListener() {
            @Override
            public void onItemClick(imageItem item) {
                Toast.makeText(HomePage.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
        Button logoutBtn = findViewById(R.id.logout);

        logoutBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, logout.class);
                startActivity(intent);
            }
        });
    }
    private void populateImageList() {
        imageList.add(new imageItem(R.drawable.image1, "Mountain View", "Beautiful mountain landscape"));
        imageList.add(new imageItem(R.drawable.image2, "Ocean Sunset", "Stunning sunset over the ocean"));
        imageList.add(new imageItem(R.drawable.image3, "City Lights", "Night view of the city"));
        imageList.add(new imageItem(R.drawable.image4, "Forest Path", "Peaceful walk through the forest"));
        imageList.add(new imageItem(R.drawable.image5, "Desert Dunes", "Golden sand dunes at sunrise"));
    }

}
