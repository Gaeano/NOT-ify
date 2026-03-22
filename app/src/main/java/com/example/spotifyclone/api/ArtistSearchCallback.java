package com.example.spotifyclone.api;

public interface ArtistSearchCallback {
    void onSuccess(DiscogsResponse.Result artistResult);
    void onError(String errorMessage);
}
