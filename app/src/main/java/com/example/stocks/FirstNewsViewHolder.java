package com.example.stocks;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class FirstNewsViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView timestamp;
    TextView website;
    ImageView image;
    CardView card;

    public FirstNewsViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.newsFirstRowTitle);
        timestamp = itemView.findViewById(R.id.newsFirstRowTime);
        website = itemView.findViewById(R.id.newsFirstRowUrl);
        image = itemView.findViewById(R.id.newsFirstRowImage);
        card = itemView.findViewById(R.id.newsFirstRowCard);
    }
}
