package com.ohad.smartRoommates.App.User;

public class MyUser {
    private String firstName;
    private String lastName;
    private String email;
    private String phNumber ;
    private String userImg;

    public String getPhNumber() {
        return phNumber;
    }

    public MyUser setPhNumber(String phNumber) {
        this.phNumber = phNumber;
        return this;
    }

    public MyUser() {}

    public String getLastName() {
        return lastName;
    }

    public MyUser setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public MyUser setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public MyUser setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String  toString() {
        return "MyUser{" +
                "firstName='" + firstName + '\'' +
                ", LastName='" + lastName + '\'' +
                ", Email='" + email + '\'' +
                ", phNumber='" + phNumber + '\'' +
                '}';
    }

    public MyUser setUserImg(String userImg) {
        this.userImg = userImg;
        return this;
    }

    public String getUserImg() {
        return userImg;
    }
}
