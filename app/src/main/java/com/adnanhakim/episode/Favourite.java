package com.adnanhakim.episode;

public class Favourite {

    private int id;
    private String title, posterURL, backdropURL;

    public Favourite() {
    }

    public Favourite(int id, String title, String posterURL, String backdropURL) {
        this.id = id;
        this.title = title;
        this.posterURL = posterURL;
        this.backdropURL = backdropURL;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getBackdropURL() {
        return backdropURL;
    }
}
