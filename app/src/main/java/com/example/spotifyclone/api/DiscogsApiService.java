package com.example.spotifyclone.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DiscogsApiService {
    @Headers("User-Agent: SpotifyCloneApp/1.0")
    @GET("database/search")
    Call<DiscogsResponse> searchDatabase(
            @Query("q") String query,
            @Query("type") String type,
            @Query("token") String token
    );

    @GET("database/search")
    Call<DiscogsResponse> searchByGenre(
            @Query("q") String query,
            @Query("type") String type,
            @Query("token") String token,
            @Query("per_page") int perPage,
            @Query("genre") String genre,
            @Query("sort") String sort,
            @Query("sort_order") String sortOrder
    );

    @GET("releases/{id}")
    Call<MasterReleaseResponse> getReleaseDetails(
            @Path("id") int id,
            @Query("token") String token
    );
    @GET("masters/{id}")
    Call<MasterReleaseResponse> getMasterDetails(
            @Path("id") int id,
            @Query("token") String token
    );

    @GET("/artists/{artist_id}")
    Call<ArtistProfileResponse> getArtistProfile(
            @Path("artist_id") int artistId,
            @Query("token") String token
    );

    @GET("/artists/{artist_id}/releases")
    Call<ArtistReleaseResponse> getArtistReleases(
            @Path("artist_id") int artistId,
            @Query("sort") String sort,
            @Query("sort_order") String order,
            @Query("token") String token

    );

}
