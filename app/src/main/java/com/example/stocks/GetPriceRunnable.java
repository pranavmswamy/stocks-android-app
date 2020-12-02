package com.example.stocks;

public abstract class GetPriceRunnable implements Runnable {

    boolean KILL = false;

    public void killTask() {
        KILL = true;
    }

    public void enableTask() {
        KILL = false;
    }

}
