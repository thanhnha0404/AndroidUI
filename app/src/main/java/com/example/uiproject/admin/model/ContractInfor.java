package com.example.uiproject.admin.model;

import java.util.Date;

public class ContractInfor {
    private Long id;
    private String status;
    private Date dateFrom;
    private Date dateTo;
    private Long price;
    private String nameCustomer;
    private String nameCar;
    private String indentifyCar;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getDateFrom() { return dateFrom; }
    public void setDateFrom(Date dateFrom) { this.dateFrom = dateFrom; }

    public Date getDateTo() { return dateTo; }
    public void setDateTo(Date dateTo) { this.dateTo = dateTo; }

    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }

    public String getNameCustomer() { return nameCustomer; }
    public void setNameCustomer(String nameCustomer) { this.nameCustomer = nameCustomer; }

    public String getNameCar() { return nameCar; }
    public void setNameCar(String nameCar) { this.nameCar = nameCar; }

    public String getIndentifyCar() { return indentifyCar; }
    public void setIndentifyCar(String indentifyCar) { this.indentifyCar = indentifyCar; }
} 