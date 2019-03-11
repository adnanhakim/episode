package com.devteam.episode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class EpisodeAdapter extends PagerAdapter {

    private List<Episode> episodes;
    private Context context;
    private RequestOptions requestOptions;

    public EpisodeAdapter(List<Episode> episodes, Context context) {
        this.episodes = episodes;
        this.context = context;
        requestOptions = new RequestOptions().centerCrop()
                .placeholder(R.drawable.loading_screen).error(R.drawable.loading_screen);
    }

    @Override
    public int getCount() {
        return episodes.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.episode_item, container, false);

        ImageView ivPhoto;
        TextView tvTitle, tvDate, tvRating, tvInfo;
        ExpandableTextView etvOverview;

        ivPhoto = view.findViewById(R.id.ivEpisodePhoto);
        tvTitle = view.findViewById(R.id.tvEpisodeTitle);
        etvOverview = view.findViewById(R.id.etvEpisodeOverview);
        tvDate = view.findViewById(R.id.tvEpisodeDate);
        tvRating = view.findViewById(R.id.tvEpisodeRating);
        tvInfo = view.findViewById(R.id.tvEpisodeInfo);

        Episode episode = episodes.get(position);

        tvTitle.setText(episode.getTitle());
        etvOverview.setText(episode.getOverview());
        tvDate.setText(episode.getDate());
        tvRating.setText(String.valueOf(episode.getRating()));

        String seasonString, episodeString;
        int seasonNo = episode.getSeasonNo();
        int episodeNo = episode.getEpisodeNo();
        if (seasonNo < 10) {
            seasonString = "0" + String.valueOf(seasonNo);
        } else {
            seasonString = String.valueOf(seasonNo);
        }

        if (episodeNo < 10) {
            episodeString = "0" + String.valueOf(episodeNo);
        } else {
            episodeString = String.valueOf(episodeNo);
        }

        tvInfo.setText("S" + seasonString + " | E" + episodeString);
        Glide.with(context).load(episode.getImageURL()).apply(requestOptions).into(ivPhoto);

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


}
