package com.example.stocks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FIRST = 1;
    private static final int NOTFIRST = 0;
    ArrayList<String> news = new ArrayList<>(Arrays.asList("rgege", "wrfwrf", "wfwf"));

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

        if(itemType == FIRST) {
            FirstNewsViewHolder firstHolder = (FirstNewsViewHolder) holder;
            firstHolder.website.setText("0adada");
            firstHolder.timestamp.setText("dfeaf");
            //firstHolder.image.setImageResource();
            firstHolder.title.setText("affaf");
            firstHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        else if(itemType == NOTFIRST) {
            OtherNewsViewHolder otherHolder = (OtherNewsViewHolder) holder;
            otherHolder.website.setText("other");
            otherHolder.timestamp.setText("other");
            otherHolder.title.setText("other");
            otherHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
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
