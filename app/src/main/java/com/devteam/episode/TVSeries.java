package com.devteam.episode;

public class TVSeries {

    private int id;
    private String title, overview, imageURL;

    public TVSeries(int id, String title, String overview, String imageURL) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.imageURL = imageURL;
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
