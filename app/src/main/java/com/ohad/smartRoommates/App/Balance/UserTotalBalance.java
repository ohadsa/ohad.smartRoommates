package com.ohad.smartRoommates.App.Balance;

public class UserTotalBalance {

    private String userName ;
    private String userId ;
    private int balance ;

    public UserTotalBalance(){}

    public String getUserId() {
        return userId;
    }

    public UserTotalBalance setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public UserTotalBalance setUserName(String userName) {
        this.userName = userName;
        return this;
    }



    public int getBalance() {
        return balance;
    }

    public UserTotalBalance setBalance(int balance) {
        this.balance = balance;
        return this;
    }

}
