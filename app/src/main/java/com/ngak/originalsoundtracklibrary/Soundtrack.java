package com.ngak.originalsoundtracklibrary;

import android.net.Uri;

import java.util.ArrayList;

public class Soundtrack {
    int id;
    String name;
    String album;
    ArrayList<String> artists;
    String fileurl;
    boolean favorited;

    public Soundtrack(int id, String name, String album, ArrayList<String> artists, String fileurl, boolean isFav) {
        this.id = id;
        this.name = name;
        this.album = album;
        this.artists = artists;
        this.fileurl = fileurl;
        this.favorited = isFav;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlbum() {
        return album;
    }

    public ArrayList<String> getArtists() {
        return artists;
    }

    public String getFileurl() {
        return fileurl;
    }
}
