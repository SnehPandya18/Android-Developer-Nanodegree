package com.snehpandya.popularmovies.adapter;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.snehpandya.popularmovies.BuildConfig;
import com.snehpandya.popularmovies.R;
import com.snehpandya.popularmovies.databinding.ListItemBinding;
import com.snehpandya.popularmovies.model.Result;
import com.snehpandya.popularmovies.viewholder.MoviesViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesViewHolder> {

    private final MovieClickListener mMovieClickListener;
    private ArrayList<Result> mMoviesList;

    public MoviesAdapter(MovieClickListener movieClickListener, ArrayList<Result> moviesList) {
        this.mMovieClickListener = movieClickListener;
        this.mMoviesList = moviesList;
    }

    @BindingAdapter({"android:src"})
    public static void setImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.shade)
                        .error(R.drawable.ic_launcher_foreground)
                        .override(800)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(BuildConfig.imageUrl + url)
                .into(imageView);
    }

    public ArrayList<Result> getMoviesList() {
        return mMoviesList;
    }

    public void addMoviesList(List<Result> moviesList) {
        mMoviesList.addAll(moviesList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding listItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item, parent, false);
        return new MoviesViewHolder(listItemBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        holder.setBinding(mMoviesList.get(position));
        holder.mBinding.image.setTransitionName("movie_" + String.valueOf(holder.getAdapterPosition()));
        holder.itemView.setOnClickListener(v -> mMovieClickListener.onMoviePosterClick(mMoviesList.get(position),
                holder.mBinding.image,
                holder.mBinding.image.getTransitionName()));
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }
}
