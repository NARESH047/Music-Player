package com.example.musicplayer;

import java.io.Serializable;

public class Audio implements Serializable {

    String path;
    String name;
    String album;
    String artist;
    int Id;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setId(int Id){this.Id = Id;}

    public int getId() {
        return Id;
    }

}
