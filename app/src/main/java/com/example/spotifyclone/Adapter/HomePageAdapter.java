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

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.ViewHolder> {

    private List<DiscogsResponse.Result> musicList;

    private OnClickItemListener listener;

    public interface OnClickItemListener{
        void onItemClick(DiscogsResponse.Result result);
    }

    public HomePageAdapter(OnClickItemListener listener, List<DiscogsResponse.Result> musicList){
        this.listener = listener;
        this.musicList = musicList;
    }





   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemimage, parent, false);
       return new ViewHolder(view, listener);
   }

   @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       DiscogsResponse.Result album = musicList.get(position);

       String fullTitle = album.title;

       if (fullTitle != null && fullTitle.contains(" - ")) {
           String[] parts = fullTitle.split(" - ", 2);
           holder.title.setText(parts[1]);
       } else {
           holder.title.setText(fullTitle);
       }

       Glide.with(holder.itemView.getContext())
               .load(album.coverImage)
               .placeholder(R.drawable.ic_launcher_background)
               .into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }


    public  class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView title;

        OnClickItemListener listener;


        public ViewHolder(@NonNull View itemView, OnClickItemListener listener) {
            super(itemView);
            this.listener = listener;
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getBindingAdapterPosition();

            if (pos != RecyclerView.NO_POSITION && listener != null){
                listener.onItemClick(musicList.get(pos));
            }
        }

    }


}
