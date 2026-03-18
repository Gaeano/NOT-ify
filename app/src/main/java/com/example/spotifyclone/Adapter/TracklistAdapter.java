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

import java.util.List;

public class TracklistAdapter extends RecyclerView.Adapter<TracklistAdapter.TrackViewHolder> {

    private OnItemClickListener listener;
    private List<DiscogsResponse.Result> resultList;

    public TracklistAdapter(OnItemClickListener listener, List<DiscogsResponse.Result> resultList) {
        this.listener = listener;
        this.resultList = resultList;
    }

    public interface OnItemClickListener {
        void onItemClick( int position);
    }


    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracklist_card, parent, false);
        return new TrackViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        DiscogsResponse.Result result = resultList.get(position);

        holder.songTitle.setText(result.title);
        holder.artistName.setText(result.artist);



    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView songTitle;
        TextView artistName;

        OnItemClickListener listener;

        public TrackViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            songTitle = itemView.findViewById(R.id.song_title);
            artistName = itemView.findViewById(R.id.artist);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();

            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(position);
            }
        }
    }
}
