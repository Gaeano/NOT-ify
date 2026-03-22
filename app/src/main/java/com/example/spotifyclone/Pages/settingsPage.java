package com.example.spotifyclone.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyclone.Authentication.openingPage;
import com.example.spotifyclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class settingsPage extends AppCompatActivity {
    private Button logoutBtn, backBtn;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private TextView displayName;
    private RelativeLayout userProfileBtn;

    private FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_page);

        logoutBtn = findViewById(R.id.logout);
        displayName = findViewById(R.id.profile_name);
        userProfileBtn = findViewById(R.id.profile_section_btn);
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(v -> finish());


        displayName.setText(user.getDisplayName());

        userProfileBtn.setOnClickListener(v ->{
            Intent intent = new Intent(settingsPage.this, userProfilePage.class);
            startActivity(intent);
        });




        logout(logoutBtn);

    }

    public void logout (Button logoutBtn){
        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                auth.signOut();
                Log.d("SettingsPage", "User logged out");
                Intent intent = new Intent(settingsPage.this, openingPage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}