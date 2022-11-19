package com.example.m_expense.Database;

import java.io.Serializable;

public class Expense implements Serializable {
    private int idExpense;
    private String TypeExpense;
    private String Date;
    private float Amount;
    private String Note;
    private int tripID;
    private String location;
    private String imageExpense;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // constructor
    public Expense() {

    }

    // all get and set methods
    public int getId() {
        return idExpense;
    }

    public void setId(int id) {
        this.idExpense = id;
    }

    public String getTypeExpense() {
        return TypeExpense;
    }

    public void setTypeExpense(String type) {
        this.TypeExpense = type;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public Float getAmount() {
        return Amount;
    }

    public void setAmount(Float amount) {
        this.Amount = amount;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        this.Note = note;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }


    public Expense(int id, String type, String date, String note, float amount, int tripID, String location, String imageExpense) {
        this.idExpense = id;
        this.TypeExpense = type;
        this.Date = date;
        this.Note = note;
        this.Amount = amount;
        this.tripID = tripID;
        this.location = location;
        this.imageExpense = imageExpense;
    }

    public Expense(String type, String date, String note, float amount, int tripID, String location, String imageExpense) {
        this.TypeExpense = type;
        this.Date = date;
        this.Note = note;
        this.Amount = amount;
        this.tripID = tripID;
        this.location = location;
        this.imageExpense = imageExpense;
    }

    public String getImageExpense() {
        return imageExpense;
    }

    public void setImageExpense(String imageExpense) {
        this.imageExpense = imageExpense;
    }
}