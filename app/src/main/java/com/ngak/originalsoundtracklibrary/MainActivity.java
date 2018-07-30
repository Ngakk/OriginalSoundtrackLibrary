package com.ngak.originalsoundtracklibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainActivityDownloader {

    DataBaseAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        db = new DataBaseAdapter(this);
        db.createDatabase();

        new Downloader.AsyncNewest(this).execute();

        new Downloader.AsyncBest(this).execute();

        new Downloader.AsyncRandom(this).execute();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(this, "Esto te va a dejar buscar algo", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_search) {
            Toast.makeText(this, "Esto te va a dejar buscar algo", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Esto te va a dejar cambiar la configuracion de tu cuenta", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_myfav) {
            OpenMyFavorites();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void DownloadIndexAlbums(){

    }

    public void OpenMyFavorites(){
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, ActivityList.class);

        bundle.putInt("listid", 1);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void AlbumDetail(int id){
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, DetailedView.class);

        bundle.putInt("action", 0);
        bundle.putInt("id", id);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onAsyncNewestEnd(ArrayList<Album> albums) {
        final ArrayList<Album> newest = albums;
        GridView group1 = (GridView)findViewById(R.id.group1_content);
        GridViewAdapter group1adapter = new GridViewAdapter(this, newest);
        group1.setAdapter(group1adapter);
        group1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int albumid = newest.get(position).getId();
                AlbumDetail(albumid);
            }
        });
    }

    @Override
    public void onAsyncBestEnd(ArrayList<Album> albums) {
        final ArrayList<Album> best = albums;
        GridView group2 = (GridView)findViewById(R.id.group2_content);
        GridViewAdapter group2adapter = new GridViewAdapter(this, best);
        group2.setAdapter(group2adapter);
        group2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int albumid = best.get(position).getId();
                AlbumDetail(albumid);
            }
        });
    }

    @Override
    public void onAsyncRandomEnd(ArrayList<Album> albums) {
        final ArrayList<Album> random = albums;
        GridView group3 = (GridView)findViewById(R.id.group3_content);
        GridViewAdapter group3adapter = new GridViewAdapter(this, random);
        group3.setAdapter(group3adapter);
        group3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int albumid = random.get(position).getId();
                AlbumDetail(albumid);
            }
        });
    }

}
