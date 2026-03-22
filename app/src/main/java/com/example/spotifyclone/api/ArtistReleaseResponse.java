package com.example.spotifyclone.api;

import java.util.List;

public class ArtistReleaseResponse {
    public List<Release> releases;

    public static class Release{
        public int id;
        public String title;
        public String type;
        public String thumb;
        public String artist;
    }
}
