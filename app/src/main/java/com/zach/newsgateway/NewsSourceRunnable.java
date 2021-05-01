package com.zach.newsgateway;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class NewsSourceRunnable implements Runnable {

    private final String TAG = "NewsSourceRunnable";
    private final MainActivity ma;
    private final String category;
    private final String KEY = "dbc1977cd9764615be77a2b8c5df2375";
    private final String URL = "https://newsapi.org/v2/sources?language=en&country=us&category=";

    public NewsSourceRunnable(MainActivity ma, String category) {
        this.ma = ma;
        if (!category.toLowerCase().equals("all")) {
            this.category = category.toLowerCase();
        }
        else this.category = "";
    }

    private void results(String s) {
        // populate source list
        final ArrayList<Source> srcs = parseJSON(s);
        // set up list of categories
        final ArrayList<String> cats = new ArrayList<>();
        if (srcs != null) {
            for (Source src : srcs) {
                if (!cats.contains(src.getCategory())) {
                    cats.add(src.getCategory());
                }
            }
            ma.runOnUiThread(() -> ma.setSrcs(srcs, cats));
        }
    }


    @Override
    public void run() {
        StringBuilder allJson = new StringBuilder();
        StringBuilder uriStrB = new StringBuilder();
        uriStrB.append(URL).append(category).append("&apiKey=").append(KEY);

        Uri uri = Uri.parse(uriStrB.toString());
        String uriString = uri.toString();
        URL url;
        HttpsURLConnection conn;
        BufferedReader reader;

        try {
            url = new URL(uriString);

            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent","");
            InputStream is = conn.getInputStream();
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


    private ArrayList<Source> parseJSON(String s) {

        ArrayList<Source> srcs = new ArrayList<>();
        try {
            JSONObject allInfo = new JSONObject(s);

            JSONArray sourceArray = allInfo.getJSONArray("sources");

            for (int i = 0; i < sourceArray.length(); i++) {
                JSONObject source = (JSONObject) sourceArray.get(i);
                String id = source.getString("id");
                String name = source.getString("name");
                String category = source.getString("category");

//                if (!srcs.containsKey(category))
//                    srcs.put(category, new HashSet<>());
//
//                HashSet<Source> catSet = srcs.get(category);
//                if (catSet != null) {
//                    catSet.add(new Source(id, name, category));
//                }
                srcs.add(new Source(id, name, category));
            }
            return srcs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
