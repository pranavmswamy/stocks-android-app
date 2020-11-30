package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.TimeZone;

public class PortfolioHeaderViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView amount;
    TextView date;

    public PortfolioHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.portfolioHeaderTag);
        amount = itemView.findViewById(R.id.amount);
        date = itemView.findViewById(R.id.date);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int dateNum = calendar.get(Calendar.DATE);
        String month = new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)];

        date.setText("" + month + " " + dateNum + ", " + year);

    }

    public TextView getTitle() {
        return title;
    }

    public TextView getAmount() {
        return amount;
    }
}
