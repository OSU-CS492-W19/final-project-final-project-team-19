package com.example.musicroulette;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicroulette.utils.NetworkUtils;
import com.example.musicroulette.utils.SpotifyUtils;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SAVED_REPO_KEY = "spotifyRepo";

    private String mAccessToken;
    private String mAccessCode;
    private ImageView mAlbumImage;
    private TextView mSongName;
    private TextView mArtistName;
    private Button mShuffle;
    private SharedPreferences mPrefs;

    private Button mOpenSpotify;
    private String mSongUri;
    private String mSongSpotifyUrl;

    private SpotifyUtils.SpotifyTrackRepo savedTrackRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAlbumImage = findViewById(R.id.album_art);
        mShuffle = findViewById(R.id.shuffle_button);
        mOpenSpotify = findViewById(R.id.btn_open_spotify);

        savedTrackRepo = new SpotifyUtils.SpotifyTrackRepo();

        mSongUri = null;
        mSongSpotifyUrl = null;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_REPO_KEY)) {
            savedTrackRepo = (SpotifyUtils.SpotifyTrackRepo) savedInstanceState.getSerializable(SAVED_REPO_KEY);
            Log.d(TAG, "=== Loading Data from Saved Instance");

            mArtistName = findViewById(R.id.song_artist);
            mSongName = findViewById(R.id.song_title);

            Picasso.get()
                    .load(savedTrackRepo.savedAlbumImageURL)
                    .into(mAlbumImage);
            mSongName.setText(savedTrackRepo.savedSongName);
            mArtistName.setText(savedTrackRepo.savedArtistName);

            mSongUri = savedTrackRepo.savedSongUri;
            mSongSpotifyUrl = savedTrackRepo.savedSongURL;
        }

        if(mAccessToken == null) {
            RequestToken();
        }

        // Placeholder image if there is no current image in the view
		if (mAlbumImage.getDrawable() == null) {
			Picasso.get()
					.load("https://developer.spotify.com/assets/branding-guidelines/icon4@2x.png")
					.into(mAlbumImage);
		}

        //When shuffle button is pressed
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

        mOpenSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "=== App Spotify Song Uri: " + mSongUri);
                Log.d(TAG, "=== Browser Spotify Url: " + mSongSpotifyUrl);

                if(mSongUri != null && mSongSpotifyUrl != null) {
                    PackageManager pm = getPackageManager();
                    boolean isSpotifyInstalled;
                    try {   //Try to open the spotify app
                        pm.getPackageInfo("com.spotify.music", 0);
                        isSpotifyInstalled = true;
                    } catch (PackageManager.NameNotFoundException e) {
                        isSpotifyInstalled = false;
                    }

                    if (isSpotifyInstalled) {   //If the Spotify App is installed, open the song in the app
                        Uri trackURL = Uri.parse(mSongUri);
                        Intent intent = new Intent(Intent.ACTION_VIEW, trackURL);
                        startActivity(intent);
                    }
                    else {                      //If Spotify App is not install, open the song in browser Spotify
                        Uri trackURL = Uri.parse(mSongSpotifyUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, trackURL);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (savedTrackRepo != null) {
            outState.putSerializable(SAVED_REPO_KEY, savedTrackRepo);
        }
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
                if(playlists != null) {
                    if (playlists.items.length > 0) {
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

        	mArtistName = findViewById(R.id.song_artist);
        	mSongName = findViewById(R.id.song_title);

            if(s != null) {
                SpotifyUtils.Track[] tracks = SpotifyUtils.parseSpotifyPlaylist(s);
                //Log.d(TAG, "=== Number of tracks in playlist: " + tracks.length);

                //If tracks were found
                if(tracks.length > 0){
                    //Get a random number between 0 and the number of tracks in the playlist
                    int rand = new Random().nextInt(tracks.length);
                    String randomTrack = tracks[rand].track.name;
                    String randomArtist = "";
                    Log.d(TAG, "=== Random Track Name: " + randomTrack);

                    for(int i = 1; i <= tracks[rand].track.artists.length; i++){    //For each artist
                        //If the person is actually an artist, add them to the artist string list
                        if("artist".equals(tracks[rand].track.artists[i-1].type)) {
                            randomArtist += tracks[rand].track.artists[i - 1].name + ", ";
                        }
                    }
                    randomArtist = randomArtist.substring(0, randomArtist.length() - 2);    //Remove extra comma
                    Log.d(TAG, "=== Track Artists: " + randomArtist);

                    //Do something with the track name?
					mSongName.setText(randomTrack);
					mArtistName.setText(randomArtist);

					savedTrackRepo.savedSongName = randomTrack;
					savedTrackRepo.savedArtistName = randomArtist;

                    Log.d(TAG, "***** Saved Song Name: " + savedTrackRepo.savedSongName);
                    Log.d(TAG, "***** Saved Artist Name: " + savedTrackRepo.savedArtistName);

                    //Load the track's album image
                    if(tracks[rand].track.album.images.length > 0) {
                        //Log.d(TAG, "=== Track Album Image: " + tracks[rand].track.album.images[0].url);
                        Picasso.get()
                                .load(tracks[rand].track.album.images[0].url)
                                .into(mAlbumImage);

                        savedTrackRepo.savedAlbumImageURL = tracks[rand].track.album.images[0].url;
                        Log.d(TAG, "***** Saved Image URL: " + savedTrackRepo.savedAlbumImageURL);
                    }

                    //Log.d(TAG, "=== Track URL: " + tracks[rand].track.href);
                    mSongUri = tracks[rand].track.uri;
                    mSongSpotifyUrl = tracks[rand].track.external_urls.spotify;

                    savedTrackRepo.savedSongUri = tracks[rand].track.uri;
                    savedTrackRepo.savedSongURL = tracks[rand].track.external_urls.spotify;

                    Log.d(TAG, "***** Saved Song Uri: " + savedTrackRepo.savedSongUri);
                    Log.d(TAG, "***** Saved Song URL: " + savedTrackRepo.savedSongURL);
                }
            }
            else {
                Log.d(TAG, "=== Result is null");
            }
        }
    }
}
