package com.example.musicroulette.utils;

import android.net.Uri;

import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SpotifyUtils {

    private final static String TAG = SpotifyUtils.class.getSimpleName();

    public final static String CLIENT_ID = "a8a1ba77b43d4c6abca5c15a7129bf44";
    public final static String CLIENT_SECRET = "a8a1ba77b43d4c6abca5c15a7129bf44";
    public final static String REDIRECT_URI = "musicroulette://spotify";

    public final static int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public final static int AUTH_CODE_REQUEST_CODE = 0x11;

    public static AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {
        return new AuthenticationRequest.Builder(SpotifyUtils.CLIENT_ID,
                type, SpotifyUtils.REDIRECT_URI)
                .setShowDialog(false)
                .setScopes(new String[]{"streaming"})
                .setCampaign("your-campaign-token")
                .build();
    }
}