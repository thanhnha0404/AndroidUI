package com.example.uiproject.model;

public class Car {
    private String name;
    private String price;
    private int imageResource;
    private int viewCount;
    private boolean isFavorite;

    public Car(String name, String price, int imageResource, int viewCount) {
        this.name = name;
        this.price = price;
        this.imageResource = imageResource;
        this.viewCount = viewCount;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
} 