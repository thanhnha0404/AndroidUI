package com.example.uiproject.entity;

import java.util.Date;

public class CarDTO {
    private int id;
    private Date dateOfStart;
    private String description;
    private String identify;
    private String name;
    private String picture;
    private int price;
    private boolean isSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public Date getDateOfStart() {
        return dateOfStart;
    }
    public void setDateOfStart(Date dateOfStart) {
        this.dateOfStart = dateOfStart;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getIdentify() {
        return identify;
    }
    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
