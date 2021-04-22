package com.example.movietinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: add info for users to indicate how to swipe (e.g. click to open website)
//TODO: add loading bar

public class MainActivity extends AppCompatActivity {

    private Button signOutButton;
    private MovieAdapter movieAdapter;

    private FirebaseAuth firebaseAuth;

    private final String IMdB_BASE_URL = "https://www.imdb.com/title/";

    private DatabaseReference usersDB;

    private List<Movie> cardItems;


    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();

        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");

        cardItems = new ArrayList<>();


        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, ActivityForChoosingLoginOrRegistration.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        QueryService qs = new QueryService(this);

        qs.getMoviesList(15, 5, "Action", new QueryService.VolleyResponseListener() {
            @Override
            public void onError(String message) {
                makeToast(MainActivity.this, message);
            }

            @Override
            public void onResponse(List<Movie> movieList) {

                for(int i = 0; i < movieList.size(); i++) {
                    cardItems.add(movieList.get(i));
                }

                movieAdapter.notifyDataSetChanged();

            }
        });

        movieAdapter = new MovieAdapter(MainActivity.this, R.layout.card_item_view, cardItems);

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);

        flingContainer.setAdapter(movieAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                cardItems.remove(0);
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Movie currentMovie = (Movie)dataObject;

                usersDB.child(uid).child("Dislike").child(currentMovie.getTitle()).setValue(true);
                usersDB.child(uid).child("Like").child(currentMovie.getTitle()).removeValue();

                makeToast(MainActivity.this, "Rejected " + currentMovie.getTitle());
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Movie currentMovie = (Movie)dataObject;

                usersDB.child(uid).child("Like").child(currentMovie.getTitle()).setValue(true);
                usersDB.child(uid).child("Dislike").child(currentMovie.getTitle()).removeValue();

                makeToast(MainActivity.this, "Added " + currentMovie.getTitle() + " to Matches");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Movie currentMovie = movieAdapter.getItem(itemPosition);
                String IMdBCode = currentMovie.getImdb_code();
                String urlToOpen = IMdB_BASE_URL + IMdBCode;

                openIMdBLink(urlToOpen);

                makeToast(MainActivity.this, "Opening IMdB web page for " + currentMovie.getTitle());
            }
        });

        /*
        DatabaseReference likesDB = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Like");

        likesDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.equals(true) || snapshot==null) return;

                Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                for(String key: map.keySet()) {
                    if(!ActivityForChoosingLoginOrRegistration.likes.contains(key))
                        ActivityForChoosingLoginOrRegistration.likes.add(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference dislikesDB = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Dislike");

        dislikesDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.equals(true) || snapshot==null) return;

                Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                for(String key: map.keySet()) {
                    if(!ActivityForChoosingLoginOrRegistration.dislikes.contains(key))
                        ActivityForChoosingLoginOrRegistration.dislikes.add(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }); */
    }



    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    void openIMdBLink(String url) {
        // Convert the String URL into a URI object (to pass into the Intent constructor)
        Uri movieUri = Uri.parse(url);

        // Create a new intent to view the movie URI
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, movieUri);

        // Send the intent to launch a new activity
        startActivity(websiteIntent);
    }
}