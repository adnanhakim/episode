package com.devteam.episode;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private final String REMAINING_URL = "?api_key=" + API_KEY + "&language=en-US";
    private final String IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private JsonObjectRequest detailsRequest, castRequest, externalIdsRequest;
    private RequestQueue requestQueue;

    // UI Elements
    private RelativeLayout relativeLayout, relativeDetails;
    private View view;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NestedScrollView nestedScrollView;
    private Typeface rubik;
    private TextView tvNetworks, tvOverview, tvStatus, tvGenres, tvRuntime;
    private TextView tvImdb, tvFacebook, tvInstagram, tvTwitter;
    private ImageView ivPoster, ivBackdrop;
    private RecyclerView seasonRecycler, castRecycler;
    private SeasonAdapter seasonAdapter;
    private CastAdapter castAdapter;
    private ImageButton ibFavourites;

    // Variables
    public static final String TAG = "DetailsActivity: ";
    private boolean isFavourited;
    private int seriesId;
    private String seriesTitle, intentActivity;
    private List<Season> seasonList;
    private List<Cast> castList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);

        initialize();

        // To get movie title and id from intent and set it to header
        final Intent intent = getIntent();
        seriesId = intent.getIntExtra("ID", 000000);
        seriesTitle = intent.getStringExtra("TITLE");
        isFavourited = intent.getBooleanExtra("BOOLEAN", false);
        intentActivity = intent.getStringExtra("ACTIVITY");

        setUpCollapsingToolbar(seriesTitle);
        //nestedScrollView.scrollTo(0, 0);
        seasonRecycler.setFocusable(false);
        castRecycler.setFocusable(false);

        seasonList = new ArrayList<>();
        castList = new ArrayList<>();
        getDetails(seriesId);
        getCast(seriesId);
        getExternalIds(seriesId);

        if (isFavourited == true) {
            ibFavourites.setImageResource(R.drawable.ic_favorite);
        } else {
            ibFavourites.setImageResource(R.drawable.ic_not_favorite);
        }

        // For favourites
        ibFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Favourite favourite = new Favourite(seriesId, seriesTitle, posterURL, backdropURL);
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                DatabaseReference databaseReference =
                        FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
                DatabaseReference favReference = databaseReference.child("favouriteList");
                if (isFavourited == false) {
                    // To add favourites
                    favReference.child("" + seriesId).setValue(favourite).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: " + favourite.getTitle() + " added to favourites");
                            Toast.makeText(DetailActivity.this, favourite.getTitle() + " added to favourites", Toast.LENGTH_SHORT).show();
                            SplashScreenActivity.favouritesList.add(favourite);
                            isFavourited = true;
                            ibFavourites.setImageResource(R.drawable.ic_favorite);

                            if (intentActivity.equals("PROFILE")) {
                                ProfileFragment.adapter.notifyDataSetChanged();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: Failed: " + e.getMessage());
                        }
                    });
                } else {
                    // Delete
                    favReference.child("" + favourite.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: " + favourite.getTitle() + " removed from favourites");
                            Toast.makeText(DetailActivity.this, favourite.getTitle() + " removed from favourites", Toast.LENGTH_SHORT).show();
                            MainActivity.updateFavourites(DetailActivity.this, firebaseAuth.getUid());

                            isFavourited = false;
                            ibFavourites.setImageResource(R.drawable.ic_not_favorite);

                            if (intentActivity.equals("PROFILE")) {
                                ProfileFragment.adapter.notifyDataSetChanged();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: Failed: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private void initialize() {
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarDetails);
        nestedScrollView = findViewById(R.id.nestedSVDetails);
        ivBackdrop = findViewById(R.id.ivDetailsBackDrop);
        tvNetworks = findViewById(R.id.tvDetailsNetwork);
        ibFavourites = findViewById(R.id.ibDetailsFavourite);
        tvStatus = findViewById(R.id.tvDetailsStatus);
        tvOverview = findViewById(R.id.tvDetailsOverview);
        tvGenres = findViewById(R.id.tvDetailsGenres);
        tvRuntime = findViewById(R.id.tvDetailsRuntime);
        tvImdb = findViewById(R.id.tvDetailsImdb);
        tvFacebook = findViewById(R.id.tvDetailsFacebook);
        tvInstagram = findViewById(R.id.tvDetailsInstagram);
        tvTwitter = findViewById(R.id.tvDetailsTwitter);
        seasonRecycler = findViewById(R.id.seasonRecyclerView);
        castRecycler = findViewById(R.id.castRecyclerView);
        relativeLayout = findViewById(R.id.relativeMain);
        relativeDetails = findViewById(R.id.relativeDetails);
        relativeLayout.setVisibility(View.INVISIBLE);

        // Initialize typefaces
        rubik = ResourcesCompat.getFont(this, R.font.rubik_regular);

        // Initialize Volley
        requestQueue = Volley.newRequestQueue(this);

        relativeDetails.setFocusable(false);
    }

    private void setUpCollapsingToolbar(String title) {
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleTypeface(rubik);
        collapsingToolbarLayout.setCollapsedTitleTypeface(rubik);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorGold));
    }

    // UI Variables
    private String backdropURL, posterURL, networks = "", overview, status, genres = "", runtime;
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
                    overview = response.getString("overview");
                    status = response.getString("status");

                    // To get runtime
                    JSONArray runtimeArray = response.getJSONArray("episode_run_time");
                    runtime = runtimeArray.get(0) + " minutes";

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

                    // To get genres
                    JSONArray genreArray = response.getJSONArray("genres");
                    for (int i = 0; i < genreArray.length(); i++) {
                        JSONObject genreObject = genreArray.getJSONObject(i);
                        if (i == 0) {
                            genres = genres + genreObject.getString("name");
                        } else {
                            genres = genres + ", " + genreObject.getString("name");
                        }
                    }

                    // To get season details
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
                setUpSeasonRecyclerView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: Error: " + error.toString());
            }
        });

        requestQueue.add(detailsRequest);
    }

    private void setUpSeasonRecyclerView() {
        Log.d(TAG, "setUpSeasonRecyclerView: There are " + seasonList.size() + " seasons");
        seasonAdapter = new SeasonAdapter(seasonList, DetailActivity.this);
        seasonRecycler.setAdapter(seasonAdapter);
        seasonRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fillDetails() {
        tvNetworks.setText(networks);
        tvOverview.setText(overview);
        tvStatus.setText(status);
        tvGenres.setText(genres);
        tvRuntime.setText(runtime);

        RequestOptions option = new RequestOptions().centerCrop();
        Glide.with(DetailActivity.this).load(backdropURL).apply(option).into(ivBackdrop);

        relativeLayout.setVisibility(View.VISIBLE);
    }

    private void getCast(int seriesId) {
        tvId = seriesId;
        String url = "https://api.themoviedb.org/3/tv/" + seriesId + "/credits?api_key=" + API_KEY + "&language=en-US";
        Log.d(TAG, "getCast: Requesting cast details...");
        castRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "onResponse: Got cast");
                    JSONArray castArray = response.getJSONArray("cast");
                    for (int i = 0; i < castArray.length(); i++) {
                        JSONObject castObject = castArray.getJSONObject(i);
                        String name = castObject.getString("name");
                        String character = castObject.getString("character");
                        String imageURL = IMAGE_URL + castObject.getString("profile_path");
                        Cast cast = new Cast(name, character, imageURL);
                        castList.add(cast);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: JSONException: " + e.getMessage());
                }
                setUpCastRecyclerView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: Cast not received: " + error.toString());
            }
        });
        requestQueue.add(castRequest);
    }

    private void setUpCastRecyclerView() {
        Log.d(TAG, "setUpCastRecyclerView: There are " + castList.size() + " casts");
        castAdapter = new CastAdapter(castList, DetailActivity.this);
        castRecycler.setAdapter(castAdapter);
        castRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        castRecycler.setHasFixedSize(true);
    }

    // External ids
    String imdbId, facebookId, instagramId, twitterId;
    String IMDB_URL = "https://www.imdb.com/title/";
    String FACEBOOK_URL = "https://www.facebook.com/";
    String INSTAGRAM_URL = "https://www.instagram.com/";
    String TWITTER_URL = "https://twitter.com/";

    private void getExternalIds(int seriesId) {
        Log.d(TAG, "getExternalIds: Requesting external ids...");
        String url = BASE_URL + seriesId + "/external_ids" + REMAINING_URL;
        externalIdsRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "onResponse: Got external ids");
                    imdbId = response.getString("imdb_id");
                    facebookId = response.getString("facebook_id");
                    instagramId = response.getString("instagram_id");
                    twitterId = response.getString("twitter_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setExternalIds();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: Error: " + error.toString());
            }
        });
        requestQueue.add(externalIdsRequest);
    }

    private void setExternalIds() {
        // IMDb
        if (!imdbId.equals(null)) {
            final String url = IMDB_URL + imdbId;
            tvImdb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        } else {
            tvImdb.setVisibility(View.GONE);
        }

        // Facebook
        if (!facebookId.equals(null)) {
            final String url = checkForFacebookUrl(this) + facebookId;
            tvFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        } else {
            tvFacebook.setVisibility(View.GONE);
        }

        // Instagram
        if (!instagramId.equals(null)) {
            final String url = INSTAGRAM_URL + instagramId;
            tvInstagram.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        } else {
            tvInstagram.setVisibility(View.GONE);
        }

        // Twitter
        if (!twitterId.equals(null)) {
            final String url = TWITTER_URL + twitterId;
            tvTwitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        } else {
            tvTwitter.setVisibility(View.GONE);
        }
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

    public String checkForFacebookUrl(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", PackageManager.GET_ACTIVITIES).versionCode;

            boolean activated = packageManager.getApplicationInfo("com.facebook.katana", 0).enabled;

            if (activated) {
                if(versionCode >= 3002850)
                    return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
                else
                    return "fb://page/";
            }
            else
                return FACEBOOK_URL;

        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetailActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onBackPressed();
        this.finish();
    }
}
