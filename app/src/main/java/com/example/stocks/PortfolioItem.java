package com.example.stocks;

public class PortfolioItem {
    private float totalValue;
    private String stock;
    private int noOfShares;

    public String getStockName() {
        return this.stock;
    }

    public int getNoOfShares() {
        return this.noOfShares;
    }

    public float getTotalValue() {
        return this.totalValue;
    }
}
