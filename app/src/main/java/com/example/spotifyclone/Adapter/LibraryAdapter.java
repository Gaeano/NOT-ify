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
import com.example.spotifyclone.api.DiscogsResponse;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    OnItemClickListenerLib listener;
    private List<DiscogsResponse.Result> resultList;

    public interface OnItemClickListenerLib{
        void onItemClick(int position);
    }

    public LibraryAdapter(OnItemClickListenerLib listener, List<DiscogsResponse.Result> resultList) {
        this.listener = listener;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result_layout, parent, false);
        return new LibraryViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        DiscogsResponse.Result result = resultList.get(position);

        String fullTitle = result.title;

        if (fullTitle != null && fullTitle.contains(" - ")) {
            String[] parts = fullTitle.split(" - ", 2);
            holder.albumTitle.setText(parts[1]);
            holder.albumArtist.setText(parts[0]);
        } else {
            holder.albumTitle.setText(fullTitle);
        }

        Glide.with(holder.itemView.getContext())
                .load(result.coverImage)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.albumImg);

    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class LibraryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView albumImg;
        TextView albumTitle, albumArtist;

        OnItemClickListenerLib listener;

        public LibraryViewHolder(@NonNull View itemView, OnItemClickListenerLib listener) {
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