package com.ngak.originalsoundtracklibrary;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class DataBaseAdapter
{
    //Album
    public static final String ALBUM_TABLE = "album";
    public static final String ALBUM_NAME = "name";
    public static final String ALBUM_ID = "id";
    public static final String ALBUM_DATE = "releasedate";
    public static final String ALBUM_SOURCE = "sourceid";
    public static final String ALBUM_COVER = "imageurl";
    public static final String ALBUM_ARTIST = "mainartistid";
    public static final String ALBUM_RATING = "rating";
    //Artist
    public static final String ARTIST_TABLE = "artist";
    public static final String ARTIST_FULLNAME = "fullname";
    public static final String ARTIST_NAME = "firstname";
    public static final String ARTIST_LASTNAME = "lastname";
    public static final String ARTIST_ID = "id";
    public static final String ARTIST_DATE = "birthdate";
    public static final String ARTIST_PORTRAIT = "imageurl";
    //Track
    public static final String TRACK_TABLE = "track";
    public static final String TRACK_ID = "id";
    public static final String TRACK_NAME = "name";
    public static final String TRACK_ALBUM = "albumid";
    public static final String TRACK_FILE = "fileurl";
    //playlist
    public static final String LIST_TABLE = "playlist";
    //Track_list_link
    public static final String TRACK_LIST_TABLE = "track_list_link";
    public static final String TRACK_LIST_TRACK = "trackid";
    public static final String TRACK_LIST_LIST = "listid";

    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public DataBaseAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DataBaseAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataBaseAdapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    public Cursor getRow(String table, int id){
        try
        {
            String sql ="SELECT * FROM " + table + " WHERE id = " + id;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null)
            {
                mCur.moveToNext();
            }
            return mCur;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    //Album
    public ArrayList<Album> makeAlbumList(String sql){
        ArrayList<Album> albums = new ArrayList<>();
        Cursor mCur = mDb.rawQuery(sql, null);
        mCur.moveToFirst();
        while(!mCur.isAfterLast()){
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_album);
            albums.add(new Album(
                    mCur.getInt(mCur.getColumnIndex(ALBUM_ID)),
                    mCur.getString(mCur.getColumnIndex(ALBUM_NAME)),
                    mCur.getString(mCur.getColumnIndex(ARTIST_FULLNAME)),
                    mCur.getString(mCur.getColumnIndex(ALBUM_DATE)),
                    mCur.getString(mCur.getColumnIndex(ALBUM_SOURCE)),
                    bitmap,
                    mCur.getString(mCur.getColumnIndex(ALBUM_COVER)))
            );
            mCur.moveToNext();
        }
        mCur.close();
        return albums;
    }

    public ArrayList<Album> getNewestAlbums(){
        String sql = "SELECT "+ ALBUM_TABLE +".*, "+ ARTIST_TABLE +"."+ ARTIST_FULLNAME +" FROM "+ ALBUM_TABLE + " LEFT JOIN "+ ARTIST_TABLE +" ON "+ ALBUM_TABLE +"."+ ALBUM_ARTIST +" = "+ARTIST_TABLE+".id ORDER BY "+ ALBUM_TABLE +"."+ALBUM_DATE+" DESC LIMIT 6";
        return makeAlbumList(sql);
    }

    public ArrayList<Album> getBestAlbums(){
        String sql = "SELECT "+ ALBUM_TABLE +".*, "+ ARTIST_TABLE +"."+ ARTIST_FULLNAME +" FROM "+ ALBUM_TABLE + " LEFT JOIN "+ ARTIST_TABLE +" ON "+ ALBUM_TABLE +"."+ ALBUM_ARTIST +" = "+ARTIST_TABLE+".id ORDER BY "+ ALBUM_TABLE +"."+ALBUM_RATING+" LIMIT 6";
        return makeAlbumList(sql);
    }

    public ArrayList<Album> getRandomAlbums(){
        String sql = "SELECT "+ ALBUM_TABLE +".*, "+ ARTIST_TABLE +"."+ ARTIST_FULLNAME +" FROM "+ ALBUM_TABLE + " LEFT JOIN "+ ARTIST_TABLE +" ON "+ ALBUM_TABLE +"."+ ALBUM_ARTIST +" = "+ARTIST_TABLE+".id ORDER BY RANDOM() LIMIT 3";
        return makeAlbumList(sql);
    }

    public Album getAlbum(int id){

        String sql = "SELECT "+ ALBUM_TABLE +".*, "+ ARTIST_TABLE +"."+ ARTIST_FULLNAME +" FROM "+ ALBUM_TABLE + " LEFT JOIN "+ ARTIST_TABLE +" ON "+ ALBUM_TABLE +"."+ ALBUM_ARTIST +" = "+ARTIST_TABLE+".id WHERE "+ ALBUM_TABLE +".id = " + id;
        Cursor mCur = mDb.rawQuery(sql, null);
        mCur.moveToFirst();
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_album);

        Album album = new Album(
                mCur.getInt(mCur.getColumnIndex(ALBUM_ID)),
                mCur.getString(mCur.getColumnIndex(ALBUM_NAME)),
                mCur.getString(mCur.getColumnIndex(ARTIST_FULLNAME)),
                mCur.getString(mCur.getColumnIndex(ALBUM_DATE)),
                mCur.getString(mCur.getColumnIndex(ALBUM_SOURCE)),
                bitmap,
                mCur.getString(mCur.getColumnIndex(ALBUM_COVER))
        );
        mCur.close();
        return album;
    }

    //Soundtracks
    public ArrayList<Soundtrack> getTrackList(int listid){
        ArrayList<Soundtrack> soundtracks = new ArrayList<>();
        String sql = "SELECT track.*, artist.fullname AS fullname, album.name AS albumname " +
                "FROM track " +
                "INNER JOIN (" +
                "    track_list_link " +
                "    INNER JOIN playlist" +
                "    ON track_list_link.listid = playlist.id) " +
                "ON track.id = track_list_link.trackid " +
                "LEFT JOIN (" +
                "    track_artist_link" +
                "    INNER JOIN artist" +
                "    ON track_artist_link.artistid = artist.id) " +
                "ON track_artist_link.trackid = track.id " +
                "LEFT JOIN album " +
                "ON album.id = track.albumid " +
                "WHERE playlist.id = " + listid;
        Cursor mCur = mDb.rawQuery(sql, null);
        mCur.moveToFirst();
        while(!mCur.isAfterLast()){
            int currentid = mCur.getInt(mCur.getColumnIndex("id"));
            Uri image_uri;
            if(mCur.getString(mCur.getColumnIndex(TRACK_FILE)) != null)
                image_uri = Uri.parse(mCur.getString(mCur.getColumnIndex(TRACK_FILE)));
            else
                image_uri = Uri.EMPTY;
            ArrayList<String> artists = new ArrayList<>();
            String track_name = mCur.getString(mCur.getColumnIndex(TRACK_NAME));
            String album_name = mCur.getString(mCur.getColumnIndex("albumname"));

            //Is favorited
            boolean isFav = true;
            String sqlfav = "SELECT track.* " +
                    "FROM playlist " +
                    "INNER JOIN (" +
                    "    track_list_link " +
                    "    INNER JOIN track " +
                    "    ON track_list_link.trackid = track.id) " +
                    "ON playlist.id = track_list_link.listid " +
                    "WHERE playlist.id = 1";
            Cursor favCount = mDb.rawQuery(sqlfav, null);
            if(favCount.getCount() == 0)
                isFav = false;
            favCount.close();
            while (mCur.getInt(mCur.getColumnIndex("id")) == currentid){
                artists.add(mCur.getString(mCur.getColumnIndex("fullname")));
                mCur.moveToNext();
                if(mCur.isAfterLast())
                    break;
            }
            soundtracks.add(new Soundtrack(currentid, track_name, album_name, artists, image_uri, isFav));
            if(!mCur.isAfterLast())
                mCur.moveToNext();
            else
                break;
        }
        mCur.close();
        return soundtracks;
    }

    public ArrayList<Soundtrack> getAlbumTracks(int albumid){
        ArrayList<Soundtrack> soundtracks = new ArrayList<>();
        String sql = "SELECT track.*, artist.fullname AS fullname, album.name AS albumname " +
                "FROM track " +
                "LEFT JOIN (" +
                "    track_artist_link" +
                "    INNER JOIN artist" +
                "    ON track_artist_link.artistid = artist.id) " +
                "ON track_artist_link.trackid = track.id " +
                "LEFT JOIN album " +
                "ON album.id = track.albumid " +
                "WHERE album.id = " + albumid;
        Cursor mCur = mDb.rawQuery(sql, null);
        mCur.moveToFirst();
        while(!mCur.isAfterLast()){
            int currentid = mCur.getInt(mCur.getColumnIndex("id"));
            String track_name = mCur.getString(mCur.getColumnIndex(TRACK_NAME));
            String album_name = mCur.getString(mCur.getColumnIndex("albumname"));
            //Track URI
            Uri track_uri;
            if(mCur.getString(mCur.getColumnIndex(TRACK_FILE)) != null)
                track_uri = Uri.parse(mCur.getString(mCur.getColumnIndex(TRACK_FILE)));
            else
                track_uri = Uri.EMPTY;
            //Is favorited
            boolean isFav = true;
            String sqlfav = "SELECT track.* " +
                    "FROM playlist " +
                    "INNER JOIN (" +
                    "    track_list_link " +
                    "    INNER JOIN track " +
                    "    ON track_list_link.trackid = track.id) " +
                    "ON playlist.id = track_list_link.listid " +
                    "WHERE playlist.id = 1";
            Cursor favCount = mDb.rawQuery(sqlfav, null);
            if(favCount.getCount() == 0)
                isFav = false;
            favCount.close();
            //Artist names
            ArrayList<String> artists = new ArrayList<>();
            while (mCur.getInt(mCur.getColumnIndex("id")) == currentid){
                artists.add(mCur.getString(mCur.getColumnIndex("fullname")));
                mCur.moveToNext();
                if(mCur.isAfterLast())
                    break;
            }
            soundtracks.add(new Soundtrack(currentid, track_name, album_name, artists, track_uri, isFav));
            if(!mCur.isAfterLast())
                mCur.moveToNext();
            else
                break;
        }
        mCur.close();
        return soundtracks;
    }

    //Lists
    public void addAlbumToList(int albumid, int listid){
        String sqltracks = "SELECT * FROM track WHERE "+ TRACK_ALBUM + " = " + albumid;
        Cursor mCur = mDb.rawQuery(sqltracks, null);
        mCur.moveToFirst();
        while(!mCur.isAfterLast()){
            String sqlDup = "SELECT * FROM track_list_link WHERE trackid = " + mCur.getInt(mCur.getColumnIndex("id")) + " AND listid = " + listid;
            Cursor count = mDb.rawQuery(sqlDup, null);
            if(count.getCount() == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TRACK_LIST_TRACK, mCur.getInt(mCur.getColumnIndex("id")));
                contentValues.put(TRACK_LIST_LIST, listid);

                mDb.insert(TRACK_LIST_TABLE, null, contentValues);
            }
            mCur.moveToNext();
        }
        mCur.close();
    }

    public void removeAlbumFromList(int albumid, int listid){
        String sqltracks = "SELECT * FROM track WHERE "+ TRACK_ALBUM + " = " + albumid;
        Cursor mCur = mDb.rawQuery(sqltracks, null);
        mCur.moveToFirst();
        while(!mCur.isAfterLast()){
            String sqlDel = "DELETE FROM "+ TRACK_LIST_TABLE + " WHERE trackid = " + mCur.getInt(mCur.getColumnIndex("id"));
            mDb.execSQL(sqlDel);
            mCur.moveToNext();
        }
        mCur.close();
    }

    public void addTrackToList(int trackid, int listid){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRACK_LIST_TRACK, trackid);
        contentValues.put(TRACK_LIST_LIST, listid);

        mDb.insert(TRACK_LIST_TABLE, null, contentValues);
    }

    public void removeTrackFromList(int trackid, int listid)
    {
        String sql = "DELETE FROM track_list_link WHERE trackid = "+ trackid +" AND "+ listid +" = 1";
        mDb.execSQL(sql);
    }
}