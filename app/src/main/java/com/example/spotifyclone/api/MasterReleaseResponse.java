package com.example.spotifyclone.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MasterReleaseResponse {

    @SerializedName("tracklist")
    public List<Track> tracklist;

    public List<AlbumImage> images;

    public static class Track{
        @SerializedName("title")
        public String title;

    }
    public static class AlbumImage{
        @SerializedName("uri")
        public String url;
    }


}
