package com.adnanhakim.episode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    // URL
    public static final String API_KEY = "7f1c5b6bcdc0417095c1df13c485f647";
    private final String BASE_URL = "https://api.themoviedb.org/3/tv/";
    private final String REMAINING_URL = "?api_key=7f1c5b6bcdc0417095c1df13c485f647&language=en-US";
    private final String IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private JsonObjectRequest detailsRequest;
    private RequestQueue requestQueue;

    // UI Elements
    private RelativeLayout relativeLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NestedScrollView nestedScrollView;
    private Typeface keepcalm;
    private TextView tvStatus, tvOverview;
    private ImageView ivPoster, ivBackdrop;
    private RecyclerView seasonRecycler;
    private SeasonAdapter seasonAdapter;

    // Variables
    public static final String TAG = "DetailsActivity: ";
    private int seriesId;
    private String seriesTitle;
    private List<Season> seasonList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initialize();

        // To get movie title and id from intent and set it to header
        Intent intent = getIntent();
        seriesId = intent.getIntExtra("ID", 000000);
        seriesTitle = intent.getStringExtra("TITLE");
        setUpCollapsingToolbar(seriesTitle);
        //nestedScrollView.scrollTo(0, 0);
        seasonRecycler.setFocusable(false);

        seasonList = new ArrayList<>();
        getDetails(seriesId);
    }

    private void initialize() {
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarDetails);
        nestedScrollView = findViewById(R.id.nestedSVDetails);
        ivBackdrop = findViewById(R.id.ivDetailsBackDrop);
        ivPoster = findViewById(R.id.ivDetailsPoster);
        tvStatus = findViewById(R.id.tvDetailsStatus);
        tvOverview = findViewById(R.id.tvDetailsOverview);
        seasonRecycler = findViewById(R.id.seasonRecyclerView);
        relativeLayout = findViewById(R.id.relativeMain);
        relativeLayout.setVisibility(View.INVISIBLE);

        // Initialize typefaces
        keepcalm = ResourcesCompat.getFont(this, R.font.keepcalm_font);

        // Initialize Volley
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setUpCollapsingToolbar(String title) {
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleTypeface(keepcalm);
        collapsingToolbarLayout.setCollapsedTitleTypeface(keepcalm);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorGold));
    }

    // UI Variables
    private String backdropURL, posterURL, status, overview;
    int tvId;

    private void getDetails(final int seriesId) {
        tvId = seriesId;
        String URL = BASE_URL + seriesId + REMAINING_URL;
        Log.d(TAG, "getDetails: Requesting details for URL: " + URL);
        detailsRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "onResponse: Details found");
                    backdropURL = IMAGE_URL + response.getString("backdrop_path");
                    posterURL = IMAGE_URL + response.getString("poster_path");
                    status = response.getString("status");
                    overview = response.getString("overview");

                    // To get season details
                    Log.d(TAG, "onResponse: Hello");
                    JSONArray seasons = response.getJSONArray("seasons");
                    for (int i = 0; i < seasons.length(); i++) {
                        JSONObject object = seasons.getJSONObject(i);
                        int id = object.getInt("id");
                        int episodes = object.getInt("episode_count");
                        String title = object.getString("name");
                        String date = object.getString("air_date");
                        String imageURL = IMAGE_URL + object.getString("poster_path");
                        int seasonNo = object.getInt("season_number");
                        Season season = new Season(tvId, id, episodes, title, formatDate(date), imageURL, seasonNo);
                        seasonList.add(season);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: Exception: " + e.getMessage());
                }
                fillDetails();
                setUpRecyclerView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: Error: " + error.toString());
            }
        });

        requestQueue.add(detailsRequest);
    }

    private void setUpRecyclerView() {
        Log.d(TAG, "setUpRecyclerView: There are " + seasonList.size() + " seasons");
        seasonAdapter = new SeasonAdapter(seasonList, DetailActivity.this);
        seasonRecycler.setAdapter(seasonAdapter);
        seasonRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fillDetails() {
        tvStatus.setText(status);
        tvOverview.setText(overview);

        RequestOptions option = new RequestOptions().centerCrop();
        Glide.with(DetailActivity.this).load(posterURL).apply(option).into(ivPoster);
        Glide.with(DetailActivity.this).load(backdropURL).apply(option).into(ivBackdrop);

        relativeLayout.setVisibility(View.VISIBLE);
    }

    public static String formatDate(String oldDate) {
        String oldDateFormat = "yyyy-MM-dd";
        String newDateFormat = "dd MMMM yyyy";
        String newDate;
        SimpleDateFormat sdf = new SimpleDateFormat(oldDateFormat);
        try {
            Date date = sdf.parse(oldDate);
            sdf.applyPattern(newDateFormat);
            newDate = sdf.format(date);
            return newDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
