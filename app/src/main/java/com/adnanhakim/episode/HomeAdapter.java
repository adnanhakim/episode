package com.adnanhakim.episode;

import android.content.Context;
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

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private List<Home> homeList;
    private Context context;
    private RequestOptions requestOptions;

    public HomeAdapter(List<Home> homeList, Context context) {
        this.homeList = homeList;
        this.context = context;
        requestOptions = new RequestOptions().centerCrop();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.home_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Home home = homeList.get(position);

        holder.episodeName.setText(home.getEpisodeName());
        holder.showName.setText(home.getShowName());
        holder.airDate.setText(home.getAirDate());
        if(home.getEpisodeNo() < 10 && home.getSeasonNo() >= 10)
            holder.seasonEpisode.setText("S" + home.getSeasonNo() + " | E0" + home.getEpisodeNo());
        else if(home.getEpisodeNo() >= 10 && home.getSeasonNo() < 10)
            holder.seasonEpisode.setText("S0" + home.getSeasonNo() + " | E" + home.getEpisodeNo());
        else if(home.getEpisodeNo() < 10 && home.getSeasonNo() < 10)
            holder.seasonEpisode.setText("S0" + home.getSeasonNo() + " | E0" + home.getEpisodeNo());
        else
            holder.seasonEpisode.setText("S" + home.getSeasonNo() + " | E" + home.getEpisodeNo());

        Glide.with(context).load(home.getBackdropPath()).apply(requestOptions).into(holder.backDrop);
    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView episodeName, showName, seasonEpisode, airDate;
        public ImageView backDrop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            episodeName = itemView.findViewById(R.id.tvEpisodeName);
            showName = itemView.findViewById(R.id.tvShowName);
            seasonEpisode = itemView.findViewById(R.id.tvSeasonEpisode);
            backDrop = itemView.findViewById(R.id.ivHomeBackdrop);
            airDate = itemView.findViewById(R.id.tvAirDate);

            //Poster Corners
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                backDrop.setClipToOutline(true);
            }

        }
    }
}
