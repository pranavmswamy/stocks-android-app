package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HeaderViewHolder extends RecyclerView.ViewHolder {

    private TextView title;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.header);
    }

    public TextView getTitle() {
        return this.title;
    }
}
