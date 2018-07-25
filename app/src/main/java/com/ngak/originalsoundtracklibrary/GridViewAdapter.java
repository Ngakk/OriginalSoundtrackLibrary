package com.ngak.originalsoundtracklibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter implements DownloadRequester {
    private final Context mContext;
    private final ArrayList<Album> albums;

    public GridViewAdapter(Context context, ArrayList<Album> albums) {
        this.mContext = context;
        this.albums = albums;
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Album album = albums.get(position);

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.album_min_display, null);
        }

        // 3
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.album_min_cover);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.album_min_title);
        final TextView sourceTextView = (TextView)convertView.findViewById(R.id.album_min_source);
        final TextView composerTextView = (TextView)convertView.findViewById(R.id.album_min_composer);

        // 4
        if(!album.imageDownloaded && !album.getCoverurl().equals("")){
            new Downloader.AsyncLoadAlbumCover(album, this).execute(album.getCoverurl());
        }

        nameTextView.setText(album.getName());
        sourceTextView.setText(album.getSource());
        composerTextView.setText(album.getComposer());
        imageView.setImageBitmap(album.getCover());

        return convertView;
    }

    @Override
    public void onAsyncRequestEnd(Album album, Bitmap bitmap) {
        album.setCover(bitmap);
        notifyDataSetChanged();
    }
}
