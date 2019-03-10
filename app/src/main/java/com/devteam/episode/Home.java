package com.devteam.episode;

public class Home {
    private int episodeSeasonNo, episodeEpisodeNo, airDateInt, showId;
    private String showName, networks, episodeName, backdropPath, airDate, status;

    public Home(int episodeSeasonNo, int episodeEpisodeNo, int airDateInt, int showId, String showName, String networks, String episodeName, String backdropPath, String airDate, String status) {
        this.episodeSeasonNo = episodeSeasonNo;
        this.episodeEpisodeNo = episodeEpisodeNo;
        this.airDateInt = airDateInt;
        this.showId = showId;
        this.showName = showName;
        this.networks = networks;
        this.episodeName = episodeName;
        this.backdropPath = backdropPath;
        this.airDate = airDate;
        this.status = status;
    }

    public int getEpisodeSeasonNo() {
        return episodeSeasonNo;
    }

    public int getEpisodeEpisodeNo() {
        return episodeEpisodeNo;
    }

    public int getAirDateInt() {
        return airDateInt;
    }

    public int getShowId() {
        return showId;
    }

    public String getShowName() {
        return showName;
    }

    public String getNetworks() {
        return networks;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getAirDate() {
        return airDate;
    }

    public String getStatus() {
        return status;
    }
}
