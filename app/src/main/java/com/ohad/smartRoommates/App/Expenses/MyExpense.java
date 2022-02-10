package com.ohad.smartRoommates.App.Expenses;

import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;

import java.util.ArrayList;

public class MyExpense {

    private String group;
    private int cost ;
    private String description ;
    private String payer ;
    private String expenseId;


    public MyExpense(){
        this.payer = MyFireBaseRTDB.getMyPhoneNumber();
    }

    public String getExpenseId() {
        return expenseId;
    }

    public MyExpense setPayer(String payer) {
        this.payer = payer;
        return this;
    }

    public MyExpense setExpenseId(String expenseId) {
        this.expenseId = expenseId;
        return this;
    }

    public String getPayer() {
        return payer;
    }

    public String getGroup() {
        return group;
    }

    public MyExpense setGroup(String group) {
        this.group = group;
        return this;
    }

    public double getCost() {
        return cost;
    }

    public MyExpense setCost(int cost) {
        this.cost = cost;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MyExpense setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "MyExpense{" +
                "group='" + group + '\'' +
                ", cost=" + cost +
                ", description='" + description + '\'' +
                ", payer='" + payer + '\'' +
                ", ExpenseId='" + expenseId + '\'' +
                '}';
    }
}
