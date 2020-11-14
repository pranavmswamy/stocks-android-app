package com.example.stocks;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class OtherNewsViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView timestamp;
    TextView website;
    ImageView image;
    CardView card;

    public OtherNewsViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.newsOtherTitle);
        timestamp = itemView.findViewById(R.id.newsOtherTime);
        website = itemView.findViewById(R.id.newsOtherUrl);
        image = itemView.findViewById(R.id.newsOtherImage);
        card = itemView.findViewById(R.id.newsOtherCardView);
    }
}
