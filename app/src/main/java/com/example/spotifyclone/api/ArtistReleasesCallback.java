package com.example.spotifyclone.api;

import java.util.List;

public interface ArtistReleasesCallback {
    public void onSuccess(List<ArtistReleaseResponse.Release> resultList);
    public void onError(String errorMessage);
}
