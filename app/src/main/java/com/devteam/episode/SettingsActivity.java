package com.devteam.episode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvEditDp, tvNightMode;
    private Switch switchNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        init();

        switchNightMode.setChecked(false);
        switchNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(SettingsActivity.this, "Night Mode coming soon!", Toast.LENGTH_SHORT).show();
                    switchNightMode.setChecked(false);
                }
            }
        });
    }

    private void init() {
        tvEditDp = findViewById(R.id.tvSettingsEditDp);
        tvNightMode = findViewById(R.id.tvSettingsNightMode);
        switchNightMode = findViewById(R.id.switchSettingsNightMode);
    }
}
