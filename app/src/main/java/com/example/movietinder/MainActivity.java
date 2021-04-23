package com.example.movietinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//TODO: add info for users to indicate how to swipe (e.g. click to open website)
//TODO: add loading bar
//TODO: add settings so that users can discover movies
//TODO: prevent discovered movies from being discovered again unless all movies from api are done

public class MainActivity extends AppCompatActivity {


    private MovieAdapter movieAdapter;

    private FirebaseAuth firebaseAuth;

    private final String IMdB_BASE_URL = "https://www.imdb.com/title/";

    private DatabaseReference usersDB;

    private List<Movie> cardItems;

    private int limit = 10;
    private int page = 1;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        email = Utils.encodeString(firebaseAuth.getCurrentUser().getEmail());

        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");

        cardItems = new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minRating = sharedPreferences.getString(getString(R.string.minimum_rating), getString(R.string.minimum_rating_default));
        String genre = sharedPreferences.getString(getString(R.string.genre), getString(R.string.genre_default));

        QueryService qs = new QueryService(this);

        qs.getMoviesList(limit, Integer.parseInt(minRating), genre, page, new QueryService.VolleyResponseListener() {
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

                String safeCurrentMovieTitle = Utils.encodeString(currentMovie.getTitle());

                usersDB.child(email).child("Dislike").child(safeCurrentMovieTitle).setValue(true);
                usersDB.child(email).child("Like").child(safeCurrentMovieTitle).removeValue();

                makeToast(MainActivity.this, "Added " + currentMovie.getTitle() + " to Dislikes");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Movie currentMovie = (Movie)dataObject;

                String safeCurrentMovieTitle = Utils.encodeString(currentMovie.getTitle());

                usersDB.child(email).child("Like").child(safeCurrentMovieTitle).setValue(true);
                usersDB.child(email).child("Dislike").child(safeCurrentMovieTitle).removeValue();

                makeToast(MainActivity.this, "Added " + currentMovie.getTitle() + " to Likes");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                qs.getMoviesList(limit, Integer.parseInt(minRating), genre, ++page, new QueryService.VolleyResponseListener() {
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