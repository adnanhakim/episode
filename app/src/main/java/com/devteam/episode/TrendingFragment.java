package com.devteam.episode;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
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

import static android.graphics.Color.parseColor;

public class TrendingFragment extends Fragment {

    // URL
    private String URL = "https://api.themoviedb.org/3/trending/tv/day?api_key=7f1c5b6bcdc0417095c1df13c485f647";
    private final String IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private JsonObjectRequest request, searchRequest;
    private RequestQueue requestQueue;

    // UI Elements
    private Toolbar toolbar;
    private View view;
    private TextView tvHeader;
    private RecyclerView recyclerView, searchRecyclerView;
    private MainAdapter adapter, searchAdapter;
    private LinearLayoutManager layoutManager;


    // Variables
    private static final String TAG = "TrendingFragment";
    private List<TVSeries> tvSeries, searchItems;
    private Boolean isScrolling = false;
    private int currentPage = 0, totalPages = 99999;
    private int visibleItems, totalItems, scrolledOutItems;


    public TrendingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.trending_fragment, container, false);
        setHasOptionsMenu(true);

        // Init
        toolbar = view.findViewById(R.id.toolbarTrending);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        tvHeader = view.findViewById(R.id.tvTrendingHeader);
        recyclerView = view.findViewById(R.id.trendingRecyclerView);
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);

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
        MainActivity.bottomNavigationView.getMenu().getItem(1).setIcon(null);
        MainActivity.trendingProgressBar.setVisibility(View.VISIBLE);
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

                    MainActivity.trendingProgressBar.setVisibility(View.INVISIBLE);
                    MainActivity.bottomNavigationView.getMenu().getItem(1).setIcon(R.drawable.ic_trending);

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
                    MainActivity.trendingProgressBar.setVisibility(View.INVISIBLE);
                    MainActivity.bottomNavigationView.getMenu().getItem(1).setIcon(R.drawable.ic_trending);

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
                    MainActivity.bottomNavigationView.getMenu().getItem(1).setIcon(null);
                    MainActivity.trendingProgressBar.setVisibility(View.VISIBLE);
                    MainActivity.trendingProgressBar.isIndeterminate();
                    MainActivity.trendingProgressBar.setFadingEdgeLength(100);
                    /*
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.isIndeterminate();
                    progressBar.setFadingEdgeLength(100);
                    */
                    isScrolling = false;
                    getJSONRequest();
                    adapter.notifyItemRangeInserted(adapter.getItemCount(), tvSeries.size() - 1);
                }
            }
        });
        //progressBar.setVisibility(View.GONE);
        MainActivity.trendingProgressBar.setVisibility(View.INVISIBLE);
        MainActivity.bottomNavigationView.getMenu().getItem(1).setIcon(R.drawable.ic_trending);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_search, menu);

        MenuItem item = menu.findItem(R.id.menuMainSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search for a show");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    searchRecyclerView.setVisibility(View.VISIBLE);
                    searchShow(newText);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    searchRecyclerView.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.GONE);
                MainActivity.trendingProgressBar.setVisibility(View.GONE);
                tvHeader.setVisibility(View.INVISIBLE);
                MainActivity.bottomNavigationView.setVisibility(View.GONE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tvHeader.setVisibility(View.VISIBLE);
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchShow(String newText) {
        // Search query
        // https://api.themoviedb.org/3/search/tv?api_key=7f1c5b6bcdc0417095c1df13c485f647&language=en-US&query=The%20flash&page=1
        // Base url + /search/movie?api_key= + api key + &query= + query + &page=1
        // Every space must be replaced by %20
        searchItems = new ArrayList<>();
        String query = newText.replace(" ", "%20");
        String url = "https://api.themoviedb.org/3/search/tv?api_key=7f1c5b6bcdc0417095c1df13c485f647&language=en-US&query="
                + query + "&page=1";

        searchRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject tvShow = results.getJSONObject(i);
                        int id = tvShow.getInt("id");
                        String title = tvShow.getString("original_name");
                        String overview = tvShow.getString("overview");
                        String imageURL = IMAGE_URL + tvShow.getString("poster_path");

                        TVSeries tvSeries = new TVSeries(id, title, overview, imageURL);
                        searchItems.add(tvSeries);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setUpSearchRecyclerView(searchItems);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        //requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(searchRequest);
    }

    private void setUpSearchRecyclerView(List<TVSeries> searchItems) {
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new MainAdapter(searchItems, getContext());
        searchRecyclerView.setAdapter(searchAdapter);
    }
}
