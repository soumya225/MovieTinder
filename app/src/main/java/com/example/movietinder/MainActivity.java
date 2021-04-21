package com.example.movietinder;

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
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

//TODO: add info for users to indicate how to swipe (e.g. click to open website)

public class MainActivity extends AppCompatActivity {

    private Button signOutButton;
    private MovieAdapter movieAdapter;

    private FirebaseAuth firebaseAuth;

    private final String IMdB_BASE_URL = "https://www.imdb.com/title/";

    private ListView listView;
    private List<Movie> cardItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

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

        qs.getMoviesList(15, 3, "Romance", new QueryService.VolleyResponseListener() {
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

        movieAdapter = new MovieAdapter(MainActivity.this, R.layout.item, cardItems);

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
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                makeToast(MainActivity.this, "Left!");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                makeToast(MainActivity.this, "Right!");
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
                //TODO: correct movie is not being retrieved - currentMovie is always first movie

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