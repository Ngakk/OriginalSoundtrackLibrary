package com.ngak.originalsoundtracklibrary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SoundtrackAdapter extends ArrayAdapter<Soundtrack> implements View.OnClickListener {
    private ArrayList<Soundtrack> tracks;
    Context mContext;
    TrackListHolder holder;
    private int ultimaPosicion = -1;


    private static class ViewHolder{
        TextView txtName;
        TextView txtArtist;
        TextView txtAlbum;
        ImageView play;
        ImageView download;
        ImageView favorite;
    }

    public SoundtrackAdapter(ArrayList<Soundtrack> tracks, Context context, TrackListHolder holder){
        super(context, R.layout.playlist_item, tracks);
        this.tracks = tracks;
        this.mContext = context;
        this.holder = holder;
    }

    @Override
    public void onClick(View v) {

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Soundtrack track = getItem(position);
        final ViewHolder viewHolder;
        final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.playlist_item, parent, false);
            viewHolder.txtName = (TextView)convertView.findViewById(R.id.track_name);
            viewHolder.txtAlbum = (TextView)convertView.findViewById(R.id.track_album);
            viewHolder.txtArtist = (TextView)convertView.findViewById(R.id.track_artist);
            viewHolder.play = (ImageView)convertView.findViewById(R.id.track_play);
            viewHolder.download = (ImageView)convertView.findViewById(R.id.track_download);
            viewHolder.favorite = (ImageView)convertView.findViewById(R.id.track_favorite);
            result = convertView;
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
            result = convertView;
        }

        viewHolder.txtName.setText(track.getName());
        String finalArtist = "";
        ArrayList<String> artists = track.getArtists();
        for(int i = 0; i < artists.size(); i++){
            if(i != 0)
                finalArtist += ", ";
            finalArtist += artists.get(i);
        }
        finalArtist += ".";
        viewHolder.txtArtist.setText(finalArtist);
        viewHolder.txtAlbum.setText(track.getAlbum());
        if(track.favorited) {
            viewHolder.favorite.setImageDrawable(getContext().getDrawable(R.drawable.ic_favorite_black_24dp));
        }else {
            viewHolder.favorite.setImageDrawable(getContext().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        }
        viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(track.favorited){
                    viewHolder.favorite.setImageDrawable(getContext().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                }
                else{
                    viewHolder.favorite.setImageDrawable(getContext().getDrawable(R.drawable.ic_favorite_black_24dp));
                }
                track.favorited = !track.favorited;
                holder.onTrackFavoriteChanged(position, track.favorited);
                notifyDataSetChanged();
            }
        });

        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Place holder de play y pausa", Toast.LENGTH_SHORT).show();
            }
        });
        if(Uri.parse(track.getFileurl()) == Uri.EMPTY)
            viewHolder.download.setVisibility(View.GONE);
        viewHolder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Should download something", Toast.LENGTH_SHORT).show();
                holder.requestDownload(Uri.parse("http://192.168.1.218:8080/ostlibrary/" + track.getFileurl()));
            }
        });
        return convertView;
    }
}
