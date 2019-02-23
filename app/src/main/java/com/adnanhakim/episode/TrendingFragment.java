package com.adnanhakim.episode;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class TrendingFragment extends Fragment {

    // URL
    private String URL = "https://api.themoviedb.org/3/trending/tv/day?api_key=7f1c5b6bcdc0417095c1df13c485f647";
    private final String IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private JsonObjectRequest request;
    private RequestQueue requestQueue;

    // UI Elements
    private View view;
    private RecyclerView recyclerView;
    private MainAdapter adapter;
    private LinearLayoutManager layoutManager;

    // Variables
    private static final String TAG = "TrendingFragment";
    private List<TVSeries> tvSeries;
    private Boolean isScrolling = false;
    private int currentPage = 0, totalPages = 99999;
    private int visibleItems, totalItems, scrolledOutItems;


    public TrendingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.trending_fragment, container, false);
        recyclerView = view.findViewById(R.id.trendingRecyclerView);
        adapter = new MainAdapter(tvSeries, getContext());
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvSeries = new ArrayList<>();
        getJSONRequest();
    }

    private void getJSONRequest() {
        Log.d(TAG, "getJSONRequest: Requesting JSON...");
        if (currentPage < totalPages || totalPages == 99999) {
            currentPage += 1;
            URL = URL + "&page=" + currentPage;
            Log.d(TAG, "getJSONRequest: Requesting page: " + currentPage);
            request = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "onResponse: Got results");
                        totalPages = response.getInt("total_pages");
                        JSONArray results = response.getJSONArray("results");

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject object = results.getJSONObject(i);
                            int id = object.getInt("id");
                            String title = object.getString("original_name");
                            String overview = object.getString("overview");
                            String poster_path = object.getString("poster_path");
                            TVSeries tv = new TVSeries(id, title, overview, IMAGE_URL + poster_path);
                            tvSeries.add(tv);
                        }
                        setUpRecyclerView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: Error: " + error.toString());
                }
            });
            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(request);
        } else {
            Log.d(TAG, "getJSONRequest: Reached the end");
            Toast.makeText(getActivity(), "That's all!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpRecyclerView() {
        Log.d(TAG, "setUpRecyclerView: List having " + tvSeries.size() + " results");
        adapter.notifyDataSetChanged();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItems = layoutManager.getChildCount();
                totalItems = layoutManager.getItemCount();
                scrolledOutItems = layoutManager.findFirstVisibleItemPosition();

                if (isScrolling == true && visibleItems + scrolledOutItems == totalItems) {
                    Log.d(TAG, "onScrolled: Fetching new data...");
                    isScrolling = false;
                    getJSONRequest();
                    adapter.notifyItemRangeInserted(adapter.getItemCount(), tvSeries.size() - 1);
                }
            }
        });
    }
}
