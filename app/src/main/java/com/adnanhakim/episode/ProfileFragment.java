package com.adnanhakim.episode;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // UI Elements
    private View view;
    private ImageView ivProfileDp, ivProfileCover;
    private TextView tvProfileName, tvProfileFavourites;

    // Firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        init();
        getUserData();
        return view;
    }

    private void init() {
        ivProfileDp = view.findViewById(R.id.ivProfileDp);
        ivProfileCover = view.findViewById(R.id.ivProfileCover);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileFavourites = view.findViewById(R.id.tvProfileFavourites);

        // To make the image view circular
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivProfileDp.setClipToOutline(true);
        }
    }

    private void getUserData() {
        Log.d(TAG, "getUserData: Fetching user data...");
        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Successfully retrieved data");
                User user = dataSnapshot.getValue(User.class);
                tvProfileName.setText(user.getName());
                tvProfileFavourites.setText(user.getFavourites() + " Favourites");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Data not retrieved: " + databaseError.getMessage());
                Toast.makeText(getActivity(), "Data not retrieved", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
