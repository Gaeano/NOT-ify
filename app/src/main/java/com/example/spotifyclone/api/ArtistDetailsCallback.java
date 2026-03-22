package com.example.spotifyclone.api;

public interface ArtistDetailsCallback {
    void onSuccess(String bio, String imageUrl);
    void onError(String errorMessage);
}
