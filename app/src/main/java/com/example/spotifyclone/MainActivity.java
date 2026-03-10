package com.example.spotifyclone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyclone.Authentication.signuplogin;
import com.example.spotifyclone.Pages.homePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null){
            Intent intent = new Intent(MainActivity.this, homePage.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, signuplogin.class);
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

        finish();

    }
}