package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PortfolioHeaderViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView amount;

    public PortfolioHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.portfolioHeaderTag);
        amount = itemView.findViewById(R.id.amount);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getAmount() {
        return amount;
    }
}
