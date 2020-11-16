package com.example.stocks;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FooterViewHolder extends RecyclerView.ViewHolder {

    TextView footer;
    LinearLayout footerLayout;

    public FooterViewHolder(@NonNull View itemView) {
        super(itemView);
        footer = itemView.findViewById(R.id.footer);
        footerLayout = itemView.findViewById(R.id.footerLayout);
        footerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiingo.com")));
            }
        });
    }
}
