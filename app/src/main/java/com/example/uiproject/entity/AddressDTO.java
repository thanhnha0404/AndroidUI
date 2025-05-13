package com.example.uiproject.entity;

import java.io.Serializable;

public class AddressDTO  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String street;
    private String district;
    private String ward;
    private String province;

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
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }

}
