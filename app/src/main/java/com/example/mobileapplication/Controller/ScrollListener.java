package com.example.mobileapplication.Controller;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

// recyclerview scroll listener

public abstract class ScrollListener extends RecyclerView.OnScrollListener {

    LinearLayoutManager linearLayoutManager;

    public ScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }


    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = linearLayoutManager.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        if (!isLoading() && !isLastPage()) {
            if((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems();
            }
        }
    }

    protected abstract void loadMoreItems();
    public abstract boolean isLastPage();
    public abstract boolean isLoading();
}
