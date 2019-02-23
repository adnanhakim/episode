package com.adnanhakim.episode;

public class TVSeries {

    private int id;
    private String title, overview, imageURL;
    private boolean favourite;

    public TVSeries(int id, String title, String overview, String imageURL) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.imageURL = imageURL;
        favourite = false;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getImageURL() {
        return imageURL;
    }
}
