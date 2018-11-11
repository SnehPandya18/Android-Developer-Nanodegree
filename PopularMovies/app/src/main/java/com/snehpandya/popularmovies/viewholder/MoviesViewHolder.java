package com.snehpandya.popularmovies.viewholder;

import android.support.v7.widget.RecyclerView;

import com.snehpandya.popularmovies.databinding.ListItemBinding;
import com.snehpandya.popularmovies.model.Result;

public class MoviesViewHolder extends RecyclerView.ViewHolder {

    public ListItemBinding mBinding;

    public MoviesViewHolder(ListItemBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }

    public void setBinding(Result result) {
        mBinding.setResult(result);
        mBinding.executePendingBindings();
    }
}
