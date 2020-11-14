package com.example.stocks;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class WatchlistSection extends Section {

    private final PortfolioSection portfolio;
    private List<String> watchlist = new ArrayList<String>(Arrays.asList("wl1", "wl2", "wl3"));
    private RecyclerView parentRecyclerView;
    /**
     * Create a Section object based on {@link SectionParameters}.
     */
    public WatchlistSection(Context parentContext, RecyclerView recyclerView, PortfolioSection portfolioSection) {
        super(SectionParameters.builder().itemResourceId(R.layout.stock_listing).headerResourceId(R.layout.watchlist_header).build());
        this.parentRecyclerView = recyclerView;
        this.portfolio = portfolioSection;

        SwipeAndMoveCallback swipeAndMoveCallback = new SwipeAndMoveCallback(parentContext) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                Log.e("tag", "Watchlist Section fromPosition = " + fromPosition);
                Log.e("tag", "Watchlist Section toPosition = " + toPosition);

                if(fromPosition > portfolio.getContentItemsTotal() + 1 &&
                        toPosition < watchlist.size() + portfolio.getContentItemsTotal() + 2 &&
                        toPosition > portfolio.getContentItemsTotal() + 1 &&
                        fromPosition < watchlist.size() + portfolio.getContentItemsTotal() + 2) {
                    Log.e("tag", "Entered if condition in watchlist section" );
                    Collections.swap(watchlist, fromPosition - portfolio.getContentItemsTotal() - 2, toPosition - portfolio.getContentItemsTotal() - 2);
                    parentRecyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                    return true;
                }
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        Log.e("tag", "onSwiped: "+"item being removed" );
                        watchlist.remove(watchlist.size() - portfolio.getContentItemsTotal() - 2);
                        parentRecyclerView.getAdapter().notifyItemRemoved(position);
                        break;
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAndMoveCallback);
        itemTouchHelper.attachToRecyclerView(parentRecyclerView);
    }

    @Override
    public int getContentItemsTotal() {
        return watchlist.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new StockListingViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        StockListingViewHolder stockListingHolder = (StockListingViewHolder) holder;
        stockListingHolder.getStockName().setText(watchlist.get(position));
        stockListingHolder.getStockPrice().setText(watchlist.get(position));
        stockListingHolder.getChange().setText(watchlist.get(position));
        stockListingHolder.getSubtitle().setText(watchlist.get(position) + "at position " + holder.getAdapterPosition());
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        headerHolder.getTitle().setText("WATCHLIST");

    }


}
