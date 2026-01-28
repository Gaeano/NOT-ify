package com.example.spotifyclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUp extends AppCompatActivity {
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount);
        Auth = FirebaseAuth.getInstance();
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button createBtn = findViewById(R.id.createbtn);



        createBtn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(SignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }

                Auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(SignUp.this, HomePage.class));
                                } else {
                                    Toast.makeText(SignUp.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                                }
                        });


                }
        });
    }
}
