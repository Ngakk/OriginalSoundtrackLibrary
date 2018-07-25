package com.ngak.originalsoundtracklibrary;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static android.content.Context.DOWNLOAD_SERVICE;

public class Downloader {

    static DownloadManager downloadManager;

    public Downloader() {
    }

    /*@Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }*/

    public static long downloadFile(Uri uri, Context context){
        long downloadReference;

        // Create request for android download manager
        downloadManager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Data Download");

        //Setting description of request
        request.setDescription( Environment.DIRECTORY_DOWNLOADS + "AndroidTutorialPoint.mp3");

        //Set the local destination for the downloaded file to a path
        //within the application's external files directory

        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,"AndroidTutorialPoint.mp3");

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        /*Button DownloadStatus = (Button) findViewById(R.id.DownloadStatus);
        DownloadStatus.setEnabled(true);
        Button CancelDownload = (Button) findViewById(R.id.CancelDownload);
        CancelDownload.setEnabled(true);*/

        return downloadReference;
    }


    public static class AsyncLoadAlbumCover  extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "AsyncTaskLoadImage";
        private DownloadRequester adapter;
        private Album album;
        public AsyncLoadAlbumCover(Album album, DownloadRequester adapter) {
            this.adapter = adapter;
            this.album = album;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            album.imageDownloaded = true;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            adapter.onAsyncRequestEnd(album, bitmap);
        }
    }
}
