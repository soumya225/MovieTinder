package com.example.movietinder;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QueryService {

    /*
    //for queries
    private int limit; //between 1-50 inclusive
    private String quality; //values can be 720p, 1080p, 2160p, 3D
    private int min_rating; //between 0-9 inclusive
    private String genre; */

    //TODO: use medium_cover_image in JSON results to add image cover

    public static final String QUERY_BASE = "https://yts.mx/api/v2/list_movies.json";
    Context context;

    public QueryService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(List<Movie> movieList);
    }

    public void getMoviesList(int limit, int min_rating, String genre, VolleyResponseListener responseListener) {
        String url = buildURL(limit, min_rating, genre);

        List<Movie> moviesList = new ArrayList<>();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray movies = data.getJSONArray("movies");

                    for(int i = 0; i <  movies.length(); i++) {
                        JSONObject movie = (JSONObject)movies.get(i);

                        String imdb_code = movie.optString("imdb_code", "");
                        String title = movie.optString("title", "");
                        int rating = movie.optInt("rating",0);
                        int runtime = movie.optInt("runtime", 0);
                        JSONArray genres = movie.getJSONArray("genres");
                        String genre = "";
                        for(int j = 0; j < genres.length(); j++) {
                            genre += genres.getString(j);
                            if(j != genres.length() - 1) genre += ", ";
                        }

                        Movie mv = new Movie(imdb_code, title, rating, runtime, genre);

                        moviesList.add(mv);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                responseListener.onResponse(moviesList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onError("Error getting movie data" + error);
            }
        });

        VolleyRequestQueue.getInstance(context).addToRequestQueue(request);

    }

    public String buildURL(int limit, int min_rating, String genre) {
        Uri baseUrl = Uri.parse(QUERY_BASE);
        Uri.Builder uriBuilder = baseUrl.buildUpon();

        uriBuilder.appendQueryParameter("limit", limit + "");
        uriBuilder.appendQueryParameter("min_rating", min_rating + "");
        uriBuilder.appendQueryParameter("genre", genre);

        return uriBuilder.toString();
    }

}
