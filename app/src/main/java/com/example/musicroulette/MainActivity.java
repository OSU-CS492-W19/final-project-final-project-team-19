package com.example.musicroulette;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.musicroulette.utils.SpotifyUtils;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAuthorizatonFromSpotify();
    }

    private void requestAuthorizatonFromSpotify() {
        String url = SpotifyUtils.buildAuthorizationURL();
        Log.d(TAG, "Requesting authorization at: " + url);
        new SpotifyAuthorizationTask().execute(url);
    }


}
