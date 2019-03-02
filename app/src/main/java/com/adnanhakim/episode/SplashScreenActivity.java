package com.adnanhakim.episode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";

    // List
    public static List<Favourite> favouritesList;
    public static List<Integer> favouritesId;

    // Firebase
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Log.d(TAG, "onCreate: User is already logged in, retrieving favourites");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AtomicBoolean done = getFavourites(SplashScreenActivity.this, firebaseUser.getUid());
                    Log.d(TAG, "run: Checking done = " + done.get());
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }
            }, 3000);
        } else {
            Log.d(TAG, "onCreate: User is not logged in, redirecting to RegisterActivity");
            startActivity(new Intent(SplashScreenActivity.this, RegisterActivity.class));
            this.finish();
        }
    }

    public static AtomicBoolean getFavourites(final Context context, String uid) {
        final AtomicBoolean done = new AtomicBoolean(false);
        //final CountDownLatch latch = new CountDownLatch(1);
        favouritesId = new ArrayList<>();
        favouritesList = new ArrayList<>();

        Log.d(TAG, "getFavourites: new Arraylist");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid).child("favouriteList");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: Favourites exists");
                    for (DataSnapshot list : dataSnapshot.getChildren()) {
                        favouritesList.add(list.getValue(Favourite.class));
                    }
                    for (int i = 0; i < favouritesList.size(); i++) {
                        favouritesId.add(favouritesList.get(i).getId());
                    }
                } else {
                    Log.d(TAG, "onDataChange: Favourites do not exist");
                }
                done.set(true);
                //latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Database Error: " + databaseError.getMessage());
                Toast.makeText(context, "Database error :(", Toast.LENGTH_SHORT).show();
                done.set(false);
            }
        });
        //try {
            //latch.await();

        return done;
    }
}
