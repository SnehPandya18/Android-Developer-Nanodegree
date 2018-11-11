package com.snehpandya.popularmovies.apiservice;

import com.snehpandya.popularmovies.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private Retrofit mRetrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.apiUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public TMDBInterface mTMDBApi = mRetrofit.create(TMDBInterface.class);
}
