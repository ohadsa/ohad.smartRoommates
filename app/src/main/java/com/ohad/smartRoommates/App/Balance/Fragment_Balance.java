package com.ohad.smartRoommates.App.Balance;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ohad.smartRoommates.App.Expenses.MyExpense;
import com.ohad.smartRoommates.App.Transaction.MyTransactions;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;

import java.util.ArrayList;

public class Fragment_Balance extends Fragment {

    public interface Callback_Balance{
        void addErrorLabel();
        void settled();
    }

    private Callback_Balance callback ;
    private AppCompatActivity activity ;
    private String MyGroupID ;
    private MyBalance balance ;
    private RecyclerView  main_LST_balance;
    private TextView total_LBL_balance;

    public Fragment_Balance setCallback(Callback_Balance callback) {
        this.callback = callback;
        return this;
    }


    public Fragment_Balance setActivity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }


    public Fragment_Balance setMyGroupID(String myGroupID) {
        MyGroupID = myGroupID;
        return this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saved ){
        View view = inflater.inflate(R.layout.fragment_balance, container , false );
        findViews(view);
        initViews();

        if(MyGroupID == null) {
            if(callback!=null) {
                callback.addErrorLabel();
            }
        }
        else {
            UpdateTransactionsByGroup();
        }

        return view;
    }

    private void UpdateTransactionsByGroup() {

        MyFireBaseRTDB.getAllExpenses(MyGroupID,new MyFireBaseRTDB.callbackAllExpenses() {
            @Override
            public void dataReady(ArrayList<MyExpense> value) {

                MyFireBaseRTDB.getNumOfMemberByGroupId(MyGroupID, new MyFireBaseRTDB.Callback_Get_num_of_members() {
                    @Override
                    public void dataReady(ArrayList<String> value2) {
                        balance = new MyBalance()
                                .setMyGroupID(MyGroupID)
                                .setNumOfMember(value2.size())
                                .setUsers(value2)
                                .setMyUser(MyFireBaseRTDB.getMyPhoneNumber())
                                .setAllExpenses(value);
                        updateBalance();
                    }
                });
            }
        });

    }


    private void updateBalance() {
        balance.getTransactionsList(new MyBalance.Callback_get_get_Transactions_List() {
            @SuppressLint("SetTextI18n")
            @Override
            public void dataReady(ArrayList<MyTransactions> value) {
                Adapter_Balance adapter_balance = new Adapter_Balance(activity , value );
                main_LST_balance.setLayoutManager(new GridLayoutManager(activity , 1));
                main_LST_balance.setHasFixedSize(true);
                main_LST_balance.setItemAnimator(new DefaultItemAnimator());
                main_LST_balance.setAdapter(adapter_balance);
                adapter_balance.setBalanceItemClickListener(new Adapter_Balance.BalanceItemClickListener() {
                    @Override
                    public void SettleBtnClicked(MyTransactions transaction, int position) {

                        MaterialAlertDialogBuilder alert = new  MaterialAlertDialogBuilder(activity);
                        alert.setTitle("Settling ");
                        alert.setMessage("confirm sending money ");
                        alert.setPositiveButton("SEND $ ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                MyFireBaseRTDB.saveTransaction(transaction, new MyFireBaseRTDB.Callback_Save_Transaction() {
                                    @Override
                                    public void onSuccesses() {
                                        if(callback!=null)
                                            callback.settled();
                                    }
                                });
                            }
                        });
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alert.show();

                    }

                });
                int tmp = balance.getTotalBalance() ;
                if(tmp < 0 ){
                    total_LBL_balance.setTextColor(Color.parseColor("#E53935"));
                    total_LBL_balance.setText(balance.getTotalBalance() +" $ ");
                }
                else if(tmp > 0 ){
                    total_LBL_balance.setTextColor(Color.parseColor("#FF669900"));
                    total_LBL_balance.setText(" + "+balance.getTotalBalance() +" $ ");
                }
                else {
                    total_LBL_balance.setTextColor(Color.parseColor("#FF669900"));
                    total_LBL_balance.setText("YOU SETTLED UP!");
                }

            }
        });


    }


    private void initViews() {



    }
    private void findViews(View view) {
        main_LST_balance = view.findViewById(R.id.main_LST_balance);
        total_LBL_balance = view.findViewById(R.id.total_LBL_balance);
    }
}




