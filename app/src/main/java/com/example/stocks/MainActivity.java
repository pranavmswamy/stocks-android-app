package com.example.stocks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PortfolioSection portfolio;
    WatchlistSection watchlist;
    private AutoSuggestAdapter autoSuggestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Stocks);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();


        /*Set<String> portfolioSet = getPortfolio();
        Set<String> favoritesSet = getFavorites();

        ArrayList<String> portfolioList, favoritesList;

        if (portfolioSet != null) {
            portfolioList = new ArrayList<>(portfolioSet);
        }
        else {
            portfolioList = new ArrayList<>();
        }

        if (favoritesSet != null) {
            favoritesList = new ArrayList<>(favoritesSet);
        }
        else {
            favoritesList = new ArrayList<>();
        }*/
        ArrayList<String> portfolioList = null, favoritesList = null;
        portfolio = new PortfolioSection(this, recyclerView, portfolioList);
        watchlist = new WatchlistSection(this, recyclerView, portfolio, favoritesList);
        Footer footer = new Footer();

        sectionAdapter.addSection(portfolio);
        sectionAdapter.addSection(watchlist);
        sectionAdapter.addSection(footer);

        recyclerView.setAdapter(sectionAdapter);

        SwipeAndMoveCallback swipeAndMoveCallback = new SwipeAndMoveCallback(this, portfolio.getPortfolio(), watchlist.getWatchlist(), recyclerView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAndMoveCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.getAdapter().notifyDataSetChanged();
    }

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

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String queryString = (String) parent.getItemAtPosition(position);
                Toast.makeText(view.getContext(), "clicked", Toast.LENGTH_SHORT).show();
                searchAutoComplete.setText("" + queryString);

                String ticker = queryString.split("-")[0].trim();
                searchAutoComplete.setText(ticker);

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

    /*Set<String> getPortfolio() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("stock_app", 0);
        Set<String> portfolio = sharedPreferences.getStringSet("portfolio", null);
        return portfolio;
    }

    Set<String> getFavorites() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("stock_app", 0);
        Set<String> favorites = sharedPreferences.getStringSet("favorites", null);
        return favorites;
    }*/

    @Override
    protected void onStart() {
        watchlist.update();
        portfolio.update();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("main", "onResume: main");

        recyclerView.getAdapter().notifyDataSetChanged();
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