package com.example.m_expense.Trip;
import java.io.Serializable;

public class Trip implements Serializable {
    private int id;
    private String name;
    private String destination;
    private String dateFrom;
    private String dateTo;
    private String risk;
    private String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return destination;
    }

    public void setDes(String des) {
        this.destination = des;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String date) {
        this.dateFrom = date;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String date) {
        this.dateTo = date;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Trip(int id, String name, String des, String dateFrom, String dateTo, String risk, String desc) {
        this.id = id;
        this.name = name;
        this.destination = des;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.risk = risk;
        this.desc = desc;
    }

    public Trip(String name, String des, String dateFrom, String dateTo, String risk, String desc) {
        this.name = name;
        this.destination = des;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.risk = risk;
        this.desc = desc;
    }

    public Trip() {
    }
}

