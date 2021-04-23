package com.example.movietinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompareActivity extends AppCompatActivity {

    private Button searchBarButton;
    private EditText searchBar;
    private ListView searchResultsListView;

    private DatabaseReference usersDB;

    private String friendEmail;

    private ArrayAdapter<String> resultsAdapter;

    private List<String> matchesList;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        matchesList = new ArrayList<>();

        uid = FirebaseAuth.getInstance().getUid();

        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");
        usersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersDB.child(uid + "is searching").removeValue();

                if(friendEmail == null) {
                    matchesList.clear();
                    resultsAdapter.notifyDataSetChanged();
                    return;
                }

                if(!snapshot.hasChild(friendEmail)) {
                    matchesList.clear();
                    resultsAdapter.notifyDataSetChanged();
                    Toast.makeText(CompareActivity.this, "A user with that email does not exist", Toast.LENGTH_LONG).show();
                    return;
                }

                DataSnapshot friendLikesSnapshot = snapshot.child(friendEmail).child("Like");
                if(friendLikesSnapshot.hasChildren()) {
                    Map<String, Object> map = (Map<String, Object>) friendLikesSnapshot.getValue();

                    ArrayList<String> friendLikes = new ArrayList<>();

                    for(String key: map.keySet()) {
                            friendLikes.add(key);
                    }

                    compareLikes(friendLikes);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchBarButton = findViewById(R.id.search_bar_button);
        searchBar = findViewById(R.id.friend_email_search_bar);
        searchResultsListView = findViewById(R.id.matchesListView);

        resultsAdapter = new ArrayAdapter<>(this, R.layout.movie_item_view, R.id.movie_text_view, matchesList);
        searchResultsListView.setAdapter(resultsAdapter);

        searchBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendEmail = Utils.encodeString(searchBar.getText().toString());

                usersDB.child(uid + "is searching").setValue(true);
            }


        });

    }

    public void compareLikes(List<String> friendLikes) {
        matchesList.clear();

        for(int i = 0; i < friendLikes.size(); i++) {
            if(UserHomePage.likes.contains(friendLikes.get(i))) {
                matchesList.add(friendLikes.get(i));
            }
        }

        if(matchesList.isEmpty()) {
            Toast.makeText(this, "You have no matches with this user", Toast.LENGTH_LONG).show();
        }

        resultsAdapter.notifyDataSetChanged();
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