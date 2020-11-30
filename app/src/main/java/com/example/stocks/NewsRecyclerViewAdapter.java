package com.example.stocks;

import android.app.AlertDialog;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FIRST = 1;
    private static final int NOTFIRST = 0;
    ArrayList<NewsDataModel> news;
    Context parentContext;

    public NewsRecyclerViewAdapter(ArrayList<NewsDataModel> news, Context context) {

        //this.news = new ArrayList<>(Arrays.asList("rgege", "wrfwrf", "wfwf"));
        this.news = news;
        parentContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == FIRST) {
            View firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_first_row, parent,false);
            FirstNewsViewHolder viewHolder = new FirstNewsViewHolder(firstView);
            return viewHolder;
        }
        else if(viewType == NOTFIRST) {
            View notFirstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_other_rows, parent, false);
            OtherNewsViewHolder viewHolder = new OtherNewsViewHolder(notFirstView);
            return viewHolder;
        }
        else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);

        String timeText = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date postedDate = inputFormat.parse(news.get(position).getTimestamp());
            Date now = new Date();
            long dateDiff = now.getTime() - postedDate.getTime();

            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);
            if (minute < 24*60) {
                timeText = minute + " minutes ago";
            }  else {
                timeText = day+" days ago";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(itemType == FIRST) {
            FirstNewsViewHolder firstHolder = (FirstNewsViewHolder) holder;
            firstHolder.website.setText(news.get(position).getStockName());
            firstHolder.timestamp.setText(timeText);
            Picasso.with(parentContext).load(news.get(position).getImageUrl()).resize(1920, 1080)
                    .onlyScaleDown().into(firstHolder.image);
            firstHolder.title.setText(news.get(position).getTitle());
            firstHolder.url = news.get(position).getUrl();
        }
        else if(itemType == NOTFIRST) {
            OtherNewsViewHolder otherHolder = (OtherNewsViewHolder) holder;
            otherHolder.website.setText(news.get(position).getStockName());
            otherHolder.timestamp.setText(timeText);
            Picasso.with(parentContext).load(news.get(position).getImageUrl()).resize(1920, 1080)
                    .onlyScaleDown().into(otherHolder.image);
            otherHolder.title.setText(news.get(position).getTitle());
            otherHolder.url = news.get(position).getUrl();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return FIRST;
        }
        else {
            return NOTFIRST;
        }
    }

    @Override
    public int getItemCount() {
        return news.size();
    }
}
