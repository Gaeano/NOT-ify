package com.example.spotifyclone.api;

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
                    callback.onSuccess(response.body().results);
                } else {
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
}

