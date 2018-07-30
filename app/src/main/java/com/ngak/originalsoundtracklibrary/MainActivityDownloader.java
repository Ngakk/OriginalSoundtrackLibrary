package com.ngak.originalsoundtracklibrary;

import java.util.ArrayList;

public interface MainActivityDownloader {
    void onAsyncNewestEnd(ArrayList<Album> albums);
    void onAsyncBestEnd(ArrayList<Album> albums);
    void onAsyncRandomEnd(ArrayList<Album> albums);
}
