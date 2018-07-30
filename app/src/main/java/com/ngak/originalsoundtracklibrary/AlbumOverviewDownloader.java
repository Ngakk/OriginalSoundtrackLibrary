package com.ngak.originalsoundtracklibrary;

import java.util.ArrayList;

public interface AlbumOverviewDownloader {
    void onAsyncOverviewEnd(Album album);
    void onAsyncTracksEnd(ArrayList<Soundtrack> soundtracks);
}
