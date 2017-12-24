package com.ashutosh.ndkpixels;

/**
 * Created by Vostro-Daily on 12/25/2017.
 */

public class ItemContent {

    private int freq[];
    private int color;
    private String title;

    public ItemContent(int[] freq, int color, String title) {
        this.freq = freq;
        this.color = color;
        this.title = title;
    }

    public int[] getFreq() {
        return freq;
    }

    public int getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }
}
