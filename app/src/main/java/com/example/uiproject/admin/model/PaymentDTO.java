package com.example.uiproject.admin.model;

import java.util.Date;

public class PaymentDTO {
    private int id;
    private Date date;
    private Long price;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public Long getPrice() {
        return price;
    }
    public void setPrice(Long price) {
        this.price = price;
    }
}
