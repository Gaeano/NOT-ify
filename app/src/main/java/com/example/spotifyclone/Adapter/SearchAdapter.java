package com.example.spotifyclone.Adapter;

import android.content.Context;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private OnClickItemListener listener;
    private List<DiscogsResponse.Result> searchResults;


    public interface OnClickItemListener{
        void onItemClick(int position);
    }

    public SearchAdapter(OnClickItemListener listener, List<DiscogsResponse.Result> resultsList){
        this.listener = listener;
        this.searchResults = resultsList;
    }



    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result_layout, parent, false);
        return new SearchViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        DiscogsResponse.Result result = searchResults.get(position);

        String fullTitle = result.title;

        if (fullTitle != null && fullTitle.contains(" - ")) {
            String[] parts = fullTitle.split(" - ", 2);
            holder.title.setText(parts[1]);
            holder.subtitle.setText(parts[0]);
        } else {
            holder.title.setText(fullTitle);
        }

        Glide.with(holder.itemView.getContext())
                .load(result.coverImage)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }


    public static class SearchViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView title;
        TextView subtitle;
        OnClickItemListener listener;


        public SearchViewHolder(@NonNull View itemView, OnClickItemListener listener) {
            super(itemView);
            this.listener = listener;
            imageView = itemView.findViewById(R.id.searchResultImage);
            title = itemView.findViewById(R.id.searchResultTitle);
            subtitle = itemView.findViewById(R.id.searchResultSubtitle);

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
