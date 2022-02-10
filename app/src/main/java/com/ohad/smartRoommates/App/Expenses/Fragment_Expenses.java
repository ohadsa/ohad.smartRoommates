package com.ohad.smartRoommates.App.Expenses;

import android.content.DialogInterface;
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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;

import java.util.ArrayList;

public class Fragment_Expenses extends Fragment {


    public interface Callback_Expenses{
        void addNewExpense();
        void addErrorLabel();
        void delete_Expense();
        void editExpenseFrag(String Expenseid, String groupId);
    }
    private String myGroupId;
    private AppCompatActivity activity ;
    private Callback_Expenses callback ;
    private RecyclerView main_LST_Expenses;
    private MaterialCardView newExpense_MCV ;



    public Fragment_Expenses setActivity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }

    public Fragment_Expenses setMyGroupId(String myGroupId) {
        this.myGroupId = myGroupId;
        return this;
    }

    public Fragment_Expenses setCallback(Callback_Expenses callback) {
        this.callback = callback;
        return this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saved ){
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        findViews(view);
        initViews();
        if(myGroupId == null) {
            if(callback!=null) {
                callback.addErrorLabel();
            }
        }
        else {
            UpdateExpensesByGroup();
        }

        return view;
    }

    private void UpdateExpensesByGroup() {
        MyFireBaseRTDB.getAllExpenses(myGroupId, new MyFireBaseRTDB.callbackAllExpenses() {
            @Override
            public void dataReady(ArrayList<MyExpense> value) {
                Adapter_Expenses adapter_expenses = new Adapter_Expenses(activity , value);
                main_LST_Expenses.setLayoutManager(new GridLayoutManager(activity , 1));
                main_LST_Expenses.setHasFixedSize(true);
                main_LST_Expenses.setItemAnimator(new DefaultItemAnimator());
                main_LST_Expenses.setAdapter(adapter_expenses);
                adapter_expenses.setExpensesItemClickListener(new Adapter_Expenses.ExpensesItemClickListener() {
                    @Override
                    public void DeleteBtnClicked(MyExpense expense, int position) {
                        MaterialAlertDialogBuilder alert = new  MaterialAlertDialogBuilder(activity);
                        alert.setTitle("delete ? ");
                        alert.setMessage("this action can not be un done !");
                        alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyFireBaseRTDB.deleteExpense(expense);
                                if(callback!=null)
                                    callback.delete_Expense();
                            }


                        });
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alert.show();

                    }

                    @Override
                    public void EditBtnClicked(MyExpense expense, int position) {
                        if(callback!=null)
                            callback.editExpenseFrag(expense.getExpenseId() , myGroupId);
                    }
                });
            }
        });

    }




    private void initViews() {
        newExpense_MCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myGroupId != null) {
                    if (callback != null) {
                        callback.addNewExpense();
                    }
                }
                else{
                    callback.addErrorLabel();
                }
            }
        });
    }

    private void findViews(View view) {
        main_LST_Expenses = view.findViewById(R.id.main_LST_Expenses);
        newExpense_MCV = view.findViewById(R.id.newExpense_MCV);
    }
}
