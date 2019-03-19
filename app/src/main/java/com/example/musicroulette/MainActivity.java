package com.example.musicroulette;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.musicroulette.utils.NetworkUtils;
import com.example.musicroulette.utils.SpotifyUtils;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String mAccessToken;
    private String mAccessCode;
    private ImageView mAlbumImage;
    private Button mShuffle;
    private SharedPreferences mPrefs;
    private Transformation transformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

		transformation = new RoundedTransformationBuilder()
				.cornerRadiusDp(20)
				.oval(false)
				.build();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAlbumImage = findViewById(R.id.album_art);
        mAlbumImage.setImageResource(R.drawable.slime);
        mShuffle = findViewById(R.id.shuffle_button);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(mAccessToken == null) {
            RequestToken();
        }

        // Placeholder Image
		Picasso.get()
				.load("https://developer.spotify.com/assets/branding-guidelines/icon4@2x.png")
				.transform(transformation)
				.into(mAlbumImage);

        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String genre = mPrefs.getString(
                        getString(R.string.pref_genre_key),
                        getString(R.string.pref_genre_default_value)
                );
				Log.d(TAG, "== Genre: " + genre);
                String url = SpotifyUtils.buildGetACategorysPlaylistsBaseURL(genre);
                ArrayList<String> params = new ArrayList<>();
                params.add(url);
                params.add(mAccessToken);
                new RetrievePlaylistsOfAGenre().execute(params);
            }
        });
    }

    public void RequestToken() {
        final AuthenticationRequest request = SpotifyUtils.getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        AuthenticationClient.openLoginActivity(this, SpotifyUtils.AUTH_TOKEN_REQUEST_CODE, request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

        if (SpotifyUtils.AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            Log.d(TAG, "== Access Token: " + response.getAccessToken());
        } else if (SpotifyUtils.AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            Log.d(TAG, "== Access Code: " + mAccessCode);
        }
    }

    /** AsyncTask: RetrieveAllMusicCategoriesTask
     *  Description: Retrieve All Music Categories/Genres
     *  Params: url, access token
     *  Example:
     *          ArrayList<String> params = new ArrayList<>();
     *          params.add(SpotifyUtils.buildGetAllCategoriesURL());
     *          params.add(mAccessToken);
     *          new RetrieveAllMusicCategoriesTask().execute(params);
     **/
    class RetrieveAllMusicCategoriesTask extends AsyncTask<ArrayList<String>, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            String url = params[0].get(0);
            Log.d(TAG, "== URL: " + url);
            String accessToken = params[0].get(1);
            Log.d(TAG, "== ACCESS TOKEN: " + accessToken);
            String results = null;

            try {
                results = NetworkUtils.doHTTPGet(url, accessToken);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "== Do in Background");
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null) {
                Log.d(TAG, "== " + s);
                SpotifyUtils.SpotifyCategories categories = SpotifyUtils.parseSpotifyCategories(s);
                // Do something with the parsed categories here
            }
            else {
                Log.d(TAG, "== Result is null");
            }
        }
    }

    /** AsyncTask: RetrievePlaylistsOfAGenre
     *  Description: Retrieve All Playlists of a Genere
     *  Params: url, access token
     *  Example:
     *          ArrayList<String> params = new ArrayList<>();
     *          params.add(SpotifyUtils.buildGetACategorysPlaylistsBaseURL("pop"));
     *          params.add(mAccessToken);
     *          new RetrievePlaylistsOfAGenre().execute(params);
     **/
    class RetrievePlaylistsOfAGenre extends AsyncTask<ArrayList<String>, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            String url = params[0].get(0);
            String accessToken = params[0].get(1);
            String results = null;

            try {
                results = NetworkUtils.doHTTPGet(url, accessToken);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "== Playlist Fetching in Background");
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null) {
                SpotifyUtils.SpotifyCategoryPlaylists playlists = SpotifyUtils.parseSpotifyCategoryPlaylists(s);

                //If playlists were found
                if(playlists.items.length > 0){
                    //Get a random number between 0 and the number of playlists returned
                    int rand = new Random().nextInt(playlists.items.length);
                    Log.d(TAG, "=== Random Playlist Name: " + playlists.items[rand].name);

                    //Load the playlist image
                    /*if(playlists.items[rand].images.length > 0) {
                        //Log.d(TAG, "=== Playlist Image: " + playlists.items[rand].images[0].url);
                        Picasso.get()
                                .load(playlists.items[rand].images[0].url)
                                .transform(transformation)
                                .into(mAlbumImage);
                    }*/

                    //Choose a random playlist, grab its URL and get its track list
                    ArrayList<String> params = new ArrayList<>();
                    String url = Uri.parse(playlists.items[rand].href).buildUpon()
                                .appendPath("tracks")
                                .build()
                                .toString();
                    params.add(url);
                    params.add(mAccessToken);
                    new RetrieveTracksOfAPlaylist().execute(params);
                }
            }
            else {
                Log.d(TAG, "=== Result is null");
            }
        }
    }

    /** AsyncTask: RetrieveTracksOfAPlaylist
     *  Description: Retrieve All Tracks of a Playlist
     *  Params: url, access token
     **/
    class RetrieveTracksOfAPlaylist extends AsyncTask<ArrayList<String>, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            String url = params[0].get(0);
            String accessToken = params[0].get(1);
            String results = null;

            try {
                results = NetworkUtils.doHTTPGet(url, accessToken);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "== Track Fetching in Background");
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null) {
                SpotifyUtils.Track[] tracks = SpotifyUtils.parseSpotifyPlaylist(s);
                Log.d(TAG, "=== Number of tracks in playlist: " + tracks.length);

                //If tracks were found
                if(tracks.length > 0){
                    //Get a random number between 0 and the number of tracks in the playlist
                    int rand = new Random().nextInt(tracks.length);
                    Log.d(TAG, "=== Random Track Name: " + tracks[rand].track.name);
                    //Do something with the track name?

                    //Load the track's album image
                    if(tracks[rand].track.album.images.length > 0) {
                        Log.d(TAG, "=== Track Album Image: " + tracks[rand].track.album.images[0].url);
                        Picasso.get()
                                .load(tracks[rand].track.album.images[0].url)
                                .transform(transformation)
                                .into(mAlbumImage);
                    }
                }
            }
            else {
                Log.d(TAG, "=== Result is null");
            }
        }
    }

}
