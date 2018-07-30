package com.ngak.originalsoundtracklibrary;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumOverview extends Fragment implements DownloadRequester, AlbumOverviewDownloader, TrackListHolder{

    DataBaseAdapter db;

    int id;
    TextView title;
    TextView composer;
    ImageView cover;
    Button button;
    boolean isFavorited = true;

    ListView listView;
    SoundtrackAdapter adapter;
    ArrayList<Soundtrack> tracks;

    Uri uriDownloadRequest;

    public AlbumOverview() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        id = bundle.getInt("id");
        return inflater.inflate(R.layout.fragment_album_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = (TextView)view.findViewById(R.id.album_title);
        composer = (TextView)view.findViewById(R.id.album_composer);
        cover = (ImageView)view.findViewById(R.id.album_cover);
        button = (Button)view.findViewById(R.id.favorite_album);
        listView = (ListView)view.findViewById(R.id.list_listView);

        db = new DataBaseAdapter(getActivity());
        db.open();

        if(db.isAlbumOnLocal(id)) {
            Album album = db.getAlbum(id);

            tracks = db.getAlbumTracks(id);
            isFavorited = true;
            for (int i = 0; i < tracks.size(); i++) {
                if (!tracks.get(i).favorited)
                    isFavorited = false;
            }

            if (!album.imageDownloaded && !album.getCoverurl().equals(""))
                new Downloader.AsyncLoadAlbumCover(album, this).execute(album.getCoverurl());

            title.setText(album.getName());
            composer.setText(album.getComposer());
            cover.setImageBitmap(album.getCover());

            setButtonFavText();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddToFavorites();
                }
            });

            adapter = new SoundtrackAdapter(tracks, getContext(), this);
            listView.setAdapter(adapter);
        }
        else {
            new Downloader.AsyncAlbum(id, this).execute();
            new Downloader.AsyncAlbumTracklist(id, this).execute();
        }
        db.close();
    }

    public void AddToFavorites(){
        db.open();
        if(!isFavorited) {
            db.addAlbumToList(id, 1);
            for(int i = 0; i < tracks.size(); i++)
                tracks.get(i).favorited = true;
        }else {
            db.removeAlbumFromList(id, 1);
            for(int i = 0; i < tracks.size(); i++)
                tracks.get(i).favorited = false;
        }
        db.close();
        isFavorited = !isFavorited;
        setButtonFavText();
        adapter.notifyDataSetChanged();
    }

    public void setButtonFavText(){
        if(isFavorited){
            button.setText(R.string.unfav_button);
        }
        else{
            button.setText(R.string.fav_button);
        }
    }

    @Override
    public void onAsyncRequestEnd(Album album, Bitmap bitmap) {
        album.setCover(bitmap);
        cover.setImageBitmap(album.getCover());
    }

    @Override
    public void onTrackFavoriteChanged(int trackpos, boolean favorited) {
        db.open();
        if(favorited){
            db.addTrackToList(tracks.get(trackpos).getId(), 1);
            Toast.makeText(getContext(), "Track added to favorites", Toast.LENGTH_SHORT).show();
        }else {
            db.removeTrackFromList(tracks.get(trackpos).getId(), 1);
            Toast.makeText(getContext(), "Track removed from favorites", Toast.LENGTH_SHORT).show();
        }
        db.close();

    }

    @Override
    public void requestDownload(Uri uri) {
        if(getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            uriDownloadRequest = uri;
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else{
            Downloader.downloadFile(uri, getContext());
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

    @Override
    public void onAsyncOverviewEnd(Album album) {

        if (!album.imageDownloaded && !album.getCoverurl().equals(""))
            new Downloader.AsyncLoadAlbumCover(album, this).execute(album.getCoverurl());

        title.setText(album.getName());
        composer.setText(album.getComposer());
        cover.setImageBitmap(album.getCover());

        setButtonFavText();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddToFavorites();
            }
        });

        db.close();

    }

    @Override
    public void onAsyncTracksEnd(ArrayList<Soundtrack> soundtracks) {
        tracks = soundtracks;
        isFavorited = true;
        for (int i = 0; i < tracks.size(); i++) {
            if (!tracks.get(i).favorited)
                isFavorited = false;
        }
        adapter = new SoundtrackAdapter(tracks, getContext(), this);
        listView.setAdapter(adapter);
        Utilities.setListViewHeightBasedOnChildren(listView);
        adapter.notifyDataSetChanged();
    }
}
