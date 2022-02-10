package com.ohad.smartRoommates.App.Transaction;

public class MyTransactions {

    String senderName ;
    String receiverName;
    String senderID ;
    String receiverID;
    String groupID ;
    String transactionKey ;
    double money;

    public String getSenderID() {
        return senderID;
    }

    public String getGroupID() {
        return groupID;
    }

    public MyTransactions setGroupID(String groupID) {
        this.groupID = groupID;
        return this;
    }

    public MyTransactions setSenderID(String senderID) {
        this.senderID = senderID;
        return this;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public MyTransactions setReceiverID(String receiverID) {
        this.receiverID = receiverID;
        return this;
    }

    public MyTransactions() {
    }

    public String getSenderName() {
        return senderName;
    }

    public MyTransactions setSenderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public MyTransactions setReceiverName(String receiverName) {
        this.receiverName = receiverName;
        return this;
    }

    public double getMoney() {
        return money;
    }

    public MyTransactions setMoney(double money) {
        this.money = money;
        return this;
    }

    @Override
    public String toString() {
        return "MyTransactions{" +
                "senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", senderID='" + senderID + '\'' +
                ", reciverID='" + receiverID + '\'' +
                ", groupID='" + groupID + '\'' +
                ", transactionKey='" + transactionKey + '\'' +
                ", money=" + money +
                '}';
    }

    public MyTransactions setTransactionKey(String key) {
        this.transactionKey = key;
        return this;
    }
    public String getTransactionKey(){
        return transactionKey;
    }
}
