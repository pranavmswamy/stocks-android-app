package com.example.stocks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DetailsActivity extends AppCompatActivity {

    RecyclerView newsRecyclerView;
    NewsRecyclerViewAdapter newsRecyclerViewAdapter;
    private String stock;
    private TextView stockName;
    private TextView subtitle;
    private TextView price;
    private WebView chart;
    private TextView portfolioHeading;
    private TextView sharesOwned;
    private Button trade;
    private TextView currentPrice;
    private TextView low;
    private TextView bidPrice;
    private TextView openPrice;
    private TextView mid;
    private TextView volume;
    private TextView about;
    private TextView high;
    private RequestQueue requestQueue;
    private TextView marketValue;
    private TextView change;
    private ImageView trending;
    private NestedScrollView nestedScrollView;
    Button showMore;
    private ProgressBar progressBar;
    private TextView fetchingData;
    private ArrayList<StockListingDataModel> watchlist, portfolio;

    boolean favorite;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean showMoreExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar myToolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(myToolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        sharedPreferences =  getApplicationContext().getSharedPreferences("stock_app", 0);
        editor = sharedPreferences.edit();

        requestQueue = Volley.newRequestQueue(DetailsActivity.this);
        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        stockName = findViewById(R.id.stockName);
        subtitle = findViewById(R.id.subtitle);
        price = findViewById(R.id.stockPrice);
        chart = findViewById(R.id.webView);
        //portfolioHeading = findViewById(R.id.portfolioHeading);
        sharesOwned = findViewById(R.id.sharesOwned);
        trade = findViewById(R.id.trade);
        currentPrice = findViewById(R.id.currentPrice);
        low = findViewById(R.id.low);
        bidPrice = findViewById(R.id.bidPrice);
        openPrice = findViewById(R.id.openPrice);
        mid = findViewById(R.id.mid);
        volume = findViewById(R.id.volume);
        high = findViewById(R.id.high);
        about = findViewById(R.id.about);
        marketValue = findViewById(R.id.marketValue);
        change = findViewById(R.id.change);
        showMore = findViewById(R.id.showMore);
        //trending = findViewById(R.id.trendingImage);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        progressBar = findViewById(R.id.progressBar);
        fetchingData = findViewById(R.id.fetchingData);

        nestedScrollView.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            stock = (String) bundle.get("stock");
            watchlist = (ArrayList<StockListingDataModel>) bundle.get("favorites");
            portfolio = (ArrayList<StockListingDataModel>) bundle.get("portfolio");
            stockName.setText(stock.toUpperCase());

            sendLatestPriceGetRequest();
            loadChart();

            // Start get request for News
            //sendNewsGetRequest();
            //sendAboutGetRequest();
        }
        else {
            stock = "N/A";
        }

        //init fav
//        Set<String> favoritesInPref = sharedPreferences.getStringSet("favorites", null);
//        if (favoritesInPref != null) {
//            if (favoritesInPref.contains(stock.toLowerCase())) {
//                favorite = true;
//            }
//            else {
//                favorite = false;
//            }
//        }

    }

    void sendLatestPriceGetRequest() {
        String url = "http://trialnodejsbackend-env.eba-stk2e7fk.us-east-1.elasticbeanstalk.com/latest-price?companyName="+stock;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    currentPrice.setText(jsonObject.getString("last"));
                    openPrice.setText(jsonObject.getString("open"));
                    volume.setText(jsonObject.getString("volume"));
                    low.setText(jsonObject.getString("low"));
                    price.setText(jsonObject.getString("last"));
                    high.setText(jsonObject.getString("high"));

                    double _change = Double.parseDouble(jsonObject.getString("last")) - Double.parseDouble(jsonObject.getString("prevClose"));

                    if (_change > 0) {
                        change.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                        //trending.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_twotone_trending_up_24));
                        change.setText(String.format("+$%.2f", Math.abs(_change)));
                    }
                    else if (_change < 0) {
                        change.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                        //trending.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_trending_down_24));
                        change.setText(String.format("-$%.2f", Math.abs(_change)));
                    }


                    if(jsonObject.getString("mid") == "null") {
                        mid.setText("0.0");
                        bidPrice.setText("0.0");
                    }
                    else {
                        mid.setText(jsonObject.getString("mid"));
                        bidPrice.setText(jsonObject.getString("bidPrice"));
                    }

                    //converting to two decimal places
                    openPrice.setText(String.format("%.2f", Double.parseDouble(openPrice.getText().toString())));
                    currentPrice.setText(String.format("%.2f", Double.parseDouble(currentPrice.getText().toString())));
                    low.setText(String.format("%.2f", Double.parseDouble(low.getText().toString())));
                    bidPrice.setText(String.format("%.2f", Double.parseDouble(bidPrice.getText().toString())));
                    mid.setText(String.format("%.2f", Double.parseDouble(mid.getText().toString())));
                    high.setText(String.format("%.2f", Double.parseDouble(high.getText().toString())));



                    // load portfolio here cause I need last price.
                    loadPortfolioSection();

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

    void sendAboutGetRequest() {
        String url = "http://trialnodejsbackend-env.eba-stk2e7fk.us-east-1.elasticbeanstalk.com/company-details?companyName="+stock;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String aboutText = jsonObject.getString("description");
                    about.setText(aboutText);

                    subtitle.setText(jsonObject.getString("name"));

                    //finished chain of loading sections. hide progressbar and text and show
                    // nestedscrollview.
                    progressBar.setVisibility(View.GONE);
                    fetchingData.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);

                    about.post(() -> {
                        int aboutLines = about.getLineCount();
                        if (aboutLines > 2) {
                            // show showMore Button
                            showMore.setVisibility(View.VISIBLE);
                            about.setMaxLines(2);
                            about.setEllipsize(TextUtils.TruncateAt.END);
                            showMore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (showMoreExpanded == false) {
                                        showMoreExpanded = true;
                                        about.setMaxLines(Integer.MAX_VALUE);
                                        showMore.setText("Show less");
                                    }
                                    else {
                                        showMoreExpanded = false;
                                        about.setMaxLines(2);
                                        about.setEllipsize(TextUtils.TruncateAt.END);
                                        showMore.setText("Show more...");
                                    }
                                }
                            });
                        }
                        else {
                            showMore.setVisibility(View.INVISIBLE);
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
        requestQueue.add(stringRequest);
    }

    void sendNewsGetRequest() {
        String url = "http://trialnodejsbackend-env.eba-stk2e7fk.us-east-1.elasticbeanstalk.com/news?q=" + stock;
        //Log.e("url", "sendNewsGetRequest: " + url );
        ArrayList<NewsDataModel> newsArray = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // parse json
                // store into arraylist
                // send back arraylist

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        String url = jsonObject.getString("url");
                        String imageUrl = jsonObject.getString("urlToImage");
                        String date = jsonObject.getString("publishedAt");
                        JSONObject source = jsonObject.getJSONObject("source");
                        String name = source.getString("name");

                        NewsDataModel newsData = new NewsDataModel(url, date, title, imageUrl, name);
                        newsArray.add(newsData);
                    }

                    newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(newsArray, getApplicationContext());
                    newsRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this));
                    newsRecyclerView.setAdapter(newsRecyclerViewAdapter);

                    sendAboutGetRequest();

                } catch (JSONException e) {
                    // error parsing JSON
                    e.printStackTrace();
                    Log.e("DetailsActivity", "onResponse: Error Parsing News JSON Request");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // send back N/A arraylist
                Log.e("error", "onErrorResponse: " + error.toString() );
                Log.e("DetailsActivity", "onErrorResponse: Error getting News JSON Request");
            }
        });
        requestQueue.add(stringRequest);

    }

    void loadChart() {
        chart.getSettings().setJavaScriptEnabled(true);
        chart.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("webview", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId() );
                return true;
            }

        });

        chart.loadUrl("file:///android_asset/highcharts_map.html");

        chart.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equals("file:///android_asset/highcharts_map.html")) {
                    String chartUrl = "javascript:plot('" + stock + "');";
                    chart.loadUrl(chartUrl);
                }
            }
        });
    }

    void loadPortfolioSection() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("stock_app", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        float quantity = sharedPreferences.getFloat(stock.toLowerCase() + "_qty", -1.0f);
        if (quantity > 0) {
            sharesOwned.setText("Shares owned: " + String.format("%.2f",quantity));
            marketValue.setText("Market Value: $" + String.format("%.2f", quantity * Double.parseDouble(currentPrice.getText().toString())));
        }
        else {
            sharesOwned.setText("You have 0 shares of " + stock.toUpperCase());
            marketValue.setText("Start trading!");
        }

        /*double amount = sharedPreferences.getFloat(stock.toLowerCase() + "_amount", -1.0f);
        if (amount > 0) {
            marketValue.setText(amount + "");
        }
        else {
            marketValue.setText("Start trading!");
        }*/

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                final View customView = inflater.inflate(R.layout.buy_sell_dialog, null);
                builder.setView(customView);
                AlertDialog buySellDialog = builder.create();

                TextView buySellDialogTitle = customView.findViewById(R.id.buySellDialogTitle);
                buySellDialogTitle.setText("Trade " + subtitle.getText() + " shares");

                EditText noOfShares = customView.findViewById(R.id.noOfShares);
                TextView amountRemaining = customView.findViewById(R.id.amountRemaining);
                TextView amountCalc = customView.findViewById(R.id.amountCalculation);

                amountCalc.setText(Double.parseDouble(noOfShares.getText().toString()) + " x $" + price.getText() + "/share = $" + String.format("%.2f", Double.parseDouble(price.getText().toString())*Double.parseDouble(noOfShares.getText().toString())));

                amountRemaining.setText("You have $" + sharedPreferences.getFloat("amountRemaining", 0) + " to buy " + stock.toUpperCase());

                noOfShares.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().length() > 0) {
                            amountCalc.setText(Double.parseDouble(s.toString()) + " x $" + price.getText() + "/share = $" + String.format("%.2f", Double.parseDouble(price.getText().toString())*Double.parseDouble(s.toString())));
                        }
                        else {
                            amountCalc.setText("0 x $" + price.getText() + "/share = $0.00");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                AlertDialog.Builder successBuilder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater1 = LayoutInflater.from(v.getContext());
                final View successView = inflater1.inflate(R.layout.success_dialog, null);
                successBuilder.setView(successView);
                AlertDialog successDialog = successBuilder.create();

                Button successButton = successView.findViewById(R.id.successDone);
                successButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        successDialog.dismiss();
                        // update details view here
                        double updatedQuantity = sharedPreferences.getFloat(stock + "_qty", 0);
                        if (updatedQuantity > 0) {
                            sharesOwned.setText("Shares owned: " + String.format("%.2f", updatedQuantity));
                            marketValue.setText("Market Value: $" + String.format("%.2f", updatedQuantity * Double.parseDouble(currentPrice.getText().toString())));
                        }
                        else {
                            sharesOwned.setText("You have 0 shares of " + stock.toUpperCase());
                            marketValue.setText("Start trading!");
                        }
                    }
                });

                customView.findViewById(R.id.buy).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        float qtyInPortfolio = sharedPreferences.getFloat(stock + "_qty", 0);

                        String qtyEnteredStr = noOfShares.getText().toString();
                        float qtyEntered = 0;
                        if (qtyEnteredStr.length() > 0) {
                            qtyEntered = Float.parseFloat(noOfShares.getText().toString());
                        }

                        double amountRemaining = sharedPreferences.getFloat("amountRemaining", -1.0f);

                        if (qtyEntered > 0 && Double.parseDouble(price.getText().toString())*qtyEntered <= amountRemaining) {
                            // buy
                            float updatedQuantity = qtyEntered + qtyInPortfolio;
                            double cost = qtyEntered*Double.parseDouble(price.getText().toString());
                            double updatedAmountRemaining = amountRemaining - cost;

                            editor.putFloat(stock+"_qty", updatedQuantity);
                            editor.putFloat("amountRemaining", (float) updatedAmountRemaining);

                            // update portfolio set
                            Set<String> portfolio = sharedPreferences.getStringSet("portfolio", null);
                            if (portfolio != null) {
                                if (!portfolio.contains(stock.toLowerCase())) {
                                    portfolio.add(stock.toLowerCase());
                                    editor.putStringSet("portfolio", portfolio);
                                }
                            }
                            else {
                                Set<String> newPortfolio = new HashSet<>();
                                newPortfolio.add(stock.toLowerCase());
                                editor.putStringSet("portfolio", newPortfolio);
                            }
                            editor.commit();
                            // open success dialog
                            buySellDialog.dismiss();
                            TextView successDetails = successView.findViewById(R.id.successDetails);
                            successDetails.setText("You have successfully bought " + qtyEntered + " shares of " + stock.toUpperCase());
                            successDialog.show();

                        }
                        else if (qtyEntered <= 0) {
                            Toast.makeText(v.getContext(), "Cannot buy less than 0 shares", Toast.LENGTH_SHORT).show();
                        }
                        else if (Double.parseDouble(price.getText().toString())*qtyEntered > amountRemaining) {
                            Toast.makeText(v.getContext(), "Not enough money to buy", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                customView.findViewById(R.id.sell).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        float qtyInPortfolio = sharedPreferences.getFloat(stock + "_qty", -1);

                        String qtyEnteredStr = noOfShares.getText().toString();
                        float qtyEntered = 0;
                        if (qtyEnteredStr.length() > 0) {
                            qtyEntered = Float.parseFloat(noOfShares.getText().toString());
                        }

                        double amountRemaining = sharedPreferences.getFloat("amountRemaining", -1.0f);
                        double currentPrice = Double.parseDouble(price.getText().toString());

                        if (qtyEntered > 0 && qtyEntered <= qtyInPortfolio) {
                            // sell
                            double earned = qtyEntered * currentPrice;
                            double updatedAmountRemaining = amountRemaining + earned;
                            float updatedQuantity = qtyInPortfolio - qtyEntered;

                            editor.putFloat("amountRemaining", (float) updatedAmountRemaining);
                            editor.putFloat(stock+"_qty", updatedQuantity);

                            if (updatedQuantity == 0) {
                                // remove stock from portfolio set.
                                Set<String> portfolio = sharedPreferences.getStringSet("portfolio", null);
                                if (portfolio != null) {
                                    portfolio.remove(stock.toLowerCase());
                                    editor.putStringSet("portfolio", portfolio);
                                }
                                else {
                                    Log.e("selling w/o buying", "onClick: Should be unreachable code with usual execution");
                                }
                            }
                            editor.commit();
                            // open success dialog
                            buySellDialog.dismiss();
                            TextView successDetails = successView.findViewById(R.id.successDetails);
                            successDetails.setText("You have successfully sold " + qtyEntered + " shares of " + stock.toUpperCase());
                            successDialog.show();


                        }
                        else if (qtyEntered <= 0) {
                            Toast.makeText(v.getContext(), "Cannot sell less than 0 shares", Toast.LENGTH_SHORT).show();
                        }
                        else if (qtyEntered > qtyInPortfolio) {
                            Toast.makeText(v.getContext(), "Not enough shares to sell", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // show the alert dialog
                buySellDialog.show();

            }
        });

        sendNewsGetRequest();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Set<String> favorites = sharedPreferences.getStringSet("favorites", null);
        if (favorites != null && favorites.contains(stock.toLowerCase())) {
            getMenuInflater().inflate(R.menu.details_menu_favorited, menu);
            favorite = true;
        }
        else {
            getMenuInflater().inflate(R.menu.details_menu_unfavorited, menu);
            favorite = false;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fav_icon:
                Set<String> favorites = sharedPreferences.getStringSet("favorites", null);
                if (favorite == false) {
                    item.setIcon(R.drawable.ic_baseline_star_24);
                    // add to favs
                    if (favorites == null) {
                        favorites = new HashSet<>();
                    }
                    favorites.add(stock.toLowerCase());
                    Toast.makeText(getApplicationContext(), stock.toUpperCase() + " was added to favorites", Toast.LENGTH_SHORT).show();
                }
                else {
                    item.setIcon(R.drawable.ic_baseline_star_border_24);
                    // remove from favs
                    favorites.remove(stock.toLowerCase());
                    Toast.makeText(getApplicationContext(), stock.toUpperCase() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                }
                editor.putStringSet("favorites", favorites);
                Log.d("favorites:", "onOptionsItemSelected: " + favorites);
                editor.commit();
                favorite = !favorite;
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }
}