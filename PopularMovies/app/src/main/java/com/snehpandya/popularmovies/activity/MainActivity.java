package com.snehpandya.popularmovies.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.snehpandya.popularmovies.BuildConfig;
import com.snehpandya.popularmovies.R;
import com.snehpandya.popularmovies.adapter.MovieClickListener;
import com.snehpandya.popularmovies.adapter.MoviesAdapter;
import com.snehpandya.popularmovies.apiservice.RetrofitService;
import com.snehpandya.popularmovies.databinding.ActivityMainBinding;
import com.snehpandya.popularmovies.model.Response;
import com.snehpandya.popularmovies.model.Result;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MovieClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_ITEM = "movie_item_url";
    public static final String EXTRA_MOVIE_TRANSITION_NAME = "movie_image_transition_name";

    private boolean isTopRated;
    private int page = 1;

    private ActivityMainBinding mActivityMainBinding;
    private GridLayoutManager mGridLayoutManager;
    private MoviesAdapter mMoviesAdapter;

    private RetrofitService mRetrofitService = new RetrofitService();
    private Observable<Response> mResponseObservable;

    private Response mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mGridLayoutManager = new GridLayoutManager(this, 2);
        mActivityMainBinding.recyclerview.setLayoutManager(mGridLayoutManager);
        mMoviesAdapter = new MoviesAdapter(this, new ArrayList<>());
        mActivityMainBinding.recyclerview.setAdapter(mMoviesAdapter);
        mActivityMainBinding.swiperefreshlayout.setOnRefreshListener(this);

        mActivityMainBinding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItems = mGridLayoutManager.getItemCount();
                int lastItem = mGridLayoutManager.findLastVisibleItemPosition();
                boolean isEnd = lastItem + 4 >= totalItems;
                if (totalItems > 0 && isEnd) {
                    page++;
                    getMoviesData(page, isTopRated);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (mMoviesAdapter.getMoviesList().isEmpty()) {
                isTopRated = true;
                getMoviesData(page, isTopRated);
            }
        } else {
            if (!mMoviesAdapter.getMoviesList().isEmpty()) {
                Log.d(TAG, "onResume: " + getString(R.string.error_fetching_data));
            } else {
                mActivityMainBinding.progressbar.setVisibility(GONE);
                mActivityMainBinding.imageNoData.setVisibility(VISIBLE);
                mActivityMainBinding.textNoData.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        getMoviesData(page, isTopRated);
        mActivityMainBinding.swiperefreshlayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular:
                if (isTopRated) {
                    mMoviesAdapter.getMoviesList().clear();
                }
                if (!isTopRated) {
                    Toast.makeText(this, getString(R.string.data_update), Toast.LENGTH_SHORT).show();
                }
                isTopRated = false;
                getMoviesData(1, isTopRated);
                break;
            case R.id.topRated:
                if (!isTopRated) {
                    mMoviesAdapter.getMoviesList().clear();
                }
                if (isTopRated) {
                    Toast.makeText(this, getString(R.string.data_update), Toast.LENGTH_SHORT).show();
                }
                isTopRated = true;
                getMoviesData(1, isTopRated);
                break;
            default:
                getMoviesData(1, isTopRated);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isTopRated", isTopRated);
        outState.putParcelable("movies", mResponse);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setData(savedInstanceState.getParcelable("movies"));
        isTopRated = savedInstanceState.getBoolean("isTopRated");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMoviePosterClick(Result result, ImageView imageView, String transitionName) {
        Intent intent = new Intent(this, DetailActivity.class);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        intent.putExtra(EXTRA_MOVIE_ITEM, result);
        intent.putExtra(EXTRA_MOVIE_TRANSITION_NAME, transitionName);

        optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                imageView,
                imageView.getTransitionName());

        startActivity(intent, optionsCompat.toBundle());
    }

    @SuppressLint("CheckResult")
    public void getMoviesData(int page, boolean isTopRated) {
        if (isTopRated) {
            mResponseObservable = mRetrofitService.mTMDBApi.getTopRatedResponse(BuildConfig.apiKey, page);
            mResponseObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setData, this::handleResponse);
        } else {
            mResponseObservable = mRetrofitService.mTMDBApi.getPopularResponse(BuildConfig.apiKey, page);
            mResponseObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setData, this::handleResponse);
        }
    }

    private void setData(Response response) {
        mResponse = response;
        if (response != null) {
            if (response.getResults() != null) {
                mMoviesAdapter.addMoviesList(response.getResults());
                mActivityMainBinding.progressbar.setVisibility(GONE);
                mActivityMainBinding.imageNoData.setVisibility(GONE);
                mActivityMainBinding.textNoData.setVisibility(GONE);
                mActivityMainBinding.swiperefreshlayout.setRefreshing(false);
            }
        }
    }

    private void handleResponse(Throwable throwable) {
        Log.d(TAG, "handleResponse: " + throwable);
        mActivityMainBinding.progressbar.setVisibility(GONE);
        mActivityMainBinding.swiperefreshlayout.setRefreshing(false);
        Toast.makeText(this, getString(R.string.error_fetching_data), Toast.LENGTH_SHORT).show();
    }
}
