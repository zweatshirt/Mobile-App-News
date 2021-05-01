package com.zach.newsgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/* API key: dbc1977cd9764615be77a2b8c5df2375
* format: https://newsapi.org/v2/sources?language=en&country=us&category=business&apiKey=
*
* Categories selected in opt menu -> load srcs in opt menu
* -> load article fragments into view pager
*
*  */



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final HashMap<String, Source> srcs = new HashMap<String, Source>();
    private final ArrayList<String> srcNames = new ArrayList<>();
    private ArrayList<String> cats = new ArrayList<>();
    private final List<Article> articles = new ArrayList<>();
    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle toggle;
    private List<Fragment> fragments;
    private ViewPager pager;
    private MyPageAdapter pageAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private String currArticle;
    private String currSrcName;
    private final String[] colorStrs = {"#258122", "#E9CC0E", "#6D17BE", "#000000",
            "#00FA80", "#FF0073", "70FFF5", "#781814", "C9B624", "#717D7E"};

    public static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    public static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    private NewsService nService;
    private NewsReceiver nReceiver;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display disp = getWindowManager().getDefaultDisplay();

        nService = new NewsService();
        nReceiver = new NewsReceiver();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItem(position);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
        );

        toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout,
                R.string.open, R.string.close
        );



        fragments = new ArrayList<>();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.view_pager);
        pager.setAdapter(pageAdapter);

        pager.setBackgroundResource(R.drawable.news);

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);
        if (srcs.isEmpty()) {
            new Thread(new NewsSourceRunnable(this, "all")).start();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }



    }

    // update news stories on resume by registering new intent filter
    @Override
    protected void onResume() {
        IntentFilter newsStoryFilter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(nReceiver, newsStoryFilter);
        super.onResume();
    }

    //unregister the receiver onStop
    @Override
    protected void onStop() {
        unregisterReceiver(nReceiver);
        super.onStop();
    }

    public void selectItem(int position) {
        pager.setBackground(null);

        String currSrcStr = srcNames.get(position);
        Source currSrc = srcs.get(currSrcStr);
//        String id = currSrc.getId();
//        currName = currSrc.getName();

        // fix
        // start new thread to grab  articles based on src
//        new Thread(new NewsArticleRunnable(this, id)).start();
        if (currSrc != null && currSrc.getName() != null) {
            setTitle(currSrc.getName());
        }

        Intent intent = new Intent(ACTION_MSG_TO_SERVICE);
        intent.putExtra("source", currSrc);
        sendBroadcast(intent);

        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;


        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: toggle" + item);
            return true;
        }
        setTitle(item.getTitle());
        srcNames.clear();
        if (item.getTitle().toString().equals("All")) {
            for (Source source : srcs.values()) {
                srcNames.add(source.getName());
            }
        }
        else {
            for (Source source : srcs.values()) {
                if (source.getCategory().equals(item.getTitle().toString().toLowerCase())) {
                    srcNames.add(source.getName());
                }
            }
        }
        // new Thread(new NewsSourceRunnable(this, (String) item.getTitle()));
        Collections.sort(srcNames);
        pager.setBackgroundResource(R.drawable.news);
        arrayAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        toggle.onConfigurationChanged(newConfig);
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles.clear();
        this.articles.addAll(articles);
        setTitle(currSrcName); // current source name

        for (int i = 0; i < pageAdapter.getCount(); i ++) {
            pageAdapter.notifyChangeInPosition(i);
        }
        fragments.clear();
        for (int i = 0; i < articles.size(); i ++) {
            fragments.add(ArticlesFragment.newInstance(articles.get(i), i + 1, articles.size()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

//    private void colorItem(MenuItem item) {
//
//
//    }


    // set by category
    // switch to pass in list of sources and list of categories
    public void setSrcs(ArrayList<Source> srcs, ArrayList<String> cats) {
        // pass in list of Sources and list of categories
        this.srcs.clear();
        srcNames.clear();
        this.cats.clear();
        sortSources(srcs);
        for (Source src : srcs) {
            srcNames.add(src.getName());
            this.srcs.put(src.getName(), src);
        }
        // Collections.sort(srcNames);

        this.cats.addAll(cats);
        this.cats.add(0, "All");
        Collections.sort(this.cats);

        String upperCat = "";
        for (String cat : this.cats) {
            upperCat = cat.substring(0, 1).toUpperCase() + cat.substring(1);
            MenuItem item = opt_menu.add(upperCat);
            // implement
            //colorItem(item);
        }


//
//        // ArrayList<String> temp = new ArrayList<>(srcs.keySet());
//
//        Collections.sort(temp);
//        for (String category : temp) {
//            opt_menu.add(category);
//            catList.add(category);
//        }
//
//
//      ArrayList<String> names = new ArrayList<>(hashM.keySet());
        // this stuff needs to get moved to onCreate at some point
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer_item, srcNames);
        mDrawerList.setAdapter(arrayAdapter);
//

        // might not be necessary
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//           getSupportActionBar().setHomeButtonEnabled(true);
//        }

    }

    private void sortSources(ArrayList<Source> srcList) {
        srcList.sort(new Comparator<Source>() {
            @Override
            public int compare(Source o1, Source o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
    }

    // build receiver
    // builder service
    // activity has a receiver specific to service
    // mostly done but needs to be tested
    public class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Article> articles;
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            if(action.equals(MainActivity.ACTION_NEWS_STORY)){
                // might crash
                if (intent.hasExtra("articles")) {
                    articles = (ArrayList<Article>) intent.getSerializableExtra("articles");
                    setTitle(currSrcName); // current source name
                    redoFragments(articles);
                }
            }
        }

        private void redoFragments(ArrayList<Article> articles) {
            // fix to show current source name
            // setTitle();
            for (int i = 0; i < pageAdapter.getCount(); i ++) {
                pageAdapter.notifyChangeInPosition(i);
            }

            fragments.clear();
            for (int i = 0; i < articles.size(); i ++) {
                fragments.add(
                        ArticlesFragment.newInstance(
                                articles.get(i), i + 1, articles.size()));
            }

            pageAdapter.notifyDataSetChanged();
            pager.setCurrentItem(0);
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        MyPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }


}