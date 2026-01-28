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

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<imageItem> imageList;
    private onitemClickListener listener;



    public interface onitemClickListener{
        void onItemClick(imageItem item);
    }

    public ImageAdapter (Context context, List<imageItem> imageList, onitemClickListener listener){
        this.context = context;
        this.imageList = imageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemimage, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        imageItem currentItem = imageList.get(position);

        holder.imageView.setImageResource(currentItem.getImageResource());
        holder.titleTextView.setText(currentItem.getTitle());
        holder.descriptionTextView.setText(currentItem.getDescription());

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    // ViewHolder class
    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleTextView;
        TextView descriptionTextView;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.title);
            descriptionTextView = itemView.findViewById(R.id.desc);
        }
    }




}
