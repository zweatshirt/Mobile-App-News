package com.zach.newsgateway;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

// API query format:
// https://newsapi.org/v2/top-headlines?sources=cnn&language=en&apiKey=ABC123xyz
public class NewsArticleRunnable implements Runnable{
    private final String TAG = "NewsArticleRunnable";
    private final NewsService nService;
    private final String LINK = "https://newsapi.org/v2/top-headlines?sources=";
    private String id;
    private final String KEY = "dbc1977cd9764615be77a2b8c5df2375";


    public NewsArticleRunnable(NewsService nService, String id) {
        this.nService = nService;
        this.id = id;
    }

    private void results(String s) {
        final ArrayList<Article> articles = parseJSON(s);
        if (articles != null) {
            nService.setArticles(articles);
        }
    }


    public void run() {
        StringBuilder urlString = new StringBuilder()
                .append(LINK).append(id)
                .append("&language=en&apiKey=").append(KEY);
        StringBuilder allJson = new StringBuilder();
        URL url;
        HttpsURLConnection conn;
        BufferedReader reader;
        InputStream is;

        try {
            url = new URL(urlString.toString());

            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "");
            is = conn.getInputStream();
            reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                allJson.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        results(allJson.toString());

    }

    private ArrayList<Article> parseJSON(String s) {
        ArrayList<Article> articles = new ArrayList<>();

        try {
            JSONObject allJson = new JSONObject(s);

            JSONArray articlesArray = allJson.getJSONArray("articles");

            String author;
            String title;
            String description;
            String url;
            String urlToImage;
            String publishedAt;

            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject article = articlesArray.getJSONObject(i);

                if (!article.getString("author").trim().isEmpty()
                && !article.getString("author").trim().equals("null")) {
                    author = article.getString("author");
                } else author = null;

                if (!article.getString("title").trim().isEmpty()
                        && !article.getString("title").trim().equals("null")) {
                    title = article.getString("title");
                } else title = null;

                if (!article.getString("description").trim().isEmpty()
                        && !article.getString("description").trim().equals("null")) {
                    description = article.getString("description");
                } else description = null;

                if (!article.getString("url").trim().isEmpty()
                        && !article.getString("url").trim().equals("null")) {
                    url = article.getString("url");
                } else url = null;

                if (!article.getString("urlToImage").trim().isEmpty()
                        && !article.getString("urlToImage").trim().equals("null")) {
                    urlToImage = article.getString("urlToImage");
                } else urlToImage = null;

                if (!article.getString("publishedAt").trim().isEmpty()
                        && !article.getString("publishedAt").trim().equals("null")) {
                    publishedAt = article.getString("publishedAt");
                } else publishedAt = null;

                articles.add(new Article(author, title, description, url, urlToImage, publishedAt));
            }
            return articles;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
