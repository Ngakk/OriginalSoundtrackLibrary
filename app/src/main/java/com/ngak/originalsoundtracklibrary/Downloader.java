package com.ngak.originalsoundtracklibrary;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;

public class Downloader {

    static DownloadManager downloadManager;
    final static String Domain = "http://192.168.1.218:8080/ostlibrary/";

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

    public static class AsyncAlbum extends AsyncTask<String, String, JSONObject> {
        private final static String TAG = "Async";
        int id;
        AlbumOverviewDownloader adapter;
        public AsyncAlbum(int id, AlbumOverviewDownloader adapter) {
            this.id = id;
            this.adapter = adapter;
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            ArrayList<NameValuePair> sparam = new ArrayList<>();
            sparam.add(new BasicNameValuePair("id", Integer.toString(id)));
            JSONObject json = jParser.getJSONFromUrl("http://192.168.1.218:8080/ostlibrary/Android/getAlbum.php", sparam);
            return json;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(JSONObject json){
            try {
                // Getting JSON Array
                JSONObject data = json.getJSONObject("0");
                Album album = new Album(data.getInt("id"), data.getString("name"), "composer",
                        data.getString("date"), data.getString("sourcename"), null, Domain + data.getString("imageurl"));
                adapter.onAsyncOverviewEnd(album);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class AsyncAlbumTracklist extends AsyncTask<String, String, JSONObject> {
        private final static String TAG = "Async";
        int id;
        AlbumOverviewDownloader adapter;
        public AsyncAlbumTracklist(int id, AlbumOverviewDownloader adapter) {
            this.id = id;
            this.adapter = adapter;
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            ArrayList<NameValuePair> sparam = new ArrayList<>();
            sparam.add(new BasicNameValuePair("id", Integer.toString(id)));
            JSONObject json = jParser.getJSONFromUrl(Domain + "Android/getAlbumTracklist.php", sparam);
            return json;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(JSONObject json){
            try {
                // Getting JSON Array

                ArrayList<Soundtrack> tracks = new ArrayList<>();
                for(int i = 0; i < json.length(); i++){
                    ArrayList<String> artists = new ArrayList<>();
                    JSONObject track = json.getJSONObject(Integer.toString(i));
                    JSONObject artist = track.getJSONObject("artistas");
                    for(int j = 0; j < artist.length(); j++){
                        JSONObject artista = artist.getJSONObject(Integer.toString(j));
                        artists.add(artista.getString("fullname"));
                    }
                    tracks.add(new Soundtrack(track.getInt("id"), track.getString("name"),  track.getString("albumname"), artists, track.getString("file"), false));
                }
                adapter.onAsyncTracksEnd(tracks);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /*TODO: hacer que cargue la portada del album que es cuando le das click
     que no cargue 20 veces el artista en un solo track en el overview
     -ya deplano si acabo unity (o le avanzo suficiente) entonces hago el detailed view
     - tabién podria escribir en la base de datos local y no descargar tanto
    */
    public static class AsyncNewest extends AsyncTask<String, String, JSONObject> {
        private final static String TAG = "Async";
        MainActivityDownloader adapter;
        public AsyncNewest(MainActivityDownloader adapter) {
            this.adapter = adapter;
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl("http://192.168.1.218:8080/ostlibrary/Android/newest.php", null);
            return json;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(JSONObject json){
            try {
                // Getting JSON Array
                ArrayList<Album> albums = new ArrayList<>();
                for(int i = 0; i < json.length(); i++){
                    JSONObject album = json.getJSONObject(Integer.toString(i));
                    albums.add(new Album(album.getInt("id"), album.getString("name"),"composer", album.getString("date"),
                            album.getString("sourcename"), null, Domain + album.getString("imageurl")));
                }

                adapter.onAsyncNewestEnd(albums);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class AsyncBest extends AsyncTask<String, String, JSONObject> {
        private final static String TAG = "Async";
        MainActivityDownloader adapter;
        public AsyncBest(MainActivityDownloader adapter) {
            this.adapter = adapter;
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl("http://192.168.1.218:8080/ostlibrary/Android/best.php", null);
            return json;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(JSONObject json){
            try {
                // Getting JSON Array
                ArrayList<Album> albums = new ArrayList<>();
                for(int i = 0; i < json.length(); i++){
                    JSONObject album = json.getJSONObject(Integer.toString(i));
                    albums.add(new Album(album.getInt("id"), album.getString("name"),"composer", album.getString("date"),
                            album.getString("sourcename"), null, Domain + album.getString("imageurl")));
                }

                adapter.onAsyncBestEnd(albums);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class AsyncRandom extends AsyncTask<String, String, JSONObject> {
        private final static String TAG = "Async";
        MainActivityDownloader adapter;
        public AsyncRandom(MainActivityDownloader adapter) {
            this.adapter = adapter;
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl("http://192.168.1.218:8080/ostlibrary/Android/random.php", null);
            return json;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(JSONObject json){
            try {
                // Getting JSON Array
                ArrayList<Album> albums = new ArrayList<>();
                for(int i = 0; i < json.length(); i++){
                    JSONObject album = json.getJSONObject(Integer.toString(i));
                    albums.add(new Album(album.getInt("id"), album.getString("name"),"composer", album.getString("date"),
                            album.getString("sourcename"), null, Domain + album.getString("imageurl")));
                }

                adapter.onAsyncRandomEnd(albums);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
