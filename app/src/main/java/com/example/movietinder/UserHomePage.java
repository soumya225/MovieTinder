package com.example.movietinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserHomePage extends AppCompatActivity {

    private Button likesButton, dislikesButton, discoverButton, compareButton;

    DatabaseReference userDB;

    private Button signOutButton;

    static List<String> likes;
    static List<String> dislikes;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserHomePage.this, ActivityForChoosingLoginOrRegistration.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        likes = new ArrayList<>();
        dislikes = new ArrayList<>();

        String email = Utils.encodeString(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(email);
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot likesSnapshot = snapshot.child("Like");
                DataSnapshot dislikesSnapshot = snapshot.child("Dislike");

                if(likesSnapshot.hasChildren()) {
                    Map<String, Object> map = (Map<String, Object>) likesSnapshot.getValue();

                    likes.clear();

                    for(String key: map.keySet()) {
                        if(!likes.contains(key))
                            likes.add(key);
                    }
                }

                if(dislikesSnapshot.hasChildren()) {
                    Map<String, Object> map2 = (Map<String, Object>) dislikesSnapshot.getValue();

                    dislikes.clear();

                    for (String key : map2.keySet()) {
                        if (!dislikes.contains(key))
                            dislikes.add(key);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userDB.child("Change").setValue(true);

        likesButton = findViewById(R.id.likes_button);
        dislikesButton = findViewById(R.id.dislikes_button);
        discoverButton = findViewById(R.id.discover_button);
        compareButton = findViewById(R.id.compare_button);

        likesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLikesActivity = new Intent(UserHomePage.this, LikesActivity.class);
                startActivity(goToLikesActivity);
            }
        });

        dislikesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDislikesActivity = new Intent(UserHomePage.this, DislikesActivity.class);
                startActivity(goToDislikesActivity);
            }
        });

        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToMainActivity = new Intent(UserHomePage.this, MainActivity.class);
                startActivity(goToMainActivity);
            }
        });

        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCompareActivity = new Intent(UserHomePage.this, CompareActivity.class);
                startActivity(goToCompareActivity);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        userDB.child("Change").removeValue();
    }


}