package com.example.musicroulette.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final OkHttpClient mHTTPClient = new OkHttpClient();

    public static String doHTTPGet(String url, String accessToken) throws IOException {

        Log.d(TAG, "HTTP Get with Access Token: " + accessToken);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        Response response = mHTTPClient.newCall(request).execute();

        try {
            return response.body().string();
        } finally {
            response.close();
        }
    }

    public static String doHTTPPost(Request request) throws IOException {
        Response response = mHTTPClient.newCall(request).execute();

        try {
            return response.body().string();
        } finally {
            response.close();
        }
    }
}