package com.example.stocks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class WatchlistSection extends Section {

    private final PortfolioSection portfolio;
    private ArrayList<StockListingDataModel> watchlist;
    private RecyclerView parentRecyclerView;
    Context parentContext;
    ProgressBar progressBarMain;
    TextView fetchingDataMain;
    /**
     * Create a Section object based on {@link SectionParameters}.
     */
    public WatchlistSection(Context parentContext, RecyclerView recyclerView, PortfolioSection portfolioSection, ProgressBar progressBarMain, TextView fetchingDataMain) {
        super(SectionParameters.builder().itemResourceId(R.layout.stock_listing).headerResourceId(R.layout.watchlist_header).build());
        this.parentRecyclerView = recyclerView;
        this.portfolio = portfolioSection;
        this.parentContext = parentContext;

        this.progressBarMain = progressBarMain;
        this.fetchingDataMain = fetchingDataMain;

        watchlist = new ArrayList<>();

        /*if (favoritesList != null) {
            watchlist = new ArrayList<>();
            for (String stock : favoritesList) {
                watchlist.add(new StockListingDataModel(parentContext, stock));
            }
        }*/
        update();
    }

    void update() {
        parentRecyclerView.setVisibility(View.GONE);
        fetchingDataMain.setVisibility(View.VISIBLE);
        progressBarMain.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = parentContext.getSharedPreferences("stock_app", 0);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //Log.d("asdsf", "update: update in fav called");
        Set<String> favoritesInPref = sharedPreferences.getStringSet("favorites", null);
        //Log.e("fav in watchlist.update()", "update: " + favoritesInPref );
        if (favoritesInPref != null) {
            if(watchlist == null) {
                watchlist = new ArrayList<>();
            }
            else {
                watchlist.clear();
            }

            for (String stock : favoritesInPref) {
                watchlist.add(new StockListingDataModel(parentContext, stock, parentRecyclerView, progressBarMain, fetchingDataMain));
            }
        }
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

        stockListingHolder.getStockName().setText(watchlist.get(position).ticker.toUpperCase());
        stockListingHolder.getStockPrice().setText(watchlist.get(position).currentPrice);
        double change = Double.parseDouble(String.format("%.2f", watchlist.get(position).change));
        if (change > 0) {
            stockListingHolder.trending.setVisibility(View.VISIBLE);
            stockListingHolder.getChange().setTextColor(ContextCompat.getColor(parentContext,R.color.green));
            stockListingHolder.trending.setImageDrawable(ContextCompat.getDrawable(parentContext, R.drawable.ic_twotone_trending_up_24));
        }
        else if (change < 0) {
            stockListingHolder.trending.setVisibility(View.VISIBLE);
            stockListingHolder.getChange().setTextColor(ContextCompat.getColor(parentContext, R.color.red));
            stockListingHolder.trending.setImageDrawable(ContextCompat.getDrawable(parentContext, R.drawable.ic_baseline_trending_down_24));
        }
        else {
            stockListingHolder.trending.setVisibility(View.GONE);
            stockListingHolder.getChange().setTextColor(ContextCompat.getColor(parentContext, R.color.trendingSearch));
        }


        stockListingHolder.getChange().setText(String.format("%.2f", change));
        stockListingHolder.getSubtitle().setText(watchlist.get(position).noOfShares);

        stockListingHolder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDetails = new Intent(v.getContext(), DetailsActivity.class);
                goToDetails.putExtra("stock", watchlist.get(position).ticker);
                v.getContext().startActivity(goToDetails);
            }
        });
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

    public ArrayList<StockListingDataModel> getWatchlist() {
        return watchlist;
    }

}
