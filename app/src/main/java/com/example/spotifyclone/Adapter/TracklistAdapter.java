package com.example.spotifyclone.Adapter;

import android.content.Context;
import android.telecom.StatusHints;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.api.DiscogsResponse;
import com.example.spotifyclone.api.MasterReleaseResponse;

import java.util.List;

public class TracklistAdapter extends RecyclerView.Adapter<TracklistAdapter.TrackViewHolder> {

    private List<MasterReleaseResponse.Track> resultList;

    private String artistName;
    public TracklistAdapter(List<MasterReleaseResponse.Track> resultList, String artistsName) {
        this.resultList = resultList;
        this.artistName = artistsName;
    }


    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracklist_card, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        MasterReleaseResponse.Track result = resultList.get(position);

        holder.songTitle.setText(result.title);
        holder.artistName.setText(artistName);



    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        TextView artistName;


        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title);
            artistName = itemView.findViewById(R.id.artist);


        }

    }
}
