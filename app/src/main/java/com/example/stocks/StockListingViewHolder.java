package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockListingViewHolder extends RecyclerView.ViewHolder {
    private TextView stockName;
    private TextView stockPrice;
    private TextView change;
    private TextView subtitle;

    public StockListingViewHolder(View stockListingView) {
        super(stockListingView);
        stockName = stockListingView.findViewById(R.id.stock);
        stockPrice = stockListingView.findViewById(R.id.stockPrice);
        change = stockListingView.findViewById(R.id.change);
        subtitle = stockListingView.findViewById(R.id.subtitle);
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
