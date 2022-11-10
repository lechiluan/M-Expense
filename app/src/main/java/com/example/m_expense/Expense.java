package com.example.m_expense;

import java.io.Serializable;

public class Expense implements Serializable {
    private int idExpense;
    private String TypeExpense;
    private String Date;
    private float Amount;
    private String Note;
    private int tripID;

    public Expense() {

    }


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

    public Expense(int id, String type, String des, String date, String note, float amount, int tripID) {
        this.idExpense = id;
        this.TypeExpense = type;
        this.Date = date;
        this.Note = note;
        this.Amount = amount;
        this.tripID = tripID;
    }
    public Expense(String type, String des, String date, String note, float amount, int tripID) {
        this.TypeExpense = type;
        this.Date = date;
        this.Note = note;
        this.Amount = amount;
        this.tripID = tripID;
    }

//    @Override
//    public String toString() {
//        return "Expense{" +
//                "id=" + idExpense +
//                ", type='" + TypeExpense + '\'' +
//                ", des='" + DestinationExpense + '\'' +
//                ", dateFrom='" + Date + '\'' +
//                ", risk='" + Amount + '\'' +
//                ", tripID='" + tripID + '\'' +
//                '}';
//    }
}