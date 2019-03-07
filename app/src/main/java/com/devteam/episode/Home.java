package com.devteam.episode;

public class Home {
    private int episodeSeasonNo, episodeEpisodeNo, airDateInt;
    private String showName, networks, episodeName, backdropPath, airDate;

    public Home(int episodeSeasonNo, int episodeEpisodeNo, int airDateInt, String showName, String networks, String episodeName, String backdropPath, String airDate) {
        this.episodeSeasonNo = episodeSeasonNo;
        this.episodeEpisodeNo = episodeEpisodeNo;
        this.airDateInt = airDateInt;
        this.showName = showName;
        this.networks = networks;
        this.episodeName = episodeName;
        this.backdropPath = backdropPath;
        this.airDate = airDate;
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
}
