package com.ngak.originalsoundtracklibrary;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumDetails extends Fragment implements DownloadRequester  {
    int id;
    TextView textView;

    public AlbumDetails() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        this.id = bundle.getInt("id");
        return inflater.inflate(R.layout.fragment_album_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = (TextView)view.findViewById(R.id.textView2);
        //new Downloader.AsyncAlbum(id, this).execute();
    }

    @Override
    public void onAsyncRequestEnd(Album album, Bitmap bitmap) {

    }
}
