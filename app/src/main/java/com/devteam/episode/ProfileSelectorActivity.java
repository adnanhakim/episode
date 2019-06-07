package com.devteam.episode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.database.AbstractWindowedCursor;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileSelectorActivity extends AppCompatActivity {

    private static final String TAG = "ProfileSelectorActivity";

    private RecyclerView recyclerView;
    private ProfileDpAdapter profileDpAdapter;
    private StaggeredGridLayoutManager manager;
    private List<ProfileDp> profileDps;

    // Firebase
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_selector);

        profileDps = new ArrayList<>();
        recyclerView = findViewById(R.id.profileDpRecyclerView);
        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        // Create List of Dps (Image Paths) from Firebase Storage
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ProfileDp");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Got Profile Dps");
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String imageUrl = postSnapshot.getValue(String.class);
                    ProfileDp profileDp = new ProfileDp(imageUrl);
                    profileDps.add(profileDp);
                }
                profileDpAdapter = new ProfileDpAdapter(ProfileSelectorActivity.this, profileDps);
                recyclerView.setAdapter(profileDpAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
