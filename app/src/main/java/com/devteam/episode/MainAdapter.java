package com.devteam.episode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<TVSeries> tvSeries;
    private List<Integer> favouritesId;
    private Context context;
    private RequestOptions requestOptions;

    public MainAdapter(List<TVSeries> tvSeries, Context context) {
        this.tvSeries = tvSeries;
        this.context = context;
        requestOptions = new RequestOptions().centerCrop()
              .placeholder(R.drawable.loading_screen).error(R.drawable.loading_screen);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tv_item_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TVSeries tv = tvSeries.get(position);

        holder.tvTitle.setText(tv.getTitle());
        holder.tvOverview.setText(tv.getOverview());

        Glide.with(context).load(tv.getImageURL()).apply(requestOptions).into(holder.ivPoster);

        // On click listener for opening movie details page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFavourited = false;

                for(int i =0; i<SplashScreenActivity.favouritesList.size(); i++) {
                    if(SplashScreenActivity.favouritesList.get(i).getId() == tv.getId()) {
                        isFavourited = true;
                        break;
                    }
                }

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("ID", tv.getId());
                intent.putExtra("TITLE", tv.getTitle());
                intent.putExtra("BOOLEAN", isFavourited);
                intent.putExtra("ACTIVITY", "MAIN");
                v.getContext().startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        /*
        // On click listener for adding to favourites
        holder.ivPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv.isFavourite() == false) {
                    MainActivity.favouritesList.add(tv);
                    tv.setFavourite(true);
                    Toast.makeText(context, tv.getTitle() + " added to favourites", Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.favouritesList.remove(tv);
                    tv.setFavourite(false);
                    Toast.makeText(context, tv.getTitle() + " removed from favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        return tvSeries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTitle, tvOverview;
        public ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvCardTitle);
            tvOverview = itemView.findViewById(R.id.tvCardOverview);
            ivPoster = itemView.findViewById(R.id.ivCardPoster);

            //Poster Corners
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivPoster.setClipToOutline(true);
            }
        }
    }
}
