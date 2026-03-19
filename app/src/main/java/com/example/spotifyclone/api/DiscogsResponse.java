package com.example.spotifyclone.api;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DiscogsResponse {

    @SerializedName("results")
    public List<Result> results;

    public static class Result{
        @SerializedName("id")
        public int id;

        @SerializedName("title")
        public String title;

        @SerializedName("thumb")
        public String coverImage;

        @SerializedName("artists")
        public String artist;

        @SerializedName("year")
        public String year;

        @SerializedName("master_id")
        public int masterId;
    }



}
