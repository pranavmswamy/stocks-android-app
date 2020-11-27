package com.example.stocks;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockListingViewHolder extends RecyclerView.ViewHolder {
    private TextView stockName;
    private TextView stockPrice;
    private TextView change;
    private TextView subtitle;
    ImageButton details;

    public StockListingViewHolder(View stockListingView) {
        super(stockListingView);
        stockName = stockListingView.findViewById(R.id.portfolioHeading);
        stockPrice = stockListingView.findViewById(R.id.stockPrice);
        change = stockListingView.findViewById(R.id.change);
        subtitle = stockListingView.findViewById(R.id.sharesOwned);
        details = stockListingView.findViewById(R.id.detailsButton);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDetails = new Intent(stockListingView.getContext(), DetailsActivity.class);
                goToDetails.putExtra("stock", "goog");
                stockListingView.getContext().startActivity(goToDetails);
            }
        });
    }

    public TextView getStockName() {
        return this.stockName;
    }

    public TextView getStockPrice() {
        return this.stockPrice;
    }

    public TextView getChange() {
        return this.change;
    }

    public TextView getSubtitle() {
        return this.subtitle;
    }
}
