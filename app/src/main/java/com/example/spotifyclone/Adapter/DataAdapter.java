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

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private Context context;
    private List<DiscogsResponse.Result> musicList;

    public DataAdapter(Context context, List<DiscogsResponse.Result> musicList){
        this.context = context;
        this.musicList = musicList;
    }



   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.itemimage, parent, false);
       return new ViewHolder(view);
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

       Glide.with(context)
               .load(album.coverImage)
               .placeholder(R.drawable.ic_launcher_background)
               .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }


    public static class ViewHolder extends  RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView artist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
        }

    }


}
