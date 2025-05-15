package com.example.uiproject.admin.model;

public class Payment {
    public int id;
    public double price;
    public String payDate;

    public Payment(int id, double price, String payDate) {
        this.id = id;
        this.price = price;
        this.payDate = payDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }
}
