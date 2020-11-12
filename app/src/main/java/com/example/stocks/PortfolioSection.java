package com.example.stocks;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.utils.EmptyViewHolder;

public class PortfolioSection extends Section {

    private List<String> portfolio = Arrays.asList("stock1", "stock2", "stock3");

    /**
     * Create a Section object based on {@link SectionParameters}.
     */
    public PortfolioSection() {
        super(SectionParameters.builder().itemResourceId(R.layout.stock_listing).headerResourceId(R.layout.section_header).build());

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
        stockListingHolder.getSubtitle().setText(portfolio.get(position));
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        headerHolder.getTitle().setText("PORTFOLIO SET FROM ..");
    }
}
