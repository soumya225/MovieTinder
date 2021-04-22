package com.example.movietinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText email;
    private EditText password;
    private EditText username;

    DatabaseReference currentUserDB;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) { //user is logged in if true
                    Intent intent = new Intent(RegistrationActivity.this, UserHomePage.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        registerButton = findViewById(R.id.click_to_register_button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        username = findViewById(R.id.username);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailInput = email.getText().toString();
                final String passwordInput = password.getText().toString();
                final String usernameInput = username.getText().toString();

                if(TextUtils.isEmpty(usernameInput)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter a username", Toast.LENGTH_LONG).show();
                    return;
                } else if(TextUtils.isEmpty(emailInput)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter an email address", Toast.LENGTH_LONG).show();
                    return;
                }  else if(TextUtils.isEmpty(passwordInput)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter a password", Toast.LENGTH_LONG).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Error registering", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            currentUserDB.child("Like").setValue(true);
                            currentUserDB.child("Dislike").setValue(true);
                            currentUserDB.child("Logged in").setValue(true);

                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthStateListener);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}