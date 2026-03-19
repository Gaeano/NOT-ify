package com.example.spotifyclone.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MasterReleaseResponse {

    @SerializedName("tracklist")
    public List<Track> tracklist;

    public static class Track{
        @SerializedName("title")
        public String title;

    }

}
