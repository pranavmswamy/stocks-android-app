package com.example.stocks;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
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
    String url;

    public FirstNewsViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.newsFirstRowTitle);
        timestamp = itemView.findViewById(R.id.newsFirstRowTime);
        website = itemView.findViewById(R.id.newsFirstRowUrl);
        image = itemView.findViewById(R.id.newsFirstRowImage);
        card = itemView.findViewById(R.id.newsFirstRowCard);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                final View customView = inflater.inflate(R.layout.news_dialog, null);
                builder.setView(customView);

                customView.findViewById(R.id.chrome).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                });

                customView.findViewById(R.id.twitter).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=" + "Check%20out%20this%20Link:%20" + url + "%20#CSCI571StockApp")));
                    }
                });

                ImageView img = customView.findViewById(R.id.dialogImage);
                img.setImageDrawable(image.getDrawable());

                TextView dialogTitle = customView.findViewById(R.id.dialogTitle);
                dialogTitle.setText(title.getText());

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

    }
}
