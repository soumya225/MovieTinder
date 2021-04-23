package com.example.movietinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {
    Context context;

    public MovieAdapter(Context context, int resourceID, List<Movie> items) {
        super(context, resourceID, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie currentMovie = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_item_view, parent, false);
        }

        TextView title = convertView.findViewById(R.id.movie_info);
        ImageView poster = convertView.findViewById(R.id.movie_poster);

        title.setText(currentMovie.toString());
        poster.setImageResource(R.drawable.ic_launcher_foreground);

        String url = currentMovie.getPosterURL();
        if (url != null) {
            Picasso.get().load(url).into(poster);
        }

        return convertView;
    }


}
