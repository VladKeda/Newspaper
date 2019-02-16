package com.example.vlad.newspaper;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

import java.util.Objects;

public class NewsDetailActivity extends SingleFragmentActivity {
    private static final String EXTRA_URL = "com.example.vlad.newspaper.url";

    @Override
    protected Fragment createFragment() {
        String urlString = getIntent().getStringExtra(EXTRA_URL);
        return NewsDetailFragment.newInstance(urlString);
    }

    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    public void onBackPressed() {
        WebView newsDetailWV = findViewById(R.id.news_web_view);
        if (newsDetailWV != null && newsDetailWV.canGoBack()){
            newsDetailWV.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
