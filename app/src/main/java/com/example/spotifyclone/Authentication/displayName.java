package com.example.spotifyclone.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class displayName extends AppCompatActivity {
    //create logic to input user credentials to database {User UID, displayName, email}
    Button backBtn, createBtn;
     private FirebaseAuth auth;
    private FirebaseFirestore db;
    EditText displayNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_name);

        backBtn = findViewById(R.id.backBtn);
        createBtn = findViewById(R.id.createBtn);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        displayNameInput = findViewById(R.id.displayNameEditText);

        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        backFunction(backBtn);



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



                ProgressBar loadingIndicator = findViewById(R.id.loadingIndicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                createBtn.setEnabled(false);

                String name = displayNameInput.getText().toString();

                if (name.isEmpty()){
                    Toast.makeText(displayName.this, "Please enter a display name", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();

                            if (user != null) {
                                String userId = user.getUid();

                                //add display name to the Firebase Auth
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                user.updateProfile(profileUpdates);

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("id", userId);
                                userData.put("email", email);
                                userData.put("displayName", name);


                                //Add to database
                                db.collection("users").document(userId)
                                        .set(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(displayName.this, "Account Created", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(displayName.this, loginAcc.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(displayName.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                                if (loadingIndicator != null) {
                                                    loadingIndicator.setVisibility(View.GONE);
                                                }
                                                createBtn.setEnabled(true);
                                            }
                                        });
                            }
                        } else {
                            loadingIndicator.setVisibility(View.GONE);
                            createBtn.setEnabled(true);
                            String actualError = task.getException() != null ? task.getException().getMessage() : "Unknown Error";
                            Toast.makeText(displayName.this, "Auth Error: " + actualError, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}