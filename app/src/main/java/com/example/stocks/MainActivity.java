package com.example.stocks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;

import java.util.Collections;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Stocks);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();

        PortfolioSection portfolio = new PortfolioSection(this, recyclerView);
        WatchlistSection watchlist = new WatchlistSection(this, recyclerView, portfolio);
        Footer footer = new Footer();


        sectionAdapter.addSection(portfolio);
        sectionAdapter.addSection(watchlist);
        sectionAdapter.addSection(footer);


        recyclerView.setAdapter(sectionAdapter);

        SwipeAndMoveCallback swipeAndMoveCallback = new SwipeAndMoveCallback(this, portfolio.getPortfolio(), watchlist.getWatchlist(), recyclerView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAndMoveCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



}