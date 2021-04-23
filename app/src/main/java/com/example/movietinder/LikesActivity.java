package com.example.movietinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LikesActivity extends AppCompatActivity {

    private ListView likesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        likesListView = findViewById(R.id.likesListView);

        ArrayAdapter<String> likesAdapter = new ArrayAdapter<>(this, R.layout.movie_item_view, R.id.movie_text_view, UserHomePage.likes);
        likesListView.setAdapter(likesAdapter);
    }
}