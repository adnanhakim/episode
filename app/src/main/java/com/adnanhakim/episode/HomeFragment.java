package com.adnanhakim.episode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    // URL
    //private String URL = "https://api.themoviedb.org/3/trending/tv/day?api_key=7f1c5b6bcdc0417095c1df13c485f647";
    private final String BASE_URL = "https://api.themoviedb.org/3/tv/";
    private final String REMAINING_URL = "?api_key=7f1c5b6bcdc0417095c1df13c485f647&language=en-US";
    private final String IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private JsonObjectRequest homeRequest;
    private RequestQueue requestQueue;

    // UI Elements
    private View view;
    private RecyclerView homeRecyclerView;
    private HomeAdapter homeAdapter;
    private LinearLayoutManager layoutManager;

    //Variables
    public static final String TAG = "HomeFragment";
    private List<Home> homeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);



        homeRecyclerView = view.findViewById(R.id.homeRecyclerView);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeList = new ArrayList<>();
        getJSONRequest();
    }

    private String backdropURL, showName, episodeName, networks = "";
    private int episodeNo, seasonNo;
    int tvId;

    private void getJSONRequest() {
        Log.d(TAG, "" + MainActivity.favouritesId.size());
        requestQueue = Volley.newRequestQueue(getActivity());
        for (int i = 0; i < MainActivity.favouritesId.size(); i++) {
            tvId = MainActivity.favouritesId.get(i);
            String URL = BASE_URL + tvId + REMAINING_URL;
            Log.d(TAG, "getDetails: Requesting details for URL: " + URL);
            homeRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "onResponse: Details found");
                        backdropURL = IMAGE_URL + response.getString("backdrop_path");
                        showName = response.getString("name");

                        // To get networks
                        JSONArray networkArray = response.getJSONArray("networks");
                        for (int i = 0; i < networkArray.length(); i++) {
                            JSONObject networkObject = networkArray.getJSONObject(i);
                            if (i == 0) {
                                networks = networks + networkObject.getString("name");
                            } else {
                                networks = networks + ", " + networkObject.getString("name");
                            }
                        }

                        // To get season details
                        JSONObject nextEpisodeToAir = response.getJSONObject("next_episode_to_air");
                        episodeName = nextEpisodeToAir.getString("name");
                        episodeNo = nextEpisodeToAir.getInt("episode_number");
                        seasonNo = nextEpisodeToAir.getInt("season_number");

                        Home home = new Home(seasonNo, episodeNo, showName, networks, episodeName, backdropURL);
                        homeList.add(home);

                    } catch (JSONException e) {
                        Log.e(TAG, "onResponse: Exception: " + e.getMessage());
                    }
                    setUpRecyclerView();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: Error: " + error.toString());
                }
            });
            requestQueue.add(homeRequest);
        }
    }

    private void setUpRecyclerView() {
        Log.d(TAG, "setUpRecyclerView: There are " + homeList.size() + " seasons");
        homeAdapter = new HomeAdapter(homeList, getActivity());
        homeRecyclerView.setAdapter(homeAdapter);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
