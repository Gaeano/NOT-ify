package com.example.spotifyclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginAcc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        EditText emailEditText = findViewById(R.id.enteremail);
        EditText passwordEditText = findViewById(R.id.enterpassword);
        Button loginBtn = findViewById(R.id.loginbutton);
        FirebaseAuth auth = FirebaseAuth.getInstance();

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
