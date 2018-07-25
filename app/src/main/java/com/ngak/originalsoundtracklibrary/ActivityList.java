package com.ngak.originalsoundtracklibrary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityList extends AppCompatActivity implements TrackListHolder {

    ListView trackList;
    ArrayList<Soundtrack> tracks;
    DataBaseAdapter db;
    SoundtrackAdapter adapter;
    Uri uriDownloadRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();

        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        trackList = (ListView)findViewById(R.id.list_listView);
        db = new DataBaseAdapter(this);
        db.open();
        tracks = db.getTrackList(bundle.getInt("listid"));

        adapter = new SoundtrackAdapter(tracks, this, this);
        trackList.setAdapter(adapter);

        db.close();
    }


    @Override
    public void onTrackFavoriteChanged(int trackpos, boolean favorited) {
        db.open();
        if(favorited){
            db.addTrackToList(tracks.get(trackpos).getId(), 1);
            Toast.makeText(this, "Track added to favorites", Toast.LENGTH_SHORT).show();
        }else {
            db.removeTrackFromList(tracks.get(trackpos).getId(), 1);
            Toast.makeText(this, "Track removed from favorites", Toast.LENGTH_SHORT).show();
        }
        db.close();
        tracks.remove(trackpos);
        adapter = new SoundtrackAdapter(tracks, this, this);
        trackList.setAdapter(adapter);
    }

    @Override
    public void requestDownload(Uri uri) {
        if(this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            uriDownloadRequest = uri;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else{
            Downloader.downloadFile(uri, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestDownload(uriDownloadRequest);
                }
                break;
            }

        }
    }
}
