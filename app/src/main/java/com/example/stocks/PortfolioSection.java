package com.example.stocks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class PortfolioSection extends Section {


    private ArrayList<StockListingDataModel> portfolio;
    private ArrayList<StockListingDataModel> watchlist;

    private RecyclerView parentRecyclerView;
    private Context parentContext;
    ProgressBar progressBarMain;
    TextView fetchingDataMain;

    /**
     * Create a Section object based on {@link SectionParameters}.
     */

    public PortfolioSection(Context parentContext, RecyclerView recyclerView, ProgressBar progressBarMain, TextView fetchingDataMain) {
        super(SectionParameters.builder().itemResourceId(R.layout.stock_listing).headerResourceId(R.layout.portfolio_header).build());
        this.parentRecyclerView = recyclerView;
        this.parentContext = parentContext;
        portfolio = new ArrayList<>();
        this.fetchingDataMain = fetchingDataMain;
        this.progressBarMain = progressBarMain;
        /*if (portfolioList != null) {
            portfolio = new ArrayList<>();
            for (String stock : portfolioList) {
                portfolio.add(new StockListingDataModel(parentContext, stock));
            }
        }*/
        update();
    }

    /*StockListingDataModel portfolioItemFactoryMethod(String stock) {
        final String[] companyName = new String[1];
        final String[] currentPrice = new String[1];
        final String[] noOfShares = new String[1];
        final double[] change = new double[1];

        String aboutUrl = "http://trialnodejsbackend-env.eba-stk2e7fk.us-east-1.elasticbeanstalk.com/company-details?companyName="+stock;
        String priceUrl = "http://trialnodejsbackend-env.eba-stk2e7fk.us-east-1.elasticbeanstalk.com/latest-price?companyName="+stock;

        StringRequest aboutRequest = new StringRequest(Request.Method.GET, aboutUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // chaining level 1 starts
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    companyName[0] = jsonObject.getString("name");

                    // chaining level 2 starts
                    StringRequest priceRequest = new StringRequest(Request.Method.GET, priceUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                currentPrice[0] = jsonObject.getString("last");
                                change[0] = Double.parseDouble(jsonObject.getString("last")) - Double.parseDouble(jsonObject.getString("prevClose"));

                                // chaining level 3 starts.
                                SharedPreferences sharedPreferences = parentContext.getSharedPreferences("stock_app", 0);
                                int noOfSharesBought = sharedPreferences.getInt(stock+"_qty", -1);
                                if (noOfSharesBought < 0) {
                                    noOfShares[0] = companyName[0];
                                }
                                else {
                                    noOfShares[0] = noOfSharesBought + " shares";
                                }

                                // need price, name, change, noOfSharesBought or name of company
                                StockListingDataModel stockListing = new StockListingDataModel(companyName[0], currentPrice[0], change[0], noOfShares[0]);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("About Get Req Failed Error", "onErrorResponse: " + error.toString() );
            }
        });

        return stockListing;
    }*/

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

        stockListingHolder.getStockName().setText(portfolio.get(position).ticker.toUpperCase());
        stockListingHolder.getStockPrice().setText(portfolio.get(position).currentPrice);
        double change = Double.parseDouble(String.format("%.2f", portfolio.get(position).change));
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
        stockListingHolder.getSubtitle().setText(portfolio.get(position).noOfShares);
        stockListingHolder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDetails = new Intent(v.getContext(), DetailsActivity.class);
                goToDetails.putExtra("stock", portfolio.get(position).ticker);
                v.getContext().startActivity(goToDetails);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new PortfolioHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        PortfolioHeaderViewHolder headerHolder = (PortfolioHeaderViewHolder) holder;
        headerHolder.getTitle().setText("PORTFOLIO");

        SharedPreferences sharedPreferences = parentContext.getSharedPreferences("stock_app", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        double amountRemaining = sharedPreferences.getFloat("amountRemaining", -1.0f);
        if (amountRemaining < 0) {
            // opened app for first time, not traded. set amount = 20000
            editor.putFloat("amountRemaining", 20000.0f);
        }
        editor.commit();
        headerHolder.getAmount().setText("" + sharedPreferences.getFloat("amountRemaining", -1.0f));

    }

    public ArrayList<StockListingDataModel> getPortfolio() {
        return portfolio;
    }

    void update() {
        parentRecyclerView.setVisibility(View.GONE);
        fetchingDataMain.setVisibility(View.VISIBLE);
        progressBarMain.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = parentContext.getSharedPreferences("stock_app", 0);
        Log.d("asdsf", "update: update in port called");
        Set<String> portfolioinPref = sharedPreferences.getStringSet("portfolio", null);
        Log.e("portfolio in portfolio.update()", "update: " + portfolioinPref );
        if (portfolioinPref != null) {
            if (portfolio != null) {
                portfolio.clear();
            }
            else {
                portfolio = new ArrayList<>();
            }
            for (String stock : portfolioinPref) {
                portfolio.add(new StockListingDataModel(parentContext, stock, parentRecyclerView, progressBarMain, fetchingDataMain));
            }
        }
    }

//    void updateValues() {
//        for(StockListingDataModel model : portfolio) {
//            model.updateValues();
//        }
//    }
}
