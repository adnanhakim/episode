package com.devteam.episode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    private List<Favourite> favouriteList;
    private Context context;
    private RequestOptions requestOptions;

    public FavouriteAdapter(List<Favourite> favouriteList, Context context) {
        this.favouriteList = favouriteList;
        this.context = context;
        requestOptions = new RequestOptions().centerCrop()
                .placeholder(R.drawable.loading_screen).error(R.drawable.loading_screen);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favourite_shows_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String imageURL = favouriteList.get(position).getPosterURL();

        // Putting images into imageView
        Glide.with(context).load(imageURL).apply(requestOptions).into(holder.ivPoster);

        // onClick for opening respective DetailActivity for each poster
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("ID", favouriteList.get(position).getId());
                intent.putExtra("TITLE", favouriteList.get(position).getTitle());
                intent.putExtra("BOOLEAN", true);
                intent.putExtra("ACTIVITY", "PROFILE");
                v.getContext().startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPoster = itemView.findViewById(R.id.ivFavouritePoster);

            //Poster Corners
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivPoster.setClipToOutline(true);
            }
        }
    }
}
