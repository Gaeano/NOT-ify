package com.example.spotifyclone.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyclone.Pages.homePage;
import com.example.spotifyclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class openingPage extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openingpage);

        Button signUpBtn = findViewById(R.id.signupbutton);
        Button loginBtn = findViewById(R.id.loginbutton);

        FirebaseUser user = auth.getCurrentUser();

        if (user != null){
            Intent intent = new Intent(openingPage.this, homePage.class);
            startActivity(intent);
            finish();
        }




        signUpBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(openingPage.this, SignUp.class);
                startActivity(intent);

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(openingPage.this, loginAcc.class);
                startActivity(intent);

            }
        });
    }
}
