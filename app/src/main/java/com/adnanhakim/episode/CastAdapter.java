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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    private List<Cast> castList;
    private Context context;
    private RequestOptions requestOptions;

    public CastAdapter(List<Cast> castList, Context context) {
        this.castList = castList;
        this.context = context;
        requestOptions = new RequestOptions().centerCrop();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cast_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cast cast = castList.get(position);

        holder.tvName.setText(cast.getName());
        holder.tvCharacter.setText(cast.getCharacter());

        Glide.with(context).load(cast.getImageURL()).apply(requestOptions).into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvCharacter;
        public ImageView ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvCastName);
            tvCharacter = itemView.findViewById(R.id.tvCastCharacter);
            ivPhoto = itemView.findViewById(R.id.ivCastPhoto);

            //Poster Corners
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivPhoto.setClipToOutline(true);
            }
        }
    }
}
