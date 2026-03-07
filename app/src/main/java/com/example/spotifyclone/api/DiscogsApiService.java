package com.example.spotifyclone.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface DiscogsApiService {
    @Headers("User-Agent: SpotifyCloneApp/1.0")
    @GET("database/search")
    Call<DiscogsResponse> searchDatabase(
            @Query("q") String query,
            @Query("type") String type,
            @Query("token") String token
    );
}
