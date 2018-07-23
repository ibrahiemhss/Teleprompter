package com.and.ibrahim.teleprompter.mvp.model;

import android.content.Context;

public class Teleprmpter {
    Context mContext;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTextTitle() {
        return textTitle;
    }

    public void setTextTitle(String textTitle) {
        this.textTitle = textTitle;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    private String textTitle,textContent;
}
