package com.adnanhakim.episode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import transformer.FadeTransformer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EpisodeActivity extends AppCompatActivity {

    private static final String TAG = "EpisodeActivity";
    private ViewPager viewPager;
    private Toolbar toolbar;
    private TextView tvHeader;
    private EpisodeAdapter episodeAdapter;
    private List<Episode> episodes;

    //https://api.themoviedb.org/3/tv/69050/season/1?api_key=7f1c5b6bcdc0417095c1df13c485f647&language=en-US
    private final String BASE_URL = "https://api.themoviedb.org/3/tv/";
    private final String REMAINING_URL = "?api_key=7f1c5b6bcdc0417095c1df13c485f647&language=en-US";
    private final String IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private String seasonTitle, URL;
    private int tvId, seasonNo;

    private JsonObjectRequest objectRequest;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
        init();

        Intent intent = getIntent();
        seasonTitle = intent.getStringExtra("SEASONTITLE");
        seasonNo = intent.getIntExtra("SEASONNO", 999999);
        tvId = intent.getIntExtra("TVID", 999999);
        tvHeader.setText(seasonTitle);

        getEpisodeList();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbarEpisode);
        setSupportActionBar(toolbar);
        tvHeader = findViewById(R.id.tvEpisodeHeader);
        viewPager = findViewById(R.id.episodeViewPager);
    }

    private void getEpisodeList() {
        Log.d(TAG, "fillList: Filling list with episodes");
        episodes = new ArrayList<>();

        URL = BASE_URL + tvId + "/season/" + seasonNo + REMAINING_URL;
        objectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "onResponse: Episodes found");
                    JSONArray episodeArray = response.getJSONArray("episodes");
                    for (int i = 0; i < episodeArray.length(); i++) {
                        JSONObject object = episodeArray.getJSONObject(i);
                        int no = object.getInt("episode_number");
                        String date = object.getString("air_date");
                        String title = object.getString("name");
                        String overview = object.getString("overview");
                        int rating = object.getInt("vote_average");
                        String path = IMAGE_URL + object.getString("still_path");
                        Episode episode = new Episode(no, DetailActivity.formatDate(date), title, overview, rating, path);
                        episodes.add(episode);
                    }
                    setUpViewPager();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: Episodes not found");
                Log.e(TAG, "onErrorResponse: Error: " + error.toString());
            }
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);
    }

    private void setUpViewPager() {
        episodeAdapter = new EpisodeAdapter(episodes, EpisodeActivity.this);
        viewPager.setAdapter(episodeAdapter);
        viewPager.setPadding(130, 32, 130, 0);
        viewPager.setPageTransformer(true, new FadeTransformer());
        viewPager.setPageMargin(40);
        Log.d(TAG, "onCreate: List having " + episodes.size() + " results");
    }
}
