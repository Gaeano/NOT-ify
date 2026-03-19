package com.example.spotifyclone.api;

import java.util.List;

public interface TracklistCallback {

    void onSuccess(List<MasterReleaseResponse.Track> tracks);

    void onError(String errorMessage);
}
