package com.example.spotifyclone.api;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicFetcher{

    public void fetchSearched(String query, String token, MusicDataCallback callback) {
        DiscogsApiService discogsApiService = RetrofitClient.getService();

        Call<DiscogsResponse> call = discogsApiService.searchDatabase(query, "release", token);

        call.enqueue(new Callback<DiscogsResponse>() {
            @Override
            public void onResponse(Call<DiscogsResponse> call, Response<DiscogsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("MusicFetcher", "Response successful");
                    callback.onSuccess(response.body().results);
                } else {
                    Log.d("MusicFetcher", "Response not successful");
                    callback.onError("Error fetching music data");
                }
            }

            @Override
            public void onFailure(Call<DiscogsResponse> call, Throwable t) {
                callback.onError("Error fetching music data");
            }
        });
    }

    public void fetchByGenre(String genre, String token, MusicDataCallback callback) {
        DiscogsApiService discogsApiService = RetrofitClient.getService();

        Call<DiscogsResponse> call = discogsApiService.searchByGenre("", "release", token, 10, genre, "have", "desc");

        call.enqueue(new Callback<DiscogsResponse>() {
            @Override
            public void onResponse(Call<DiscogsResponse> call, Response<DiscogsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().results);
                } else {
                    callback.onError("Error fetching " + genre + " music data");
                }
            }

            @Override
            public void onFailure(Call<DiscogsResponse> call, Throwable t) {
                callback.onError("Error fetching music data");
            }
        });
    }

    public void fetchTrackList(String token, int albumId, TracklistCallback callback) {
        DiscogsApiService discogsApiService = RetrofitClient.getService();

        Call<MasterReleaseResponse> call = discogsApiService.retrieveTrackList(albumId, token);

        call.enqueue(new Callback<MasterReleaseResponse>() {
            @Override
            public void onResponse(Call<MasterReleaseResponse> call, Response<MasterReleaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().tracklist);
                } else {
                    callback.onError("Error fetching music data" + response.code());
                }
            }

            @Override
            public void onFailure(Call<MasterReleaseResponse> call, Throwable t) {
                callback.onError("Error fetching music data" + t.getMessage());
            }
        });
    }
}

