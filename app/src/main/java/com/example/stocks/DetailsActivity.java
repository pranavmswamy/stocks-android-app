package com.example.stocks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

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


        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            stock = (String) bundle.get("stock");
            stockName.setText(stock.toUpperCase());

            loadChart();
            loadPortfolioSection();






            // Start get request for News
            sendNewsGetRequest();
            sendAboutGetRequest();
            sendLatestPriceGetRequest();


        }
        else {
            stock = "N/A";
        }
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

                    if(jsonObject.getString("mid") == "null") {
                        mid.setText("0.0");
                        bidPrice.setText("0.0");
                    }
                    else {
                        mid.setText(jsonObject.getString("mid"));
                        bidPrice.setText(jsonObject.getString("bidPrice"));
                    }

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

        int quantity = sharedPreferences.getInt(stock.toLowerCase() + "_qty", -1);
        if (quantity > 0) {
            sharesOwned.setText("You have " + quantity + "shares of " + stock.toUpperCase());
        }
        else {
            sharesOwned.setText("You have 0 shares of " + stock.toUpperCase());
        }

        double amount = sharedPreferences.getFloat(stock.toLowerCase() + "_amount", -1.0f);
        if (amount > 0) {
            marketValue.setText(amount + "");
        }
        else {
            marketValue.setText("Start trading!");
        }

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                final View customView = inflater.inflate(R.layout.buy_sell_dialog, null);
                builder.setView(customView);

                TextView buySellDialogTitle = customView.findViewById(R.id.buySellDialogTitle);
                buySellDialogTitle.setText("Trade " + subtitle.getText() + " shares");

                EditText noOfShares = customView.findViewById(R.id.noOfShares);
                TextView amountCalc = customView.findViewById(R.id.amountCalculation);

                noOfShares.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().length() > 0) {
                            amountCalc.setText(Integer.parseInt(s.toString()) + " x $" + price.getText() + "/share = $" + Double.parseDouble(price.getText().toString())*Integer.parseInt(s.toString()));
                        }
                        else {
                            amountCalc.setText("");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });




                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
}