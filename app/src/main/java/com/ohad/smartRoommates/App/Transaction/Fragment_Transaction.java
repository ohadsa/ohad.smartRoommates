package com.ohad.smartRoommates.App.Transaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ohad.smartRoommates.App.Expenses.MyExpense;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;

import java.util.ArrayList;

public class Fragment_Transaction extends Fragment {
    private Callback_Transaction callback ;
    private AppCompatActivity activity ;
    private String MyGroupID ;
    private RecyclerView main_LST_Transactions;

    public interface Callback_Transaction{
        void deletedTransaction();
        void addErrorLabel();
    }


    public Fragment_Transaction setCallback(Callback_Transaction callback) {
        this.callback = callback;
        return this;
    }


    public Fragment_Transaction setActivity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }


    public Fragment_Transaction setMyGroupID(String myGroupID) {
        MyGroupID = myGroupID;
        return this;
    }



    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saved ){
        View view = inflater.inflate(R.layout.fragment_transactions, container , false );
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
        MyFireBaseRTDB.getAllMoneyTransactions(MyGroupID, new MyFireBaseRTDB.Callback_Get_All_Transactions() {
            @Override
            public void dataReady(ArrayList<MyTransactions> value) {
                Log.d("DELETE", "update in fregment new adapter__________________");
                for(MyTransactions t : value)
                    Log.d("DELETE", t.toString());
                Log.d("DELETE", "____________________");
                AdapterTransactions adapterTransactions = new AdapterTransactions(activity , value);
                main_LST_Transactions.setLayoutManager(new GridLayoutManager(activity , 1 ));
                main_LST_Transactions.setHasFixedSize(true);
                main_LST_Transactions.setItemAnimator(new DefaultItemAnimator());
                main_LST_Transactions.setAdapter(adapterTransactions);
                adapterTransactions.setTransactionsItemClickListener(new AdapterTransactions.TransactionsItemClickListener() {

                    @Override
                    public void DeleteBtnClicked(MyTransactions transaction, int position) {
                        MyFireBaseRTDB.deleteTransaction(transaction, new MyFireBaseRTDB.Callback_Delete_transaction() {
                            @Override
                            public void onSuccesses() {
                                if(callback!=null)
                                    callback.deletedTransaction();
                            }
                        });
                    }
                });
            }
        });
    }

    private void initViews() {
    }

    private void findViews(View view) {
        main_LST_Transactions = view.findViewById(R.id.main_LST_Transactions);
    }
}
