package com.devteam.episode;

public class ProfileDp {
    private String imageUrl;

    public ProfileDp() {
        // Default Constructor
    }

    public ProfileDp(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
