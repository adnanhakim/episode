package com.devteam.episode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

public class WebActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton ibClose;
    private TextView tvHeader;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        init();

        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String header = intent.getStringExtra("HEADER");
        String baseUrl = intent.getStringExtra("URL");

        webView.loadUrl(baseUrl + header);
    }

    private void init() {
        toolbar = findViewById(R.id.toolbarWeb);
        ibClose = findViewById(R.id.ibWebClose);
        tvHeader = findViewById(R.id.tvWebHeader);
        webView = findViewById(R.id.webView);
    }
}
