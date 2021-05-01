package com.zach.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/* Activity sends broadcast to NewsService, need Receiver to listen */

public class NewsService extends Service {
    private final ArrayList<Article> articles = new ArrayList<>();
    private boolean running = true;
    private NewsServiceReceiver newsServiceReceiver;
    private final String TAG = "NewsService";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        newsServiceReceiver = new NewsServiceReceiver();
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_MSG_TO_SERVICE);
        registerReceiver(newsServiceReceiver, filter);

        new Thread(() -> {
            while(running) {
                Log.d(TAG, "in loop");
                if (articles.isEmpty()) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Intent newIntent = new Intent(MainActivity.ACTION_NEWS_STORY);
                    newIntent.putExtra("articles", articles);
                    sendBroadcast(newIntent);
                    articles.clear();
                }

            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(newsServiceReceiver);
        running = false;
        super.onDestroy();
    }

    public void setArticles(ArrayList<Article> articles) {

        this.articles.clear();
        this.articles.addAll(articles);

    }

    public class NewsServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MainActivity.ACTION_MSG_TO_SERVICE)) {
                Source source = (Source) intent.getSerializableExtra("source");
                new Thread(new NewsArticleRunnable(NewsService.this, source.getId())).start();

            }
        }

    }

}
