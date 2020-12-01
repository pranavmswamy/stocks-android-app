package com.example.stocks;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

class StockListingDataModel {
    String companyName;
    String currentPrice;
    double change;
    String noOfShares;
    RequestQueue requestQueue;
    Context parentContext;
    String ticker;
    RecyclerView parentRecyclerView;

    public StockListingDataModel(Context parentContext, String ticker, RecyclerView parentRecyclerView) {
        requestQueue = Volley.newRequestQueue(parentContext);
        this.parentContext = parentContext;
        this.ticker = ticker;
        this.parentRecyclerView = parentRecyclerView;
        sendAboutGetRequest(ticker);

    }

    void sendAboutGetRequest(String stock) {
        String url = "http://trialnodejsbackend-env.eba-stk2e7fk.us-east-1.elasticbeanstalk.com/company-details?companyName="+stock;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    companyName = jsonObject.getString("name");
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
        requestQueue.add(stringRequest);
    }

    void sendLatestPriceGetRequest(String stock) {
        String url = "http://trialnodejsbackend-env.eba-stk2e7fk.us-east-1.elasticbeanstalk.com/latest-price?companyName="+stock;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    double last = Double.parseDouble(jsonObject.getString("last"));
                    double prevClose = Double.parseDouble(jsonObject.getString("prevClose"));
                    double _change = last - prevClose;

                    currentPrice = String.valueOf(last);
                    change = _change;

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
        parentRecyclerView.getAdapter().notifyDataSetChanged();

    }

}
