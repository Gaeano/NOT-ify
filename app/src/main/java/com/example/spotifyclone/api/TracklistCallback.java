package com.example.spotifyclone.api;

import java.util.List;

public interface TracklistCallback {

    void onSuccess(List<MasterReleaseResponse.Track> tracks, String highResUrl);

    void onError(String errorMessage);
}
