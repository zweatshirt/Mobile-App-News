package com.zach.newsgateway;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;

import com.caverock.androidsvg.SVG;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;

public class Article implements Serializable {

    // should probably make these final
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private Drawable img;

    public Article(String auth, String t, String desc, String url,
                   String urlImg, String publshedAt) {
        this.author = auth;
        this.title = t;
        this.description = desc;
        this.url = url;
        this.urlToImage = urlImg;
        this.publishedAt = publshedAt;

        try {
            InputStream input = new java.net.URL(urlToImage).openStream();
            SVG svg = SVG.getFromInputStream(input);
            this.img = new PictureDrawable(svg.renderToPicture());
        } catch (Exception e) {
            this.img = null;
            e.printStackTrace();
        }

    }

    public Drawable getDrawable() {
        return img;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String t) {
        this.title = t;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrlToImage(String urlImg) {
        this.urlToImage = urlImg;
    }





}
