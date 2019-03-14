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

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    private List<Cast> castList;
    private Context context;
    private RequestOptions requestOptions;
    private String url = "https://en.wikipedia.org/wiki/";

    public CastAdapter(List<Cast> castList, Context context) {
        this.castList = castList;
        this.context = context;
        requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.loading_screen).error(R.drawable.loading_screen);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cast_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Cast cast = castList.get(position);

        holder.tvName.setText(cast.getName());
        holder.tvCharacter.setText(cast.getCharacter());

        Glide.with(context).load(cast.getImageURL()).apply(requestOptions).into(holder.ivPhoto);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("HEADER", cast.getName());
                intent.putExtra("URL", url);
                v.getContext().startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
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
