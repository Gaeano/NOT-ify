package com.example.spotifyclone.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyclone.R;

public class signuplogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openingpage);

        Button signUpBtn = findViewById(R.id.signupbutton);
        Button loginBtn = findViewById(R.id.loginbutton);
        signUpBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signuplogin.this, SignUp.class);
                startActivity(intent);

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signuplogin.this, loginAcc.class);
                startActivity(intent);

            }
        });
    }
}
