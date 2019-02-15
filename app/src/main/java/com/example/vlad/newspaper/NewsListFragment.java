package com.example.vlad.newspaper;

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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsListFragment extends Fragment {
    private static final String DATE_FORMAT = "EEE, MMM dd, yyyy hh:mm a";
    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.news_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        List<News> testList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            News news = new News();
            news.setSourceName("source #" + (i+1));
            news.setTitle("Title #" + (i+1));
            news.setDate(new Date());
            news.setUrl("");
            news.setUrlToImage("https://cnet2.cbsistatic.com/img/P-ItOMWp5HlJRfcSeXuERoyG8kY=/724x407/2019/02/15/3620cd79-3550-4155-a2f3-646e54995831/samsung-wearable-leak.jpg");
            testList.add(news);
        }


        adapter = new NewsAdapter(testList);
        recyclerView.setAdapter(adapter);

        return view;
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
            Toast.makeText(getActivity(), news.getTitle(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
