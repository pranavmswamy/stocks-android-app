package com.example.stocks;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class Footer extends Section {
    /**
     * Create a Section object based on {@link SectionParameters}.
     */
    public Footer() {
        super(SectionParameters.builder().itemResourceId(R.layout.footer).build());
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
        footerViewHolder.footer.setText("Powered by Tiingo.");

    }
}
