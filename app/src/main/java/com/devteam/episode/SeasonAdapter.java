package com.devteam.episode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.ViewHolder> {

    private List<Season> seasons;
    private Context context;
    private RequestOptions requestOptions;

    public SeasonAdapter(List<Season> seasons, Context context) {
        this.seasons = seasons;
        this.context = context;
        requestOptions = new RequestOptions().centerCrop()
                .placeholder(R.drawable.loading_screen).error(R.drawable.loading_screen);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.tv_season_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Season season = seasons.get(position);

        holder.tvTitle.setText(season.getSeasonTitle());
        holder.tvDate.setText("Air date: " + season.getSeasonDate());
        holder.tvEpisodes.setText("No of episodes: " + season.getSeasonEpisodes());

        Glide.with(context).load(season.getSeasonImageURL()).apply(requestOptions).into(holder.ivPoster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SeasonAdapter", "onClick: TVID: " + season.getTvId());
                Log.d("SeasonAdapter", "onClick: Season No: " + season.getSeasonNo());
                Intent intent = new Intent(context, EpisodeActivity.class);
                intent.putExtra("SEASONTITLE", season.getSeasonTitle());
                intent.putExtra("SEASONNO", season.getSeasonNo());
                intent.putExtra("TVID", season.getTvId());
                intent.putExtra("ACTIVITY", "SEASON");
                v.getContext().startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    @Override
    public int getItemCount() {
        return seasons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPoster;
        public TextView tvTitle, tvDate, tvEpisodes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPoster = itemView.findViewById(R.id.ivSeasonPoster);
            tvTitle = itemView.findViewById(R.id.tvSeasonTitle);
            tvDate = itemView.findViewById(R.id.tvSeasonAired);
            tvEpisodes = itemView.findViewById(R.id.tvSeasonEpisodes);

            //Poster Corners
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivPoster.setClipToOutline(true);
            }
        }
    }
}
