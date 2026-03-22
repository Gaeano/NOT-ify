package com.example.spotifyclone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.api.ArtistReleaseResponse;

import java.util.List;

public class ArtistDetailsAdapter extends RecyclerView.Adapter<ArtistDetailsAdapter.ArtistViewHolder> {

    OnItemClickListenerArtist listener;
    private List<ArtistReleaseResponse.Release> resultList;

    public interface OnItemClickListenerArtist {
        void onItemClick(int position);
    }

    public ArtistDetailsAdapter(OnItemClickListenerArtist listener, List<ArtistReleaseResponse.Release> resultList) {
        this.listener = listener;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result_layout, parent, false);
        return new ArtistViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        ArtistReleaseResponse.Release result = resultList.get(position);

        String fullTitle = result.title;

            holder.albumTitle.setText(fullTitle);

        Glide.with(holder.itemView.getContext())
                .load(result.thumb)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.albumImg);

        holder.albumArtist.setText(result.artist);

    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView albumImg;
        TextView albumTitle, albumArtist;

        OnItemClickListenerArtist listener;

        public ArtistViewHolder(@NonNull View itemView, OnItemClickListenerArtist listener) {
            super(itemView);
            this.listener = listener;
            albumImg = itemView.findViewById(R.id.searchResultImage);
            albumTitle = itemView.findViewById(R.id.searchResultTitle);
            albumArtist = itemView.findViewById(R.id.searchResultSubtitle);

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