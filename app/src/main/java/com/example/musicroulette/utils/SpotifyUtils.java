package com.example.musicroulette.utils;

import android.net.Uri;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SpotifyUtils {

    private final static String TAG = SpotifyUtils.class.getSimpleName();

    private final static String SPOTIFY_CLIENT_ID = "a8a1ba77b43d4c6abca5c15a7129bf44";
    private final static String SPOTIFY_CLIENT_SECRET = "a8a1ba77b43d4c6abca5c15a7129bf44";
    private final static String SPOTIFY_REDIRECT_URI = "https://musicroulette.com/spotify-redirect";

    private final static String SPOTIFY_AUTHORIZATION_BASE_URL = "https://accounts.spotify.com/authorize";
    private final static String SPOTIFY_AUTHORIZATION_STATE = "W33SrNUliN";
    private final static String SPOTIFY_AUTHORIZATION_RESPONSE_TYPE = "code";
    private final static String SPOTIFY_AUTHORIZATION_CLIENT_ID_PARAM = "client_id";
    private final static String SPOTIFY_AUTHORIZATION_RESPONSE_TYPE_PARAM = "response_type";
    private final static String SPOTIFY_AUTHORIZATION_REDIRECT_URI_PARAM = "redirect_uri";

    public static String buildAuthorizationURL() {
        return Uri.parse(SPOTIFY_AUTHORIZATION_BASE_URL).buildUpon()
                .appendQueryParameter(SPOTIFY_AUTHORIZATION_CLIENT_ID_PARAM, SPOTIFY_CLIENT_ID)
                .appendQueryParameter(SPOTIFY_AUTHORIZATION_RESPONSE_TYPE_PARAM, SPOTIFY_AUTHORIZATION_RESPONSE_TYPE)
                .appendQueryParameter(SPOTIFY_AUTHORIZATION_REDIRECT_URI_PARAM, SPOTIFY_REDIRECT_URI)
                .build()
                .toString();
    }

    private final static String SPOTIFY_REFRESH_TOKENS_BASE_URL = "https://accounts.spotify.com/api/token";
    private final static String SPOTIFY_REFRESH_TOKENS_GRANT_TYPE_PARAM = "grant_type";
    private final static String SPOTIFY_REFRESH_TOKENS_CODE_PARAM = "code";
    private final static String SPOTIFY_REFRESH_TOKENS_REDIRECT_URI_PARAM = "redirect_uri";
    private final static String SPOTIFY_REFRESH_TOKENS_CLIENT_ID_PARAM = "client_id";
    private final static String SPOTIFY_REFRESH_TOKENS_CLIENT_SECRET_PARAM = "client_secret";
    private final static String SPOTIFY_REFRESH_TOKENS_GRANT_TYPE = "authorization_code";

    public static Request requestAccessandRefreshTokens(String code) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(SPOTIFY_REFRESH_TOKENS_GRANT_TYPE_PARAM, SPOTIFY_REFRESH_TOKENS_GRANT_TYPE)
                .addFormDataPart(SPOTIFY_REFRESH_TOKENS_CODE_PARAM, code)
                .addFormDataPart(SPOTIFY_REFRESH_TOKENS_REDIRECT_URI_PARAM, SPOTIFY_REDIRECT_URI)
                .addFormDataPart(SPOTIFY_REFRESH_TOKENS_CLIENT_ID_PARAM, SPOTIFY_CLIENT_ID)
                .addFormDataPart(SPOTIFY_REFRESH_TOKENS_CLIENT_SECRET_PARAM, SPOTIFY_CLIENT_SECRET)
                .build();

        Request request = new Request.Builder()
                .url(SPOTIFY_REFRESH_TOKENS_BASE_URL)
                .post(requestBody)
                .build();

        return request;
    }
}