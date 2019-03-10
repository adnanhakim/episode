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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Home home = homeList.get(position);

        holder.episodeName.setText(home.getEpisodeName());
        holder.showName.setText(home.getShowName());

        if(home.getStatus().equals("aired")) {
            if (home.getAirDate().equals("001"))
                holder.airDate.setText("Yesterday");
            else
                holder.airDate.setText(home.getAirDateInt() + " Days Ago");
        }
        else {
            if (home.getAirDate().equals("000"))
                holder.airDate.setText("Today");
            else if (home.getAirDate().equals("001"))
                holder.airDate.setText("Tomorrow");
            else
                holder.airDate.setText(home.getAirDateInt() + " Days");
        }

        if(home.getEpisodeEpisodeNo() < 10 && home.getEpisodeSeasonNo() >= 10)
            holder.seasonEpisode.setText("S" + home.getEpisodeSeasonNo() + " | E0" + home.getEpisodeEpisodeNo());
        else if(home.getEpisodeEpisodeNo() >= 10 && home.getEpisodeSeasonNo() < 10)
            holder.seasonEpisode.setText("S0" + home.getEpisodeSeasonNo() + " | E" + home.getEpisodeEpisodeNo());
        else if(home.getEpisodeEpisodeNo() < 10 && home.getEpisodeSeasonNo() < 10)
            holder.seasonEpisode.setText("S0" + home.getEpisodeSeasonNo() + " | E0" + home.getEpisodeEpisodeNo());
        else
            holder.seasonEpisode.setText("S" + home.getEpisodeSeasonNo() + " | E" + home.getEpisodeEpisodeNo());

        Glide.with(context).load(home.getBackdropPath()).apply(requestOptions).into(holder.backDrop);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EpisodeActivity.class);
                intent.putExtra("SEASONTITLE", "Season " + home.getEpisodeSeasonNo());
                intent.putExtra("SEASONNO", home.getEpisodeSeasonNo());
                intent.putExtra("TVID", home.getShowId());
                intent.putExtra("EPISODENO", home.getEpisodeEpisodeNo());
                intent.putExtra("ACTIVITY", "HOME");
                v.getContext().startActivity(intent);

            }
        });
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
