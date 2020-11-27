package com.example.stocks;

public class NewsDataModel {
    private String stockName;
    private String url;
    private String timestamp;
    private String title;
    private String imageUrl;

    public NewsDataModel(String url, String timestamp, String title, String imageUrl, String name) {
        this.url = url;
        this.timestamp = timestamp;
        this.title = title;
        this.imageUrl = imageUrl;
        this.stockName = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getStockName() {
        return stockName;
    }

}
