package com.ohad.smartRoommates.App.Groups;

import android.util.Log;

import com.ohad.smartRoommates.App.User.MyUser;

import java.util.ArrayList;

public class MyGroup {

    private ArrayList<MyUser> allUsers ;
    private String groupName ;
    private String groupId ;

    public String getGroupId() {
        return groupId;
    }

    public MyGroup() {
       allUsers = new ArrayList<>() ;
    }


    public MyGroup addUsers(MyUser user) {
        this.allUsers.add(user);
        return this;
    }

    public ArrayList<MyUser> getAllUsers() {
        return allUsers;
    }

    public MyGroup setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public MyGroup setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }
    public String getGroupMembersName(){
        String result = "";
        for(MyUser user : allUsers)
            result += user.getFirstName() + " " + user.getLastName() + '\n';
        return result;
    }

    public ArrayList<String> getListOfPhones(){
        ArrayList<String> res = new ArrayList<>();
        for(MyUser user : allUsers)
            res.add(user.getPhNumber());
        return res;
    }

    public MyGroup addUsersList(ArrayList<MyUser> users) {
        allUsers.addAll(users);
        for(MyUser u : allUsers)
            Log.d("TAG2", "addUsersList: "+u.toString() );
        return this;
    }

    @Override
    public String toString() {
        return "MyGroup{" +
                "allUsers=" + allUsers +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
