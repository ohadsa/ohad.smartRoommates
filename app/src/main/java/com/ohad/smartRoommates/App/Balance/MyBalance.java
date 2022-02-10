package com.ohad.smartRoommates.App.Balance;

import com.ohad.smartRoommates.App.Expenses.MyExpense;
import com.ohad.smartRoommates.App.Transaction.MyTransactions;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;

import java.util.ArrayList;
import java.util.Comparator;

public class MyBalance {

    String myUser ;
    String MyGroupID ;
    ArrayList<MyExpense> allExpenses ;
    ArrayList<String> users ;
    ArrayList<MyTransactions> notPaidTransactions ;
    int numOfMember;
    int MyTotal = 0;

    public MyBalance(){}

    public MyBalance setNotPaidTransactions(ArrayList<MyTransactions> notPaidTransactions) {
        this.notPaidTransactions = notPaidTransactions;
        return this;
    }

    public String getMyUser() {
        return myUser;
    }

    public MyBalance setUsers(ArrayList<String> users) {
        this.users = users;
        return this;
    }

    public MyBalance setNumOfMember(int numOfMember) {
        this.numOfMember = numOfMember;
        return this;
    }

    public MyBalance setMyUser(String myUser) {
        this.myUser = myUser;
        return this;
    }

    public String getMyGroupID() {
        return MyGroupID;
    }

    public MyBalance setMyGroupID(String myGroupID) {
        MyGroupID = myGroupID;
        return this;
    }



    public int getTotalBalance() {
        return  MyTotal;
    }

    public interface Callback_get_get_Transactions_List{
        void dataReady(ArrayList<MyTransactions> value);
    }


    public Comparator<UserTotalBalance> getUserTotalComparator(){
        return new Comparator<UserTotalBalance>() {
            @Override
            public int compare(UserTotalBalance o1, UserTotalBalance o2) {
                if( (o2.getBalance() - o1.getBalance()) < 0 ){
                    return 1;
                }
                else if((o2.getBalance() - o1.getBalance()) > 0 ){
                    return -1;
                }
                else{
                    return 0 ;
                }
            }
        };

    }
    public ArrayList<MyExpense> getAllExpenses() {
        return allExpenses;
    }

    public MyBalance setAllExpenses(ArrayList<MyExpense> allExpenses) {
        this.allExpenses = allExpenses;
        return this;
    }

    @Override
    public String toString() {
        String s ="";
        for( MyExpense e : allExpenses)
            s+=e.toString()+"\n";
        return "MyBalance{" +
                "myUser='" + myUser + '\'' +
                ", MyGroupID='" + MyGroupID + '\'' +
                ", allExpenses=" + s +
                '}';
    }





    public void getTransactionsList(Callback_get_get_Transactions_List callback) {

        ArrayList<MyTransactions>  transactions = new ArrayList<>();
        Comparator<UserTotalBalance> comparator = getUserTotalComparator();
        MyFireBaseRTDB.getUsersTotalBalanceByGroupID(getMyGroupID(), users, new MyFireBaseRTDB.Callback_Get_users_balance() {
            @Override
            public void dataReady(ArrayList<UserTotalBalance> value) {
                value =  manipulate_List_user_balance(value , comparator);
                boolean x = true ;
                int iOwe = 0 ;
                int OweTome = 0 ;
                while (x){
                    value.sort(comparator);
                    int sender = value.get(0).getBalance();
                    int receiver = value.get(value.size()-1).getBalance();
                    if(sender == 0 ) {
                        x = false;
                    }
                    else {
                        int amount = Math.min(sender * -1 , receiver ) ;
                        if(amount == receiver ){
                            int tmp = value.get(0).getBalance() ;
                            value.get(value.size()-1).setBalance(0);
                            value.get(0).setBalance(amount + tmp);
                        }
                        else {
                            value.get(0).setBalance(0);
                            int tmp = value.get(value.size() - 1).getBalance();
                            value.get(value.size() - 1).setBalance(tmp - amount);
                        }

                        if( value.get(value.size()-1).getUserId().equals(myUser)){
                            OweTome+= amount;
                        }
                        else if(value.get(0).getUserId().equals(myUser)){
                            iOwe+= amount;
                        }
                       MyTransactions val =  new MyTransactions()
                                .setReceiverName(value.get(value.size()-1).getUserName())
                                .setSenderName(value.get(0).getUserName())
                                .setSenderID(value.get(0).getUserId())
                                .setReceiverID(value.get(value.size()-1).getUserId())
                                .setGroupID(getMyGroupID())
                                .setMoney(amount);

                        transactions.add(val);
                    }
                }
                MyTotal = OweTome - iOwe ;
                callback.dataReady(transactions);

            }
        });
        this.notPaidTransactions = transactions;
    }

    private ArrayList<UserTotalBalance> manipulate_List_user_balance(ArrayList<UserTotalBalance> usersBalance, Comparator<UserTotalBalance> comparator) {

        int average = getAverageAmount(usersBalance);
        int tempo = 0 ;
        for(UserTotalBalance u : usersBalance){
            int tmp =  u.getBalance() - average ;
            u.setBalance(tmp);
            tempo+=u.getBalance();
        }
        usersBalance.sort(comparator);
        if(tempo != 0 ){
            int tmp = usersBalance.get(usersBalance.size()-1).getBalance() - tempo ;
            usersBalance.get(usersBalance.size()-1).setBalance(tmp);
        }
        return usersBalance;
    }


    private int getAverageAmount(ArrayList<UserTotalBalance> usersBalance) {
        int x = 0 ;
        int counter = 0;
        for(UserTotalBalance u :usersBalance){
            x+=u.getBalance();
            counter++;
        }
        return x/counter ;
    }
}
