package com.example.uiproject.admin;

public class Car {
    private String name;
    private String price;
    private int imageResource;
    private int viewCount;
    private boolean isHot;
    private boolean isFavorite;

    public Car(String name, String price, int imageResource, int viewCount, boolean isHot) {
        this.name = name;
        this.price = price;
        this.imageResource = imageResource;
        this.viewCount = viewCount;
        this.isHot = isHot;
        this.isFavorite = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
} 