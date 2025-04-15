package com.example.uiproject.admin.model;

public class Brand {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String logo;
    private String status;

    public Brand(Long id, String name, String code, String description, String logo, String status) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.logo = logo;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
