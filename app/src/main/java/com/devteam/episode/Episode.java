package com.devteam.episode;

public class Episode {

    private int episodeNo, seasonNo;
    private double rating;
    private String date, title, overview, imageURL;

    public Episode(int episodeNo, int seasonNo, String date, String title, String overview, double rating, String imageURL) {
        this.episodeNo = episodeNo;
        this.seasonNo = seasonNo;
        this.date = date;
        this.title = title;
        this.overview = overview;
        this.rating = rating;
        this.imageURL = imageURL;
    }

    public int getEpisodeNo() {
        return episodeNo;
    }

    public int getSeasonNo() {
        return seasonNo;
    }

    public double getRating() {
        return rating;
    }

    public String getDate() {
        return date;
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
