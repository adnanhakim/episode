package com.devteam.episode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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
        requestOptions = new RequestOptions().centerCrop();
               // .placeholder(R.drawable.loading).error(R.drawable.loading);
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
        TextView tvTitle, tvOverview, tvDate, tvRating;

        ivPhoto = view.findViewById(R.id.ivEpisodePhoto);
        tvTitle = view.findViewById(R.id.tvEpisodeTitle);
        tvOverview = view.findViewById(R.id.tvEpisodeOverview);
        tvDate = view.findViewById(R.id.tvEpisodeDate);
        tvRating = view.findViewById(R.id.tvEpisodeRating);

        tvTitle.setText(episodes.get(position).getNo() + ": " + episodes.get(position).getTitle());
        tvOverview.setText(episodes.get(position).getOverview());
        tvDate.setText(episodes.get(position).getDate());
        tvRating.setText(String.valueOf(episodes.get(position).getRating()));
        Glide.with(context).load(episodes.get(position).getImageURL()).apply(requestOptions).into(ivPhoto);

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


}
