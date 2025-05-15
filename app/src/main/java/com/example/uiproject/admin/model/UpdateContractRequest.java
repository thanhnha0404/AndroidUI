package com.example.uiproject.admin.model;

public class UpdateContractRequest {
    private Long id;
    private String status;

    public UpdateContractRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
