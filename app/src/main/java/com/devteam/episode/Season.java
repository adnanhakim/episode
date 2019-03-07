package com.devteam.episode;

public class Season {

    private int tvId, seasonId, seasonEpisodes, seasonNo;
    private String seasonTitle, seasonDate, seasonImageURL;

    public Season(int tvId, int seasonId, int seasonEpisodes, String seasonTitle, String seasonDate, String seasonImageURL, int seasonNo) {
        this.tvId = tvId;
        this.seasonId = seasonId;
        this.seasonEpisodes = seasonEpisodes;
        this.seasonTitle = seasonTitle;
        this.seasonDate = seasonDate;
        this.seasonImageURL = seasonImageURL;
        this.seasonNo = seasonNo;
    }

    public int getTvId() {
        return tvId;
    }

    public int getSeasonNo() {
        return seasonNo;
    }

    public int getSeasonEpisodes() {
        return seasonEpisodes;
    }

    public String getSeasonTitle() {
        return seasonTitle;
    }

    public String getSeasonDate() {
        return seasonDate;
    }

    public String getSeasonImageURL() {
        return seasonImageURL;
    }
}
