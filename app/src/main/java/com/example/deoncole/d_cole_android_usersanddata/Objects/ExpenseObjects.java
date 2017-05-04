package com.example.deoncole.d_cole_android_usersanddata.Objects;


public class ExpenseObjects {

    String exLocation;
    Double exAmount;
    String exDate;

    public ExpenseObjects(){
    }

    public ExpenseObjects(String exLocation, Double exAmount, String exDate) {
        this.exLocation = exLocation;
        this.exAmount = exAmount;
        this.exDate = exDate;
    }

    public String getExLocation() {
        return exLocation;
    }

    public Double getExAmount() {
        return exAmount;
    }

    public String getExDate() {
        return exDate;
    }
}
