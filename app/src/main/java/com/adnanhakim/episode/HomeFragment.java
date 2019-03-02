package com.adnanhakim.episode;

import android.content.Intent;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    private void getJSONRequest() {
        //Log.d(TAG, "" + SplashScreenActivity.favouritesId.size());
        Log.d(TAG, "getJSONRequest: Checking for null pointer");
        requestQueue = Volley.newRequestQueue(getActivity());
        for (int i = 0; i < SplashScreenActivity.favouritesList.size(); i++) {
            int tvId = SplashScreenActivity.favouritesId.get(i);
            String URL = BASE_URL + tvId + REMAINING_URL;
            Log.d(TAG, "getDetails: Requesting details for URL: " + URL);
            homeRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "onResponse: Details found");
                        String backdropURL = IMAGE_URL + response.getString("backdrop_path");
                        String showName = response.getString("name");

                        // To get networks
                        String networks = "";
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
                        String episodeName = nextEpisodeToAir.getString("name");
                        int episodeNo = nextEpisodeToAir.getInt("episode_number");
                        int seasonNo = nextEpisodeToAir.getInt("season_number");
                        String airDate = nextEpisodeToAir.getString("air_date");

                        Home home = new Home(seasonNo, episodeNo, showName, networks, episodeName, backdropURL, getDateDifference(airDate));
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

        Collections.sort(homeList, new Comparator<Home>() {
            @Override
            public int compare(Home o1, Home o2) {
                return o1.getAirDate().compareTo(o2.getAirDate());
            }
        });

        homeRecyclerView.setAdapter(homeAdapter);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // Date Format: MM/dd/yyyy
    private static String changeDateFormat(String oldDate) {
        String oldDateFormat = "yyyy-MM-dd";
        String newDateFormat = "MM/dd/yyyy";
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

    private String getDateDifference(String airDate) {
        String dayDifference = "";
        int dayDifferenceInt;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            Date date = new Date();


            //Dates to compare
            String CurrentDate =  formatter.format(date);
            String FinalDate = changeDateFormat(airDate);

            Date date1;
            Date date2;

            SimpleDateFormat dates = new SimpleDateFormat("MM/dd/yyyy");

            //Setting dates
            date1 = dates.parse(CurrentDate);
            date2 = dates.parse(FinalDate);

            //Comparing dates
            long difference = Math.abs(date1.getTime() - date2.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);

            //Convert long to String
            dayDifference = Long.toString(differenceDates);

            Log.e("HERE","HERE: " + dayDifference);

        } catch (Exception exception) {
            Log.e("DIDN'T WORK", "exception " + exception);
        }
        /*
        dayDifferenceInt = Integer.parseInt(dayDifference);
        if(dayDifferenceInt == 0)
            return "Today";
        else if(dayDifferenceInt == 1)
            return "Tomorrow";
        else
            return (dayDifference + " Days");
        */
        return dayDifference;
    }

}
