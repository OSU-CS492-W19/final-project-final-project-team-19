package com.example.musicroulette;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String mAccessToken;
    private String mAccessCode;
    private ImageView mAlbumImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

		Transformation transformation = new RoundedTransformationBuilder()
				.borderColor(Color.BLACK)
				.borderWidthDp(3)
				.cornerRadiusDp(30)
				.oval(false)
				.build();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAlbumImage = findViewById(R.id.album_art);
        mAlbumImage.setImageResource(R.drawable.slime);

        if(mAccessToken == null) {
            RequestToken();
        }

        // test loading image from url
		Picasso.get()
				.load("https://is5-ssl.mzstatic.com/image/thumb/Music118/v4/ab/ea/31/abea3194-5ec0-47c8-3644-7e76c195f126/8718857500339.png/999999999x0w.jpg")
				.transform(transformation)
				.into(mAlbumImage);
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
            case R.id.action_discover:
                Intent discoverIntent = new Intent(this, MainActivity.class);
                startActivity(discoverIntent);
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

            Log.d(TAG, "== Do in Background");
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null) {
                Log.d(TAG, s);
                SpotifyUtils.SpotifyCategoryPlaylists playlists = SpotifyUtils.parseSpotifyCategoryPlaylists(s);
                // Do something with the parsed category playlists here
            }
            else {
                Log.d(TAG, "result is null");
            }
        }
    }

}
