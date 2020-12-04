package com.example.stocks;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;


class StockListingDataModel {
    String companyName;
    String currentPrice;
    double change;
    String noOfShares;
    static RequestQueue requestQueue = null;
    //static AtomicInteger requestCounter = new AtomicInteger(0);
    static int requestCounter = 0;
    static int count = 0;
    Context parentContext;
    String ticker;
    ProgressBar progressBarMain;
    TextView fetchingDataMain;
    RecyclerView parentRecyclerView;
    boolean modelLoaded = false;

    public StockListingDataModel(Context parentContext, String ticker, RecyclerView parentRecyclerView, ProgressBar progressBarMain, TextView fetchingDataMain) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(parentContext);
        }
        this.parentContext = parentContext;
        this.progressBarMain = progressBarMain;
        this.fetchingDataMain = fetchingDataMain;
        this.ticker = ticker;
        //Log.e("constructor", "StockListingDataModel: entered constructor for " + ticker);
        this.parentRecyclerView = parentRecyclerView;
        companyName = null;
        update(this.ticker.toLowerCase());
    }

    void update(String ticker) {
        //Log.e("update()", "update: entered update for " + ticker);
        modelLoaded = false;
        parentRecyclerView.setVisibility(View.GONE); // set it back to gone.
        progressBarMain.setVisibility(View.VISIBLE);
        fetchingDataMain.setVisibility(View.VISIBLE);
        sendAboutGetRequest(ticker);
    }

    void updateValues(boolean showSpinner) {
        //Log.e("fd", "updateValues: entered updateValues() for " + ticker);
        if (showSpinner == false) {
            parentRecyclerView.setVisibility(View.VISIBLE);
            progressBarMain.setVisibility(View.GONE);
            fetchingDataMain.setVisibility(View.GONE);
        }
        else {
            parentRecyclerView.setVisibility(View.GONE);
            progressBarMain.setVisibility(View.VISIBLE);
            fetchingDataMain.setVisibility(View.VISIBLE);
        }
        sendAboutGetRequest(ticker);
    }

    void sendAboutGetRequest(String stock) {
        //Log.e("about()", "sendAboutGetRequest: entered about getRequest() for" + stock);
        String url = "https://us-west2-stocks-app-backend.cloudfunctions.net/company-details?companyName="+stock;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    companyName = jsonObject.getString("name");
                    //Log.e("about()", "onResponse: about received for " + stock);
                    //requestCounter.decrementAndGet(); // 0 again at object level, but maybe not at class level.
                    //requestCounter--;
                    count++;
                    sendLatestPriceGetRequest(ticker);
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

        if (companyName == null) {
            //requestCounter.addAndGet(1);
            //requestCounter++;
            requestQueue.add(stringRequest);
        }
        else {
            sendLatestPriceGetRequest(ticker);
        }
    }

    void sendLatestPriceGetRequest(String stock) {
        //Log.e("price()", "sendLatestPriceGetRequest: " + stock );
        String url = "https://us-west2-stocks-app-backend.cloudfunctions.net/latest-price?companyName="+stock;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    double last = Double.parseDouble(jsonObject.getString("last"));
                    double prevClose = Double.parseDouble(jsonObject.getString("prevClose"));
                    double _change = last - prevClose;

                    currentPrice = String.format("%.2f", last);
                    change = _change;
                    Log.e("price()", "onResponse: New Price received for  " + stock + " - " + currentPrice);
                    //requestCounter.decrementAndGet(); // 0 again at object level, but at class level no.
                    //requestCounter--;
                    count++;
                    setNoOfShares(ticker);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //requestCounter.addAndGet(1);
        //requestCounter++;
        requestQueue.add(stringRequest);
    }

    void setNoOfShares(String stock) {
        SharedPreferences sharedPreferences = parentContext.getSharedPreferences("stock_app", 0);
        float noOfSharesBought = sharedPreferences.getFloat(stock+ "_qty", -1.0f);
        if (noOfSharesBought > 0) {
            noOfShares = String.format("%.2f", noOfSharesBought) + " shares";
        }
        else {
            noOfShares = companyName;
        }

        //Log.e("sdfs", "setNoOfShares: requestCounter = " + requestCounter );

        modelLoaded = true;
        //Log.w("Adasd", "setNoOfShares: count = " + count + ", requestCounter = " + requestCounter);
        if (/*requestCounter.get() requestCounter == 0*/ count >= requestCounter) {
            progressBarMain.setVisibility(View.GONE);
            fetchingDataMain.setVisibility(View.GONE);
            parentRecyclerView.setVisibility(View.VISIBLE);

            //Log.e("sdfg", "setNoOfShares: count >= requestCounter = " + count);
        }
        parentRecyclerView.getAdapter().notifyDataSetChanged();
    }

}
