package com.example.musicroulette.utils;

import android.net.Uri;

import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


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

    /*************************************************************
     * GET ALL CATEGORIES
     *************************************************************/
    private final static String GET_ALL_CATEGORIES_BASE_URL = "https://api.spotify.com/v1/browse/categories";

    public static String buildGetAllCategoriesURL() {
        return GET_ALL_CATEGORIES_BASE_URL;
    }

    /*************************************************************
     * GET A CATEGORY'S PLAYLISTS
     * Get a list of Spotify playlists tagged with a particular category.
     *************************************************************/
    private final static String GET_A_CATEGORYS_PLAYLISTS_BASE_URL = "https://api.spotify.com/v1/browse/categories";

    public static String buildGetACategorysPlaylistsBaseURL(String category_id) {
        return Uri.parse(GET_A_CATEGORYS_PLAYLISTS_BASE_URL).buildUpon()
                .appendPath(category_id)
                .appendPath("playlists")
                .build()
                .toString();
    }
}