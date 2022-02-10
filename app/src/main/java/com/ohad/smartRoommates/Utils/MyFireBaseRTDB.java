package com.ohad.smartRoommates.Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohad.smartRoommates.App.Transaction.MyTransactions;
import com.ohad.smartRoommates.App.Balance.UserTotalBalance;
import com.ohad.smartRoommates.App.Expenses.MyExpense;
import com.ohad.smartRoommates.App.Groups.MyGroup;
import com.ohad.smartRoommates.App.User.MyUser;

import java.util.ArrayList;


public class MyFireBaseRTDB {


    public static String getMyPhoneNumber() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        return null;
    }


    public static void saveNewExpense(MyExpense tmp) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String id = db.getReference("Groups/").child(tmp.getGroup()).child("Expenses").push().getKey();
        tmp.setExpenseId(id);
        db.getReference("Groups/").child(tmp.getGroup()).child("Expenses").child(id).setValue(tmp);

        DatabaseReference myRef = db.getReference("Groups/").child(tmp.getGroup()).child("MembersTotalAmount").child(tmp.getPayer());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double val;
                try {
                    val = snapshot.getValue(Double.class);
                    db.getReference("Groups/").child(tmp.getGroup()).child("MembersTotalAmount").child(tmp.getPayer()).setValue(val + tmp.getCost());
                } catch (Exception e) {
                    db.getReference("Groups/").child(tmp.getGroup()).child("MembersTotalAmount").child(tmp.getPayer()).setValue(tmp.getCost());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void saveGroup(MyGroup group) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String reference = db.getReference("Groups/").push().getKey();

        db.getReference("Groups/" + reference + "/groupName").setValue(group.getGroupName());
        db.getReference("Groups/" + reference + "/groupID").setValue(reference);
        for (String number : group.getListOfPhones()) {
            db.getReference("Users/").child(number).child("MyGroups").child(reference).setValue(reference);
            db.getReference("Groups/" + reference + "/members").child(number).setValue(number);
        }
    }

    public static void getNumOfMemberByGroupId(String myGroupID, Callback_Get_num_of_members callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups/" + myGroupID).child("members");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> result = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String val = child.getValue(String.class);
                    result.add(val);
                }

                callback.dataReady(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void getUsersTotalBalanceByGroupID(String myGroupID, ArrayList<String> users, Callback_Get_users_balance callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserTotalBalance> usersBalance = new ArrayList<>();
                for (String s : users) {
                    UserTotalBalance val = new UserTotalBalance().setUserName(snapshot.child("Users/").child(s).child("firstName").getValue(String.class) + " " + snapshot.child("Users/").child(s).child("lastName").getValue(String.class))
                            .setUserId(s);


                    try {
                        val.setBalance(snapshot.child("Groups/").child(myGroupID).child("MembersTotalAmount").child(s).getValue(int.class));
                    } catch (Exception e) {
                        val.setBalance(0);
                    }
                    usersBalance.add(val);

                }
                callback.dataReady(usersBalance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static void getUserNameById(String phone, Callback_user_name callback) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/" + phone);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result = null;
                try {
                    com.ohad.smartRoommates.App.User.MyUser tmp = snapshot.getValue(com.ohad.smartRoommates.App.User.MyUser.class);
                    result = tmp.getFirstName() + " " + tmp.getLastName();
                } catch (Exception ex) {
                }
                callback.dataReady(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public static void getUserImage(Callback_Get_users_Img callback) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/" + getMyPhoneNumber() + "/userImg");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.dataReady(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public static void deleteGroup(MyGroup group) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups/");
        myRef.child(group.getGroupId()).removeValue();
        for (MyUser u : group.getAllUsers()) {
            myRef = database.getReference("Users/").child(u.getPhNumber()).child("MyGroups").child(group.getGroupId());
            myRef.removeValue();
        }
    }

    public static void getGroupNameById(String groupID, Callback_Get_Group_name callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups/").child(groupID).child("groupName");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.dataReady(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void get_all_users_of_group(String groupID, Callback_get_all_users_of_group callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            ArrayList<MyUser> users = new ArrayList<>();
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot groupList = snapshot.child("Groups").child(groupID).child("members");
                try {
                    for (DataSnapshot child : groupList.getChildren()) {
                        String phone = child.getValue(String.class);
                        MyUser user = snapshot.child("Users").child(phone).getValue(MyUser.class);
                        users.add(user);

                    }

                } catch (Exception ex) {

                    Log.d("TAG_111", "####-ERROR----");

                }
                callback.dataReady(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void deleteExpense(MyExpense expense) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Groups/").child(expense.getGroup()).child("Expenses").child(expense.getExpenseId()).removeValue();
        DatabaseReference myRef = database.getReference("Groups/").child(expense.getGroup());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    double tmp = snapshot.child("MembersTotalAmount").child(expense.getPayer()).getValue(double.class) - expense.getCost();
                    database.getReference("Groups/").child(expense.getGroup()).child("MembersTotalAmount").child(expense.getPayer()).setValue(tmp);
                }
                catch (Exception e){
                    database.getReference("Groups/").child(expense.getGroup()).child("MembersTotalAmount").child(expense.getPayer()).setValue(-1*expense.getCost());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public static void deleteUserFromGroup(String groupID, String phNumber) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Groups").child(groupID).child("MembersTotalAmount").removeValue();
        myRef.child("Groups").child(groupID).child("members").child(phNumber).removeValue();
        myRef.child("Users").child(phNumber).child("MyGroups").child(groupID).removeValue();


    }

    public static void isGroupBalanced(String groupID, CallBack_balanced callback) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups/").child(groupID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Boolean flag = true;
                int groupSize = 0 ;

                double val = 0;
                for(DataSnapshot b : snapshot.child("members").getChildren()){
                    groupSize+=1;
                }
                if(snapshot.child("MembersTotalAmount").getChildren().iterator().hasNext()){
                    val = snapshot.child("MembersTotalAmount").getChildren().iterator().next().getValue(double.class);
                }

                for (DataSnapshot d : snapshot.child("MembersTotalAmount").getChildren()) {
                    for(DataSnapshot f : snapshot.child("MembersTotalAmount").getChildren()) {
                        if (d.getValue(double.class) - val >= groupSize || d.getValue(double.class) - val  <= -1*groupSize ) {
                            flag = false;
                        }
                    }
                }

                callback.dataReady(flag);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void addUserToGroup(String groupID, String phone , Callback_Add_User_to_Group callback) {

        getUserByPhoneNumber(phone, new CallBack_user() {
            @Override
            public void dataReady(MyUser value) {
                if(value == null )
                    callback.onFail(1);
                else{

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                String user = snapshot.child("Groups").child(groupID).child("members").child(phone).getValue(String.class);

                                if(user == null){
                                    myRef.child("Groups").child(groupID).child("members").child(phone).setValue(phone);
                                    callback.onSuccesses();

                                    myRef.child("Users").child(phone).child("MyGroups").child(groupID).setValue(groupID);
                                }
                                else{
                                    callback.onFail(2);
                                }
                            }
                            catch (Exception e ){

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }

    public static void changeGroupName(String groupID, String name) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Groups/").child(groupID).child("groupName").setValue(name);

    }

    public static void getExpenseById(String expenseId, String groupID , Callback_get_Expense_By_Id callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups/").child(groupID).child("Expenses").child(expenseId);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.dataReady(snapshot.getValue(MyExpense.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void EditExpense(MyExpense expense, String description, double cost ) {
        double delta = cost - expense.getCost() ;
        expense.setCost((int)cost);
        expense.setDescription(description);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Groups/").child(expense.getGroup()).child("Expenses").child(expense.getExpenseId()).setValue(expense);
        DatabaseReference myRef = database.getReference("Groups/").child(expense.getGroup()).child("MembersTotalAmount").child(expense.getPayer());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double data = snapshot.getValue(int.class);
                Log.d("OHADSSS", "onDataChange: " + data + "<-data || delta->"+delta);
                database.getReference("Groups/").child(expense.getGroup()).child("MembersTotalAmount").child(expense.getPayer()).setValue(data + delta);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void saveTransaction(MyTransactions transaction , Callback_Save_Transaction callback ) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups/").child(transaction.getGroupID());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("OHADDS", "onDataChange: " +transaction.toString());
                double senderTotal = 0 ;
                double receiverTotal = 0 ;
                try {
                    receiverTotal = snapshot.child("MembersTotalAmount").child(transaction.getReceiverID()).getValue(double.class);
                }
                catch (Exception e){}


                try {
                    senderTotal = snapshot.child("MembersTotalAmount").child(transaction.getSenderID()).getValue(double.class);
                }
                catch (Exception e ){}
                myRef.child("MembersTotalAmount").child(transaction.getSenderID()).setValue(senderTotal + transaction.getMoney());
                myRef.child("MembersTotalAmount").child(transaction.getReceiverID()).setValue(receiverTotal - transaction.getMoney());
                String key = myRef.child("MoneyTransactions").push().getKey();
                transaction.setTransactionKey(key);
                myRef.child("MoneyTransactions").child(key).setValue(transaction);
                callback.onSuccesses();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getAllMoneyTransactions(String groupID , Callback_Get_All_Transactions callback ) {
        ArrayList<MyTransactions> transactions = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups/").child(groupID).child("MoneyTransactions");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        MyTransactions transaction = child.getValue(MyTransactions.class);
                        transactions.add(transaction);
                    } catch (Exception ex) {}

                }
                Log.d("DELETE", "in database__________________");
                for(MyTransactions t : transactions)
                    Log.d("DELETE", t.toString());
                Log.d("DELETE", "____________________");
                callback.dataReady(transactions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void deleteTransaction(MyTransactions transaction , Callback_Delete_transaction callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups/").child(transaction.getGroupID());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    double oldVal = snapshot.child("MembersTotalAmount").child(transaction.getSenderID()).getValue(double.class);
                    myRef.child("MembersTotalAmount").child(transaction.getSenderID()).setValue(oldVal - transaction.getMoney());
                }catch (Exception e){
                    myRef.child("MembersTotalAmount").child(transaction.getSenderID()).setValue(-1 * transaction.getMoney());
                }

                try{
                    double oldVal = snapshot.child("MembersTotalAmount").child(transaction.getReceiverID()).getValue(double.class);
                    myRef.child("MembersTotalAmount").child(transaction.getReceiverID()).setValue(oldVal + transaction.getMoney());

            }catch (Exception e){
                myRef.child("MembersTotalAmount").child(transaction.getReceiverID()).setValue( transaction.getMoney());
            }


                myRef.child("MoneyTransactions").child(transaction.getTransactionKey()).removeValue();
                callback.onSuccesses();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void EditUser(MyUser user , Callback_Edit_User callback) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Users").child(user.getPhNumber());
        myRef.child("email").setValue(user.getEmail());
        myRef.child("firstName").setValue(user.getFirstName());
        myRef.child("lastName").setValue(user.getLastName());
        myRef.child("userImg").setValue(user.getUserImg());
        callback.onSuccesses();

    }

    public interface Callback_Edit_User {
        void onSuccesses();
    }
    public interface Callback_Delete_transaction {
        void onSuccesses();
    }

    public interface Callback_Get_All_Transactions {
        void dataReady(ArrayList<MyTransactions> value);
    }

    public interface Callback_Save_Transaction {
        void onSuccesses();
    }

    public interface Callback_get_Expense_By_Id {
        void dataReady(MyExpense value);
    }

    public interface Callback_get_all_users_of_group {
        void dataReady(ArrayList<MyUser> value);
    }


    public interface Callback_Get_users_balance {
        void dataReady(ArrayList<UserTotalBalance> value);
    }


    public interface Callback_Get_Group_name {
        void dataReady(String value);
    }

    public interface Callback_Add_User_to_Group {
        void onSuccesses();
        void onFail(int error);
    }


    public interface Callback_Get_users_Img {
        void dataReady(String value);
    }

    public interface Callback_Get_num_of_members {
        void dataReady(ArrayList<String> value);
    }

    public interface callbackAllExpenses {
        void dataReady(ArrayList<MyExpense> value);
    }

    public interface Callback_user_name {
        void dataReady(String value);
    }

    public interface CallBack_user {
        void dataReady(com.ohad.smartRoommates.App.User.MyUser value);
    }

    public interface CallBack_groupList{
        void dataReady(ArrayList<MyGroup> value);
    }
    public interface CallBack_balanced{
        void dataReady(Boolean value);
    }

    public interface CallBack_ExpensesList{
        void dataReady(ArrayList<MyExpense> value);
    }


    public static void getAllExpenses(String myGroupId , callbackAllExpenses callback) {

        ArrayList<MyExpense> expenses = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups/").child(myGroupId).child("Expenses");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        MyExpense expense = child.getValue(MyExpense.class);
                        expenses.add(expense);
                    } catch (Exception ex) {}

                }
                callback.dataReady(expenses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public static void getAllGroupsOfUser(String phNumber, CallBack_groupList callBack_groupList) {

        ArrayList<MyGroup> groups = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/"+phNumber +"/MyGroups");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        String groupID = child.getValue(String.class);
                        addGroupByIdToList(groupID , groups , callBack_groupList);
                    } catch (Exception ex) {}

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private static void addGroupByIdToList(String groupID, ArrayList<MyGroup> groups, CallBack_groupList callBack_groupList) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups/").child(groupID);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String GroupName =snapshot.child("groupName").getValue(String.class);
                MyGroup tmp = new MyGroup()
                        .setGroupName(GroupName)
                        .setGroupId(groupID);
                for(DataSnapshot child : snapshot.child("members").getChildren()){
                    try {

                        String phone = child.getValue(String.class);
                        addUserToGroupByPhone(phone , tmp , callBack_groupList , groups  );
                    } catch (Exception ex) {}
                }
                groups.add(tmp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void addUserToGroupByPhone(String phone, MyGroup tmp, CallBack_groupList callBack_groupList, ArrayList<MyGroup> groups) {
        getUserByPhoneNumber(phone, new CallBack_user() {
            @Override
            public void dataReady(com.ohad.smartRoommates.App.User.MyUser value) {
                if(value!=null) {
                    tmp.addUsers(value);
                    callBack_groupList.dataReady(groups);
                }
            }
        });
    }


    public static void getUserByPhoneNumber(String phone , CallBack_user callBack_user ){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/" + phone);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                com.ohad.smartRoommates.App.User.MyUser result = null;
                    try {
                        result = snapshot.getValue(com.ohad.smartRoommates.App.User.MyUser.class);
                    } catch (Exception ex) {}
                callBack_user.dataReady(result);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    public static void saveNewUser(com.ohad.smartRoommates.App.User.MyUser user) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("Users/"+user.getPhNumber()).setValue(user);

    }


}
