package com.example.spotifyclone.api;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicFetcher {

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

    public void fetchArtist(String token, String artistName, ArtistSearchCallback callback) {
        DiscogsApiService discogsApiService = RetrofitClient.getService();

        Call<DiscogsResponse> call = discogsApiService.searchDatabase(artistName, "artist", token);

        call.enqueue(new retrofit2.Callback<DiscogsResponse>() {
            @Override
            public void onResponse(Call<DiscogsResponse> call, Response<DiscogsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DiscogsResponse.Result artistResult = response.body().results.get(0);
                    callback.onSuccess(artistResult);
                } else {
                    callback.onError("Error fetching artist data");
                }
            }

            @Override
            public void onFailure(Call<DiscogsResponse> call, Throwable t) {
                callback.onError("Error fetching artist data");

            }
        });
    }

    public void fetchArtistDetails(String token, int artistId, ArtistDetailsCallback callback) {
        DiscogsApiService discogsApiService = RetrofitClient.getService();

        Call<ArtistProfileResponse> call = discogsApiService.getArtistProfile(artistId, token);

        call.enqueue(new Callback<ArtistProfileResponse>() {

            @Override
            public void onResponse(Call<ArtistProfileResponse> call, Response<ArtistProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String bio = response.body().profile;
                    if (bio != null && !bio.isEmpty()) {
                        String cleanBio = bio.replaceAll("\\[[a-zA-Z]=([^\\]]+)\\]", "$1");

                        String imageUrl = null;

                        if (response.body().images.get(0).uri != null & !response.body().images.get(0).uri.isEmpty()) {
                            imageUrl = response.body().images.get(0).uri;
                        }
                        callback.onSuccess(cleanBio, imageUrl);
                    } else {
                        callback.onError("No biography available");
                    }
                } else {
                    callback.onError("failed to fetch");

                }
            }

            @Override
            public void onFailure(Call<ArtistProfileResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void fetchArtistReleases(String token, int artistId, ArtistReleasesCallback callback) {

        DiscogsApiService discogsApiService = RetrofitClient.getService();

        Call<ArtistReleaseResponse> call = discogsApiService.getArtistReleases(artistId, "year", "desc", token);

        call.enqueue(new Callback<ArtistReleaseResponse>() {

            @Override
            public void onResponse(Call<ArtistReleaseResponse> call, Response<ArtistReleaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().releases != null) {
                    callback.onSuccess(response.body().releases);
                } else {
                    callback.onError("Releases failed. HTTP Code: " + response.code());                }
            }

            @Override
            public void onFailure(Call<ArtistReleaseResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });

    }
}
