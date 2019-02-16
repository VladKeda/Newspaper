package com.example.vlad.newspaper;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlad.newspaper.network.NewsFetcher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewsListFragment extends Fragment {
    private static final String DATE_FORMAT = "EEE, MMM dd, yyyy hh:mm a";
    private List<News> newsList;
    private RecyclerView recyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        boolean isConnected = ((ConnectivityManager) getActivity()
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo() != null;

        if (isConnected) {
            new FetchNewsTask().execute();
        } else {
            Toast.makeText(getActivity(), R.string.internet_connection_error, Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.news_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();

        return view;
    }

    private void setupAdapter() {
        if (newsList == null) newsList = new ArrayList<>();
        recyclerView.setAdapter(new NewsAdapter(newsList));
    }

    private class FetchNewsTask extends AsyncTask<Void,Void,List<News>> {
        @Override
        protected List<News> doInBackground(Void... voids) {
            return new NewsFetcher().fetchNewsItems();
        }

        @Override
        protected void onPostExecute(List<News> news) {
            newsList = news;
            setupAdapter();
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
        private List<News> newsList;

        public NewsAdapter(List<News> newsList) {
            this.newsList = newsList;
        }

        @NonNull
        @Override
        public NewsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new NewsHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsHolder newsHolder, int i) {
            newsHolder.bind(newsList.get(i));
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

        public List<News> getNewsList() {
            return newsList;
        }

        public void setNewsList(List<News> newsList) {
            this.newsList = newsList;
        }
    }

    private class NewsHolder extends RecyclerView.ViewHolder
                            implements View.OnClickListener {
        private News news;
        private TextView titleTextView;
        private TextView sourceTextView;
        private TextView dateTextView;
        private ImageView newsImageView;


        public NewsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_news, parent, false));
            itemView.setOnClickListener(this);

            titleTextView = (TextView) itemView.findViewById(R.id.news_title);
            sourceTextView = (TextView) itemView.findViewById(R.id.news_source);
            dateTextView = (TextView) itemView.findViewById(R.id.news_date);
            newsImageView = (ImageView) itemView.findViewById(R.id.news_image);
        }

        public void bind(News news) {
            this.news = news;
            titleTextView.setText(this.news.getTitle());
            sourceTextView.setText(this.news.getSourceName());
            dateTextView.setText(DateFormat.format("yyyy-MM-dd HH:mm", this.news.getDate()));

            Picasso.get()
                    .load(news.getUrlToImage())
                    .fit()
                    .into(newsImageView);
        }

        @Override
        public void onClick(View v) {
            Intent intent = NewsDetailActivity.newIntent(getActivity(), news.getUrl());
            startActivity(intent);
        }
    }

}
