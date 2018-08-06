package com.and.ibrahim.teleprompter.mvp.model;

public class DataObj {
    private int id;
    private int isChecked;
    private String textTitle, textContent;

    public String getTextContent() {
        return textContent;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

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

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

}
