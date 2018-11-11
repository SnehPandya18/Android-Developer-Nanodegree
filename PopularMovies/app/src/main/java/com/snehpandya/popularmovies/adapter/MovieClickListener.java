package com.snehpandya.popularmovies.adapter;

import android.widget.ImageView;

import com.snehpandya.popularmovies.model.Result;

public interface MovieClickListener {
    void onMoviePosterClick(Result result, ImageView imageView, String transitionName);
}
