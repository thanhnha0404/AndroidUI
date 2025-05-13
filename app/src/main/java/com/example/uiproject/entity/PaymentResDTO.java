package com.example.uiproject.entity;

import com.google.gson.annotations.SerializedName;

public class PaymentResDTO {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("url")
    private String url;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getURL() {
        return url;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setURL(String url) {
        this.url = url;
    }
}
