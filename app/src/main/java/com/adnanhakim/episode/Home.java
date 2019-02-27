package com.adnanhakim.episode;

public class Home {
    private int seasonNo, episodeNo;
    private String showName, networks, episodeName, backdropPath;

    public Home(int seasonNo, int episodeNo, String showName, String networks, String episodeName, String backdropPath) {
        this.seasonNo = seasonNo;
        this.episodeNo = episodeNo;
        this.showName = showName;
        this.networks = networks;
        this.episodeName = episodeName;
        this.backdropPath = backdropPath;
    }

    public int getSeasonNo() {
        return seasonNo;
    }

    public int getEpisodeNo() {
        return episodeNo;
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
}
