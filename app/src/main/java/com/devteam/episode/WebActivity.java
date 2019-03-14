package com.devteam.episode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

        tvHeader.setText(header);

        webView.getSettings().setJavaScriptEnabled(true);

        //Enabling zoom-in controls
        webView.getSettings().setSupportZoom(true);

        webView.loadUrl(baseUrl + header);

        webView.setWebViewClient(new WebViewController());

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbarWeb);
        ibClose = findViewById(R.id.ibWebClose);
        tvHeader = findViewById(R.id.tvWebHeader);
        webView = findViewById(R.id.webView);
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            finish();
            webView.goBack();
        } else {
            finish();
            super.onBackPressed();
        }
    }
}
