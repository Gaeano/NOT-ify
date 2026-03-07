package com.example.spotifyclone.api;

import java.util.List;

public interface MusicDataCallback {
    void onSuccess(List<DiscogsResponse.Result> results);
    void onError(String errorMessage);
}
