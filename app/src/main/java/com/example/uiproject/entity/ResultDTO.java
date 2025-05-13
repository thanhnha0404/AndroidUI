package com.example.uiproject.entity;

public class ResultDTO<T> {
    private boolean status;
    private String message;
    private T data;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public boolean isStatus() {
        return status;
    }
}

