package com.devteam.episode;

public class User {

    private String name;
    private int favourites;

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
