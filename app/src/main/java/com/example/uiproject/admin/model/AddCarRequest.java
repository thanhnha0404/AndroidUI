package com.example.uiproject.admin.model;

import java.util.List;

public class AddCarRequest {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String indentify;
    private String province;
    private String street;
    private String district;
    private String ward;
    private Long brandId;
    private Long lineId;
    private Long price;
    List<String> imageUrls;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIndentify() {
        return indentify;
    }

    public void setIndentify(String indentify) {
        this.indentify = indentify;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public AddCarRequest() {
    }

    public AddCarRequest(Long id, String name, String description, String status, String indentify, String province, String street, String district, String ward, Long brandId, Long lineId, Long price, List<String> imageUrls) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.indentify = indentify;
        this.province = province;
        this.street = street;
        this.district = district;
        this.ward = ward;
        this.brandId = brandId;
        this.lineId = lineId;
        this.price = price;
        this.imageUrls = imageUrls;
    }

    public AddCarRequest( String name, String description, String status, String indentify, String province, String street, String district, String ward, Long brandId, Long lineId, Long price, List<String> imageUrls) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.indentify = indentify;
        this.province = province;
        this.street = street;
        this.district = district;
        this.ward = ward;
        this.brandId = brandId;
        this.lineId = lineId;
        this.price = price;
        this.imageUrls = imageUrls;
    }
}
