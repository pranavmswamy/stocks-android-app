package com.example.stocks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class DetailsActivity extends AppCompatActivity {

    RecyclerView newsRecyclerView;
    NewsRecyclerViewAdapter newsRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsRecyclerViewAdapter = new NewsRecyclerViewAdapter();
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        newsRecyclerView.setAdapter(newsRecyclerViewAdapter);
    }
}