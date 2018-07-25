package com.ngak.originalsoundtracklibrary;

import android.graphics.Bitmap;
import android.net.Uri;

public class Album {
    int id;
    String name;
    String composer;
    String date;
    String source;
    Bitmap cover;
    String coverurl;
    boolean imageDownloaded = false;

    public Album(int id, String name, String composer, String date, String source, Bitmap cover, String coverurl) {
        this.id = id;
        this.name = name;
        this.composer = composer;
        this.date = date;
        this.source = source;
        this.cover = cover;
        this.coverurl = coverurl;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public int getId(){ return id; }

    public String getName() {
        return name;
    }

    public String getComposer() {
        return composer;
    }

    public String getDate() {
        return date;
    }

    public String getSource() {
        return source;
    }

    public Bitmap getCover() {
        return cover;
    }

    public String getCoverurl() {
        return coverurl;
    }
}
