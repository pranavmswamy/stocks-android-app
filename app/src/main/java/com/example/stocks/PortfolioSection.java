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

public class PortfolioSection extends Section {

    private List<String> portfolio = new ArrayList<String>(Arrays.asList("stock1", "stock2", "stock3"));
    private RecyclerView parentRecyclerView;
    /**
     * Create a Section object based on {@link SectionParameters}.
     */
    public PortfolioSection(Context parentContext, RecyclerView recyclerView) {
        super(SectionParameters.builder().itemResourceId(R.layout.stock_listing).headerResourceId(R.layout.portfolio_header).build());
        this.parentRecyclerView = recyclerView;


        DragToMoveCallback dragCallback = new DragToMoveCallback() {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                Log.e("tag", "Portfolio Section fromPostion = " + fromPosition);
                Log.e("tag", "Portfolio Section toPostion = " + toPosition);

                if(fromPosition <= portfolio.size() &&
                        fromPosition > 0 &&
                        toPosition > 0 &&
                        toPosition <= portfolio.size()) {
                    Log.e("tag", "Entered if condition in portfolio section" );
                    Collections.swap(portfolio, fromPosition-1, toPosition-1);
                    parentRecyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                    return true;
                }
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragCallback);
        itemTouchHelper.attachToRecyclerView(parentRecyclerView);

    }

    @Override
    public int getContentItemsTotal() {
        return portfolio.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new StockListingViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        StockListingViewHolder stockListingHolder = (StockListingViewHolder) holder;
        stockListingHolder.getStockName().setText(portfolio.get(position));
        stockListingHolder.getStockPrice().setText(portfolio.get(position));
        stockListingHolder.getChange().setText(portfolio.get(position));
        stockListingHolder.getSubtitle().setText(portfolio.get(position) + "at position " + holder.getAdapterPosition());
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new PortfolioHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        PortfolioHeaderViewHolder headerHolder = (PortfolioHeaderViewHolder) holder;
        headerHolder.getTitle().setText("PORTFOLIO");
        headerHolder.getAmount().setText("20000.00");


    }
}
