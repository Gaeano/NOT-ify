package com.example.spotifyclone.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class displayName extends AppCompatActivity {
    //create logic to input user credentials to database {User UID, displayName, email}
    Button backBtn, createBtn;
    private FirebaseAuth auth;

    EditText displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_name);

        backBtn = findViewById(R.id.backBtn);
        createBtn = findViewById(R.id.createBtn);
        auth = FirebaseAuth.getInstance();
        displayName = findViewById(R.id.displayNameEditText);

        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        backFunction(backBtn);


        String name = displayName.getText().toString();

        createAccFunction(createBtn, email, password);



    }

    public void backFunction(Button backBtn) {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(displayName.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    public void createAccFunction(Button createBtn, String email, String password) {
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(displayName.this, "Account Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(displayName.this, loginAcc.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(displayName.this, "Account Creation Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}