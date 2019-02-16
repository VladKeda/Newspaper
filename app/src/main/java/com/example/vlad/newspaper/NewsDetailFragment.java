package com.example.vlad.newspaper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class NewsDetailFragment extends Fragment {
    private static final String ARG_URL = "urlToNews";
    private WebView newsWebView;
    private ProgressBar newsPB;

    public static NewsDetailFragment newInstance(String urlSpec) {
        Bundle args = new Bundle();
        args.putString(ARG_URL, urlSpec);

        NewsDetailFragment detailFragment = new NewsDetailFragment();
        detailFragment.setArguments(args);
        return detailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);

        newsPB = (ProgressBar) view.findViewById(R.id.news_progress_horizontal);
        newsPB.setMax(100);

        newsWebView = (WebView) view.findViewById(R.id.news_web_view);
        newsWebView.getSettings().setJavaScriptEnabled(true);
        newsWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    newsPB.setVisibility(View.GONE);
                } else {
                    newsPB.setVisibility(View.VISIBLE);
                    newsPB.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportActionBar().setSubtitle(title);
            }
        });
        newsWebView.setWebViewClient(new WebViewClient());
        newsWebView.loadUrl(getArguments().getString(ARG_URL));

        return view;
    }


}
