package com.example.vlad.newspaper.network;

import android.net.Uri;
import android.util.Log;

import com.example.vlad.newspaper.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NewsFetcher {
    private static final String TAG = "NewsFetcher";
    private static final String API_KEY = "95cba4f179084568991b84dd6efc0ff6";
    private String country = "ua";
    private NewsParser parser;

    public NewsFetcher() {
        this.parser = new NewsParser();
    }

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                                    ": with " +
                                    urlSpec);
            }

            int bytesRead = 0;
            byte[] buf = new byte[1024];

            while ((bytesRead = in.read(buf)) > 0) {
                out.write(buf, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<News> fetchNewsItems(int page) {
        List<News> newsList = null;
        try {
            String urlSpec = Uri.parse("https://newsapi.org/v2/top-headlines")
                                .buildUpon()
                                .appendQueryParameter("apiKey", API_KEY)
                                .appendQueryParameter("country", country)
                                .appendQueryParameter("page", String.valueOf(page))
                                .build().toString();
            String jsonStr = getUrlString(urlSpec);
            Log.i(TAG, "JSON: " + jsonStr);
            JSONObject jsonBody = new JSONObject(jsonStr);
            newsList =  parser.parse(jsonBody);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch news items", e);
        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse date", e);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }

        return newsList;
    }

    private class NewsParser {
        private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        private static final String ARTICLES = "articles";
        private static final String SOURCE = "source";
        private static final String NAME = "name";
        private static final String TITLE = "title";
        private static final String URL = "url";
        private static final String URL_TO_IMAGE = "urlToImage";
        private static final String DATE = "publishedAt";


        public List<News> parse(JSONObject jsonBody) throws IOException, JSONException, ParseException {
            List<News> newsList = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            JSONArray articlesJsonArray = jsonBody.getJSONArray(ARTICLES);

            for (int i = 0; i < articlesJsonArray.length(); i++) {
                JSONObject articleJsonObject = articlesJsonArray.getJSONObject(i);
                News news = new News();

                news.setSourceName(articleJsonObject.getJSONObject(SOURCE)
                                                    .getString(NAME));
                news.setTitle(articleJsonObject.getString(TITLE));
                news.setUrl(articleJsonObject.getString(URL));
                news.setUrlToImage(articleJsonObject.getString(URL_TO_IMAGE));
                news.setDate(dateFormat.parse(articleJsonObject.getString(DATE)));

                newsList.add(news);
            }

            return newsList;
        }
    }
}
