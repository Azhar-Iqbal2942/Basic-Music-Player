package com.example.player;

public class Song {

    private String title;
    private String artist;
    private String location;
    private long id;

    // now this is a constructor for getting  new values in an object

    public Song(String title, String artist, String location, long id) {
        this.title = title;
        this.artist = artist;
        this.location = location;
        this.id = id;

    }

    // These are getter method ..
    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getLocation() {
        return location;
    }

    public long getId() {
        return id;
    }
}
