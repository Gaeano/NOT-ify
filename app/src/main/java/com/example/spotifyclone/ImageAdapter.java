package com.example.spotifyclone;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context context;
    private List<DiscogsResponse.Result> musicList;

    public ImageAdapter(Context context, List<DiscogsResponse.Result> musicList){
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
       holder.title.setText(album.title);

       if (album.artist != null) {
           holder.artist.setText(album.artist);
       } else {
           holder.artist.setText("Unknown Artist");
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
            artist = itemView.findViewById(R.id.desc);
        }

    }


}
