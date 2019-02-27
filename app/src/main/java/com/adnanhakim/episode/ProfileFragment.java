package com.adnanhakim.episode;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private RequestOptions requestOptions;

    // UI Elements
    private View view;
    private ImageView ivProfileDp, ivProfileCover;
    private TextView tvProfileName, tvProfileNoFavs;
    private RecyclerView recyclerView;
    private Button btnLogout;

    // Adapters
    public static FavouriteAdapter adapter;

    // Firebase variables
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        init();
        getUserData();

        // To get random background as cover
        requestOptions = new RequestOptions().centerCrop();

        if (MainActivity.favouritesList.size() >= 2) {
            Glide.with(getContext()).load(MainActivity.favouritesList.get(getRandomBackdrop()).getBackdropURL())
                    .apply(requestOptions).into(ivProfileCover);
        }
        else if(MainActivity.favouritesList.size() == 1) {
            Glide.with(getContext()).load(MainActivity.favouritesList.get(0).getBackdropURL())
                    .apply(requestOptions).into(ivProfileCover);
        }

        setUpRecyclerView();

        firebaseAuth = FirebaseAuth.getInstance();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Logging out...");
                firebaseAuth.signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity(), RegisterActivity.class));
            }
        });

        return view;
    }

    private int getRandomBackdrop() {
        Random random = new Random();
        int size = MainActivity.favouritesList.size();
        return random.nextInt(size - 1);
    }

    private void init() {
        // Find view by ids
        ivProfileDp = view.findViewById(R.id.ivProfileDp);
        ivProfileCover = view.findViewById(R.id.ivProfileCover);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        recyclerView = view.findViewById(R.id.favouriteRecyclerView);
        btnLogout = view.findViewById(R.id.btnProfileLogout);
        tvProfileNoFavs = view.findViewById(R.id.tvProfileNoFavourite);

        // To hide all data until data is fetched
        tvProfileName.setVisibility(View.INVISIBLE);

        // To make the image view circular
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivProfileDp.setClipToOutline(true);
        }
    }

    private void getUserData() {
        Log.d(TAG, "getUserData: Fetching user data...");
        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Successfully retrieved data");
                User user = dataSnapshot.getValue(User.class);
                tvProfileName.setText(user.getName());
                tvProfileName.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Data not retrieved: " + databaseError.getMessage());
                Toast.makeText(getActivity(), "Data not retrieved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRecyclerView() {
        Log.d(TAG, "setUpRecyclerView: Setting up recycler view...");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        adapter = new FavouriteAdapter(MainActivity.favouritesList, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        if (MainActivity.favouritesList.size() == 0) {
            tvProfileNoFavs.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            tvProfileNoFavs.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


}
