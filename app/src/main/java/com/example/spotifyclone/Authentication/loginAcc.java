package com.example.spotifyclone.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyclone.Pages.HomePage;
import com.example.spotifyclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginAcc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        EditText emailEditText = findViewById(R.id.enterEmail);
        EditText passwordEditText = findViewById(R.id.enterPassword);
        Button loginBtn = findViewById(R.id.loginbutton);
        Button backBtn = findViewById(R.id.backBtn);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginAcc.this, signuplogin.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                auth.signInWithEmailAndPassword(email, password);
                FirebaseUser user = auth.getCurrentUser();

                if(user != null){
                    Intent intent = new Intent(loginAcc.this, HomePage.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent (loginAcc.this, signuplogin.class);
                }


            }
        });
    }

}
