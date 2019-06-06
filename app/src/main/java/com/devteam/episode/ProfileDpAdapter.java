package com.devteam.episode;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ProfileDpAdapter extends RecyclerView.Adapter<ProfileDpAdapter.ViewHolder> {

    Context context;
    List<ProfileDp> profileDpList;
    RequestOptions requestOptions;

    public ProfileDpAdapter(Context context, List<ProfileDp> profileDpList) {
        this.context = context;
        this.profileDpList = profileDpList;
        requestOptions = new RequestOptions().centerCrop()
                .placeholder(R.drawable.loading_screen).error(R.drawable.loading_screen);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind image here
        Log.d("Adapter: ", "onBindViewHolder: " + profileDpList.get(position).getImageUrl());
        Glide.with(context).load(profileDpList.get(position).getImageUrl()).apply(requestOptions).into(holder.ivProfileDp);
    }

    @Override
    public int getItemCount() {
        return profileDpList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivProfileDp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileDp = itemView.findViewById(R.id.ivProfileDpItem);
        }
    }
}
