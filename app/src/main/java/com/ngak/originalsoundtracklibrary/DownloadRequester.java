package com.ngak.originalsoundtracklibrary;

import android.graphics.Bitmap;

import java.io.Serializable;

public interface DownloadRequester extends Serializable {
    void onAsyncRequestEnd(Album album, Bitmap bitmap);
}
