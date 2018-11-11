package com.snehpandya.popularmovies.activity;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.snehpandya.popularmovies.BuildConfig;
import com.snehpandya.popularmovies.R;
import com.snehpandya.popularmovies.databinding.ActivityDetailBinding;
import com.snehpandya.popularmovies.model.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mActivityDetailBinding;

    private Result mResult;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        postponeEnterTransition();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mResult = bundle.getParcelable(MainActivity.EXTRA_MOVIE_ITEM);
            String transitionName = bundle.getString(MainActivity.EXTRA_MOVIE_TRANSITION_NAME);
            setData(mResult, transitionName);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setData(Result result, String transitionName) {
        Glide.with(this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.shade)
                        .error(R.drawable.ic_launcher_foreground)
                        .override(800)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(BuildConfig.imageUrl + result.getPosterPath())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(mActivityDetailBinding.image);
        mActivityDetailBinding.image.setTransitionName(transitionName);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        try {
            Date date = simpleDateFormat1.parse(result.getReleaseDate());
            mActivityDetailBinding.date.setText(simpleDateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mActivityDetailBinding.title.setText(result.getOriginalTitle());
        if (!result.isAdult()) {
            mActivityDetailBinding.adult.setVisibility(View.GONE);
        } else {
            mActivityDetailBinding.adult.setVisibility(View.VISIBLE);
            mActivityDetailBinding.adult.setText(getString(R.string.adult));
        }

        mActivityDetailBinding.ratingbar.setRating((float) result.getVoteAverage());
        mActivityDetailBinding.overview.setText(result.getOverview());
    }
}
