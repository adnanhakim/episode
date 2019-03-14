package com.devteam.episode;

public class User {

    private String name, imageURL;
    private boolean nightMode;

    public User() {
    }

    public User(String name, String imageURL, boolean nightMode) {
        this.name = name;
        this.imageURL = imageURL;
        this.nightMode = nightMode;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public boolean isNightMode() {
        return nightMode;
    }
}
