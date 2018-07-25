package com.ngak.originalsoundtracklibrary;

import android.net.Uri;

public interface TrackListHolder {
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 933;
    void onTrackFavoriteChanged(int trackpos, boolean favorited);

    void requestDownload(Uri uri);
}
