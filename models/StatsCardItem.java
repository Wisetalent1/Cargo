package com.nicargo.app.models;

public class StatsCardItem {
    private String title;
    private String value;
    private String subtitle;
    private int iconRes;
    private int colorRes;
    private int backgroundColorRes;

    public StatsCardItem(String title, String value, String subtitle, int iconRes, int colorRes) {
        this.title = title;
        this.value = value;
        this.subtitle = subtitle;
        this.iconRes = iconRes;
        this.colorRes = colorRes;
    }

    public StatsCardItem(String title, String value, String subtitle, int iconRes, int colorRes, int backgroundColorRes) {
        this.title = title;
        this.value = value;
        this.subtitle = subtitle;
        this.iconRes = iconRes;
        this.colorRes = colorRes;
        this.backgroundColorRes = backgroundColorRes;
    }

    // Getters
    public String getTitle() { return title; }
    public String getValue() { return value; }
    public String getSubtitle() { return subtitle; }
    public int getIconRes() { return iconRes; }
    public int getColorRes() { return colorRes; }
    public int getBackgroundColorRes() { return backgroundColorRes; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setValue(String value) { this.value = value; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setIconRes(int iconRes) { this.iconRes = iconRes; }
    public void setColorRes(int colorRes) { this.colorRes = colorRes; }
    public void setBackgroundColorRes(int backgroundColorRes) { this.backgroundColorRes = backgroundColorRes; }
}