package com.zach.newsgateway;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArticlesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticlesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String format = "MMM dd, yyyy HH:mm";
    private final DateTimeFormatter dtfISO = DateTimeFormatter.ISO_DATE_TIME;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault());
    //Typeface typeface = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/roboto.ttf");

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ArticlesFragment() {
        // Required empty public constructor
    }

    public static ArticlesFragment newInstance(Article article, int index, int max) {
        ArticlesFragment fragment = new ArticlesFragment();
        Bundle args = new Bundle();
        args.putSerializable("ARTICLE", article);
        args.putSerializable("INDEX", index);
        args.putSerializable("MAX", max);
        fragment.setArguments(args);
        return fragment;
    }

    private void goToLink(String articleUrl) {
        if (articleUrl != null)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragLayout = inflater.inflate(R.layout.fragment_article, container, false);
        Bundle args = getArguments();
        if (args != null) {
            final Article article = (Article) args.getSerializable("ARTICLE");
            if (article == null) {
                return null;
            }

            int idx = args.getInt("INDEX");
            int max = args.getInt("MAX");

            TextView title = fragLayout.findViewById(R.id.titleView);
            if (article.getTitle() != null) {
                title.setText(article.getTitle());
            }
            else title.setVisibility(View.GONE);

            TextView date = fragLayout.findViewById(R.id.dateView);
            if (article.getPublishedAt() != null) {
                ZonedDateTime zdt = ZonedDateTime.parse(article.getPublishedAt(), dtfISO);
                String dtfStr = dtf.format(zdt) + " "; // italicized stuff gets cut off at the end
                // of text views
                date.setText(dtfStr);
            }
            else date.setVisibility(View.GONE);

            TextView auth = fragLayout.findViewById(R.id.authorView);
            if (article.getAuthor() != null) {
                auth.setText(article.getAuthor());
            }
            else auth.setVisibility(View.GONE);

            //Drawable drawable = null;

            ImageView imgView = fragLayout.findViewById(R.id.imgView);
            imgView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            if (article.getUrlToImage() != null) {
//              imgView.setImageDrawable(article.getDrawable());
                Picasso.get()
                        .load(article.getUrlToImage())
                        .into(imgView, new Callback()  {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError(Exception e) {
                                imgView.setVisibility(View.GONE);
                            }
                        });
            }
            else imgView.setVisibility(View.GONE);

            TextView desc = fragLayout.findViewById(R.id.descView);
            if (article.getDescription() != null) {
                desc.setText(article.getDescription());
            }
            else desc.setVisibility(View.GONE);

            TextView count = fragLayout.findViewById(R.id.countView);
            count.setText(String.format(Locale.US, "%d of %d", idx, max));
//            MainActivity.updateIdx(idx);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToLink(article.getUrl());
                }
            });

            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToLink(article.getUrl());
                }
            });

            auth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToLink(article.getUrl());
                }
            });

            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToLink(article.getUrl());
                }
            });

            desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToLink(article.getUrl());
                }
            });

            return fragLayout;
        }
        else return null;


    }
}