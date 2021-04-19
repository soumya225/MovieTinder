package com.example.movietinder;

public class Movie {
    private String imdb_code;
    private String title;
    private int rating;
    private int runtime;
    private String genres;

    public Movie(String imdb_code, String title, int rating, int runtime, String genres) {
        this.imdb_code = imdb_code;
        this.title = title;
        this.rating = rating;
        this.runtime = runtime;
        this.genres = genres;
    }

    public String getImdb_code() {
        return imdb_code;
    }

    public void setImdb_code(String imdb_code) {
        this.imdb_code = imdb_code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return
                "\n" + title +
                        "\nIMDb ID: " + imdb_code +
                        "\nRating: " + rating +
                        "\nRuntime: " + runtime +
                        "\nGenres: " + genres
                ;
    }
}
