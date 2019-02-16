package com.example.vlad.newspaper;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
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
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    private int numPage;
    private boolean isLoading;
    private List<News> newsList;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        numPage = 1;
        newsList = new ArrayList<>();

        boolean isConnected = ((ConnectivityManager) getActivity()
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo() != null;

        if (isConnected) {
            new FetchNewsTask().execute(String.valueOf(numPage));
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        isLoading = true;
                        Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
                                .show();
                        new FetchNewsTask().execute(String.valueOf(numPage));
                    }
                }
            }
        });

        updateUI();
        return view;
    }

    private void updateUI() {
        if (adapter == null) {
            adapter = new NewsAdapter(newsList);
        } else {
            adapter.notifyDataSetChanged();
        }
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        }

        updateSubtitle();
    }

    private void updateSubtitle() {
        if (newsList != null) {
            int count = newsList.size();
            String subtitle = "Count: ";
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setSubtitle(subtitle + count);
        }
    }

    private class FetchNewsTask extends AsyncTask<String,Void,List<News>> {
        @Override
        protected List<News> doInBackground(String... strings) {
            int page = Integer.valueOf(strings[0]);
            return new NewsFetcher().fetchNewsItems(page);
        }

        @Override
        protected void onPostExecute(List<News> newsL) {
            isLoading = false;
            if (newsL != null && newsL.size() > 0) {
                numPage++;
                newsList.addAll(newsL);
                updateUI();
            } else {
                Toast.makeText(getActivity(), "There are no news any more", Toast.LENGTH_SHORT)
                        .show();
            }
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
            dateTextView.setText(DateFormat.format(DATE_FORMAT, this.news.getDate()));

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
