package com.snehpandya.popularmovies.apiservice;

import com.snehpandya.popularmovies.model.Response;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBInterface {

    @GET("3/movie/popular")
    Observable<Response> getPopularResponse(@Query("api_key") String key, @Query("page") int page);

    @GET("3/movie/top_rated")
    Observable<Response> getTopRatedResponse(@Query("api_key") String key, @Query("page") int page);
}
