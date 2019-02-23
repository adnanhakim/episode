package com.adnanhakim.episode;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Favourites list
    public static List<TVSeries> favouritesList = new ArrayList<>();

    // URL
    public static final String API_KEY = "7f1c5b6bcdc0417095c1df13c485f647";

    // UI Elements
    private BottomNavigationView bottomNavigationView;

    // Variables
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        // To select trending fragment as main fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain,
                new TrendingFragment()).commit();
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        setUpBottomNavigation();
    }

    private void init() {
        bottomNavigationView = findViewById(R.id.bottomNavigationMain);
    }

    private void setUpBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                Fragment fragment = getSupportFragmentManager().findFragmentByTag("TRENDING");
                if(fragment != null && fragment.isVisible()) {
                    // Scroll to top of recycler view

                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.navHome:
                        selectedFragment = new HomeFragment();
                        transaction.replace(R.id.fragmentContainerMain, selectedFragment, "HOME");
                        break;
                    case R.id.navTrending:
                        selectedFragment = new TrendingFragment();
                        transaction.replace(R.id.fragmentContainerMain, selectedFragment, "TRENDING");
                        break;
                    case R.id.navProfile:
                        selectedFragment = new ProfileFragment();
                        transaction.replace(R.id.fragmentContainerMain, selectedFragment, "PROFILE");
                        break;
                }
                transaction.commit();
                return true;
            }
        });
    }
}
