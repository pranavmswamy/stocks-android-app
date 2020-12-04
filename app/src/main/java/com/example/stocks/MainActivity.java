package com.example.stocks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PortfolioSection portfolio;
    WatchlistSection watchlist;
    private AutoSuggestAdapter autoSuggestAdapter;
    private ProgressBar progressBarMain;
    TextView fetchingDataMain;
    private Handler handler;
    public static final long DEFAULT_INTERVAL = 15 * 1000;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        for(int i=0; i<Integer.MAX_VALUE/2 ; i++) {}

        setTheme(R.style.Theme_Stocks);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        recyclerView = findViewById(R.id.recycler_view);
        progressBarMain = findViewById(R.id.progressBarMain);
        fetchingDataMain = findViewById(R.id.fetchingDataMain);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        //recyclerView.setVisibility(View.GONE);

        //ArrayList<String> portfolioList = null, favoritesList = null;
        portfolio = new PortfolioSection(this, recyclerView, progressBarMain, fetchingDataMain);
        watchlist = new WatchlistSection(this, recyclerView, portfolio, progressBarMain, fetchingDataMain);
        Footer footer = new Footer();

        sectionAdapter.addSection(portfolio);
        sectionAdapter.addSection(watchlist);
        sectionAdapter.addSection(footer);

        //Log.e("total length", "onCreate: " + sectionAdapter.getItemCount());

        recyclerView.setAdapter(sectionAdapter);

        SwipeAndMoveCallback swipeAndMoveCallback = new SwipeAndMoveCallback(this, portfolio.getPortfolio(), watchlist.getWatchlist(), recyclerView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAndMoveCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.getAdapter().notifyDataSetChanged();

        sharedPreferences = getApplicationContext().getSharedPreferences("stock_app", 0);
        editor = sharedPreferences.edit();

        editor.putBoolean("first_time_loaded", true);
        editor.commit();

        //recyclerView.setVisibility(View.VISIBLE);
        //progressBarMain.setVisibility(View.GONE);
        //fetchingDataMain.setVisibility(View.GONE);
        handler = new Handler();
    }

    GetPriceRunnable runnableService = new GetPriceRunnable() {
        @Override
        public void run() {
            // create task here
            if(!KILL) {
                for (StockListingDataModel model: portfolio.getPortfolio()) {
                    model.updateValues(false);
                }
                for (StockListingDataModel model: watchlist.getWatchlist()) {
                    model.updateValues(false);
                }
                handler.postDelayed(runnableService, DEFAULT_INTERVAL);
            }
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // Get SearchView autocomplete object.
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setThreshold(3);

        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setAdapter(autoSuggestAdapter);

        Log.e("main", "onCreateOptionsMenu: in autocomplete" );

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String queryString = (String) parent.getItemAtPosition(position);
                //searchAutoComplete.setText("" + queryString);
                searchAutoComplete.setText(queryString.trim());

                String ticker = queryString.split("-")[0].trim();

                //open activity.
                Intent detailsIntent = new Intent(view.getContext(), DetailsActivity.class);
                detailsIntent.putExtra("stock", ticker);
                view.getContext().startActivity(detailsIntent);

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                makeAutoCompleteApiCall(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                makeAutoCompleteApiCall(newText);
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        Log.e("asdf", "onStart: CALLED" );
        //StockListingDataModel.requestCounter = new AtomicInteger(0);


        //watchlist.update(); -- updateValues() instead of this in onResume()
        //portfolio.update();

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("main", "onResume: main CALLED");
        boolean showSpinner = false;
        if (sharedPreferences.getBoolean("first_time_loaded", false) == true)
        {
            showSpinner = true;
            editor.putBoolean("first_time_loaded", false);
            editor.commit();
        }
        else{
            showSpinner = false;
        }

        Set<String> portfolioInPref = sharedPreferences.getStringSet("portfolio", new HashSet<>());
        Set<String> watchlistInPref = sharedPreferences.getStringSet("favorites", new HashSet<>());

        Log.e("adfsdf", "favorites in shared pref on OnResume - " + watchlistInPref);

        StockListingDataModel.requestCounter = (portfolioInPref.size() + watchlistInPref.size()) * 2;
        StockListingDataModel.count = 0;
        if (watchlistInPref.size() != watchlist.getWatchlist().size() || portfolioInPref.size() != portfolio.getPortfolio().size()) {
            Log.e("dffa", "onResume: lists updated, so entered if condiditon." );
            watchlist.update();
            portfolio.update();
        }

        if (watchlistInPref.size() == 0 && portfolioInPref.size() == 0) {
            recyclerView.setVisibility(View.VISIBLE);
            fetchingDataMain.setVisibility(View.GONE);
            progressBarMain.setVisibility(View.GONE);
        }
        /*else {
            for (StockListingDataModel model: portfolio.getPortfolio()) {
                model.updateValues(showSpinner);
            }
            for (StockListingDataModel model: watchlist.getWatchlist()) {
                model.updateValues(showSpinner);
            }
        }*/

        runnableService.enableTask();
        handler.postDelayed(runnableService, 1500);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("ada", "onStop: called" );
        runnableService.killTask();
        handler.removeCallbacksAndMessages(runnableService);
    }

    private void makeAutoCompleteApiCall(String text) {
        AutoCompleteApiCall.make(this, text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<String> stringList = new ArrayList<>();

                try {
                    JSONArray responseArray = new JSONArray(response);
                    for (int i=0; i<responseArray.length(); i++) {
                        JSONObject row = responseArray.getJSONObject(i);
                        stringList.add(row.getString("ticker") + " - " + row.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // imp - change data here.
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}