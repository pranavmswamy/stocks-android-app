package com.example.stocks;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

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

        if(itemType == FIRST) {
            FirstNewsViewHolder firstHolder = (FirstNewsViewHolder) holder;
            firstHolder.website.setText(news.get(position).getStockName());
            firstHolder.timestamp.setText(news.get(position).getTimestamp());
            //firstHolder.image.setImageResource();
            Picasso.with(parentContext).load(news.get(position).getImageUrl()).into(firstHolder.image);
            firstHolder.title.setText(news.get(position).getTitle());
            firstHolder.url = news.get(position).getUrl();
            /*firstHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("lkjhgf", "onClick: clicked---");
                }
            });*/

            /*firstHolder.card.setLongClickable(true);
            firstHolder.card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    LayoutInflater inflater = LayoutInflater.from(v.getContext());
                    final View customView = inflater.inflate(R.layout.news_dialog, null);
                    builder.setView(customView);

                    customView.findViewById(R.id.chrome).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("chromeeeeeee", "onClick: chrome clickedddddddddddd");
                        }
                    });

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });*/

        }
        else if(itemType == NOTFIRST) {
            OtherNewsViewHolder otherHolder = (OtherNewsViewHolder) holder;
            otherHolder.website.setText(news.get(position).getStockName());
            otherHolder.timestamp.setText(news.get(position).getTimestamp());
            Picasso.with(parentContext).load(news.get(position).getImageUrl()).into(otherHolder.image);
            otherHolder.title.setText(news.get(position).getTitle());
            otherHolder.url = news.get(position).getUrl();

            otherHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("lkjhgf", "onClick: clicked---");
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
