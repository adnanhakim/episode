package com.adnanhakim.episode;

public class Favourite {

    private int id;
    private String title, imageURL;

    public Favourite(int id, String title, String imageURL) {
        this.id = id;
        this.title = title;
        this.imageURL = imageURL;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageURL() {
        return imageURL;
    }

}
