package com.devteam.episode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";

    // Variable
    private TextView tvSplashScreen;

    // List
    public static List<Favourite> favouritesList;

    // Firebase
    private FirebaseUser firebaseUser;

    // Animation
    private Animation animBlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        tvSplashScreen = findViewById(R.id.tvSplashScreen);

        // Animating TextView
        animBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        tvSplashScreen.startAnimation(animBlink);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Log.d(TAG, "onCreate: User is already logged in, retrieving favourites");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getFavourites(SplashScreenActivity.this, firebaseUser.getUid());
                    //Log.d(TAG, "run: Checking done = " + done.get());
                }
            }, 3000);
        } else {
            Log.d(TAG, "onCreate: User is not logged in, redirecting to RegisterActivity");
            startActivity(new Intent(SplashScreenActivity.this, RegisterActivity.class));
            this.finish();
        }
    }

    public void getFavourites(final Context context, String uid) {
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
                } else {
                    Log.d(TAG, "onDataChange: Favourites do not exist");
                }
                updateUI(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Database Error: " + databaseError.getMessage());
                Toast.makeText(context, "Database error :(", Toast.LENGTH_SHORT).show();
                updateUI(false);
            }
        });
    }

    private void updateUI(boolean message) {
        if (message == true) {
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        }
        this.finish();
    }

}
