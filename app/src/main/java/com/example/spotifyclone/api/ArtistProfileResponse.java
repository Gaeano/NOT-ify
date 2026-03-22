package com.example.spotifyclone.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArtistProfileResponse {
    public String profile;

    public List<ArtistImage> images;

    public class ArtistImage {
        public String uri;
    }

}
