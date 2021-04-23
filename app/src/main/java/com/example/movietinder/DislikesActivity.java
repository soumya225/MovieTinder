package com.example.movietinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DislikesActivity extends AppCompatActivity {

    ListView dislikesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dislikes);

        dislikesListView = findViewById(R.id.dislikesListView);

        ArrayAdapter<String> dislikesAdapter = new ArrayAdapter<>(this, R.layout.movie_item_view, R.id.movie_text_view, UserHomePage.dislikes);
        dislikesListView.setAdapter(dislikesAdapter);
    }
}