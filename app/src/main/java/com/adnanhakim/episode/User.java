package com.adnanhakim.episode;

public class User {

    String name;
    int favourites;

    public User() {

    }

    public User(String name, int favourites) {
        this.name = name;
        this.favourites = favourites;
    }

    public String getName() {
        return name;
    }

    public int getFavourites() {
        return favourites;
    }
}
