package com.adnanhakim.episode;

public class Cast {

    private String name, character, imageURL;

    public Cast(String name, String character, String imageURL) {
        this.name = name;
        this.character = character;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public String getCharacter() {
        return character;
    }

    public String getImageURL() {
        return imageURL;
    }
}
