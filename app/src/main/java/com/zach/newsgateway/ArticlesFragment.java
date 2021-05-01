package com.zach.newsgateway;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Locale;

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
                date.setText(article.getPublishedAt());
            }
            else date.setVisibility(View.GONE);

            TextView auth = fragLayout.findViewById(R.id.authorView);
            if (article.getAuthor() != null) {
                auth.setText(article.getAuthor());
            }
            else auth.setVisibility(View.GONE);

            Drawable drawable = null;

            ImageView imgView = fragLayout.findViewById(R.id.imgView);
            imgView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            if (article.getDrawable() != null) {
                imgView.setImageDrawable(article.getDrawable());
            }
            else imgView.setVisibility(View.GONE);

            TextView count = fragLayout.findViewById(R.id.countView);
            count.setText(String.format(Locale.US, "%d of %d", idx, max));


            return fragLayout;
        }
        else return null;


    }
}