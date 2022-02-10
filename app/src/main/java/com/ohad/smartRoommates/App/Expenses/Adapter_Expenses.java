package com.ohad.smartRoommates.App.Expenses;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.io.LineReader;
import com.ohad.smartRoommates.App.AppActivity;
import com.ohad.smartRoommates.App.Groups.MyGroup;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;

import java.util.ArrayList;

public class Adapter_Expenses extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private ArrayList<MyExpense> expenses = new ArrayList<>();
    private ExpensesItemClickListener expensesItemClickListener;

    public Adapter_Expenses(Activity activity, ArrayList<MyExpense> groups) {
        this.activity = activity;
        this.expenses = groups;
    }

    public Adapter_Expenses setExpensesItemClickListener(ExpensesItemClickListener expensesItemClickListener) {
        this.expensesItemClickListener = expensesItemClickListener;
        return this;
    }

    @Override
    public RecyclerView.ViewHolder
    onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_expenses, viewGroup, false);
        return new ExpenseViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Adapter_Expenses.ExpenseViewHolder expensesViewHolder = (Adapter_Expenses.ExpenseViewHolder) holder;
        MyExpense expense = getItem(position);

        expensesViewHolder.Expense_LBL_Description.setText(expense.getDescription());
        if(expense.getPayer().equals(MyFireBaseRTDB.getMyPhoneNumber())){
            expensesViewHolder.Expense_LBL_Payer.setText("paid by you" );
        }
        else {
            MyFireBaseRTDB.getUserNameById(expense.getPayer(), new MyFireBaseRTDB.Callback_user_name() {
                @Override
                public void dataReady(String value) {

                    expensesViewHolder.Expense_LBL_Payer.setText("paid by " + value);
                }
            });
        }
        expensesViewHolder.Expense_LBL_Cost.setText(expense.getCost()+"");
        MyFireBaseRTDB.getAllGroupsOfUser(expense.getPayer(), new MyFireBaseRTDB.CallBack_groupList() {
            @Override

            public void dataReady(ArrayList<MyGroup> value) {
                Boolean val = true ;
                for(MyGroup g : value) {
                    if (g.getGroupId().equals(expense.getGroup())) {
                        val = false;
                    }
                }

                Boolean finalVal = val;
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if( finalVal) {
                                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity);
                                alert.setTitle("Error");
                                alert.setMessage("cant edit \nmember no longer in the group!");
                                alert.show();
                            }
                            else {
                                expensesViewHolder.options_layout.setVisibility(View.VISIBLE);
                            }
                            return true;
                        }
                    });
            }
        });

    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    private MyExpense getItem(int position) {
        return expenses.get(position);
    }

    public interface ExpensesItemClickListener {
        void DeleteBtnClicked(MyExpense expense, int position);
        void EditBtnClicked(MyExpense expense, int position);
    }


    public class ExpenseViewHolder extends RecyclerView.ViewHolder {

        public TextView Expense_LBL_Description ;
        public TextView Expense_LBL_Cost ;
        public TextView Expense_LBL_Payer ;
        public LinearLayout options_layout;
        public MaterialButton deleteExpenseBtn ;
        public MaterialButton editExpenseBtn ;



        public ExpenseViewHolder(final View itemView) {
            super(itemView);
            this.Expense_LBL_Description = itemView.findViewById(R.id.Expense_LBL_Description);
            this.Expense_LBL_Cost = itemView.findViewById(R.id.Expense_LBL_Cost);
            this.Expense_LBL_Payer = itemView.findViewById(R.id.Expense_LBL_Payer);
            this.options_layout = itemView.findViewById(R.id.options_layout);
            this.editExpenseBtn = itemView.findViewById(R.id.editExpenseBtn);
            this.deleteExpenseBtn = itemView.findViewById(R.id.deleteExpenseBtn);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    options_layout.setVisibility(View.GONE);
                }

            });

            editExpenseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expensesItemClickListener.EditBtnClicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });

            deleteExpenseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expensesItemClickListener.DeleteBtnClicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });




        }
    }




}



