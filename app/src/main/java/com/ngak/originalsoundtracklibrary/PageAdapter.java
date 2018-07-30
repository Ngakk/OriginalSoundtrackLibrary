package com.ngak.originalsoundtracklibrary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    int numTabs, type, id;
    AlbumOverview albumOverview;
    AlbumDetails albumDetails;

    public PageAdapter(FragmentManager fm, int numTabs, int type, int id){
        super(fm);
        this.numTabs = numTabs;
        this.type = type;
        this.id = id;
    }

    @Override
    public Fragment getItem(int position) {
        switch (type){
            case 0:
                switch (position){
                    case 0:
                        if(albumOverview == null)
                            albumOverview = new AlbumOverview();
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", id);
                        albumOverview.setArguments(bundle);
                        return albumOverview;
                    case 1:
                        if(albumDetails == null)
                            albumDetails = new AlbumDetails();
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("id", id);
                        albumDetails.setArguments(bundle2);
                        return albumDetails;
                    default:
                        return null;
                }
        }
        return null;
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}