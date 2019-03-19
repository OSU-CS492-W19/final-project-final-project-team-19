package com.example.musicroulette.utils;

import android.net.Uri;

import com.google.gson.Gson;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.net.URI;


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

    public static SpotifyCategories parseSpotifyCategories(String json) {
        Gson gson = new Gson();
        SpotifyCategoriesResults results = gson.fromJson(json, SpotifyCategoriesResults.class);
        return results.categories;
    }

    public static class SpotifyCategoriesResults {
        public SpotifyCategories categories;
    }

    public static class SpotifyCategories {
        public SpotifyCategory[] items;
    }

    public static class SpotifyCategory {
        public String href;
        public Icon[] icons;
        public String id;
        public String name;
    }

    public static class Icon {
        public int height;
        public String url;
        public int width;
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

    public static SpotifyCategoryPlaylists parseSpotifyCategoryPlaylists(String json) {
        Gson gson = new Gson();
        SpotifyCategoryPlaylistsResults results = gson.fromJson(json, SpotifyCategoryPlaylistsResults.class);
        return results.playlists;
    }

    public static class SpotifyCategoryPlaylistsResults {
        public SpotifyCategoryPlaylists playlists;
    }

    public static class SpotifyCategoryPlaylists {
        public String href;
        public SpotifyPlaylist[] items;
        public int total;
    }

    public static class SpotifyPlaylist {
        public ExternalURLs external_urls;
        public String href;
        public String id;
        public Icon[] images;
        public String name;
        public URI uri;
    }

    public static class ExternalURLs {
        public String spotify;
    }


    /*************************************************************
     * GET A PLAYLIST'S TRACKS
     * Get a list of Spotify tracks associated with a certain playlist.
     *************************************************************/
    public static Track[] parseSpotifyPlaylist(String json) {
        Gson gson = new Gson();
        SpotifyPlaylistTracks results = gson.fromJson(json, SpotifyPlaylistTracks.class);
        return results.items;
    }

    public static class SpotifyPlaylistTracks {
        public Track[] items;
    }

    public static class Track {
        public TrackDetails track;
    }

    public static class TrackDetails {
        public AlbumDetails album;
        public Artist[] artists;
        public String href;
        public SpotifyUrl external_urls;
        public String name;
        public String uri;
    }

    public static class AlbumDetails {
        public AlbumImage[] images;
    }

    public static class AlbumImage {
        public String url;
        public int height;
        public int width;
        public String name;
    }

    public static class Artist {
        public String name;
        public String type;
    }

    public static class SpotifyUrl {
        public String spotify;
    }
}