package com.ohad.smartRoommates.App.Expenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ohad.smartRoommates.App.Groups.Fragment_edit_group;
import com.ohad.smartRoommates.App.User.MyUser;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;
import com.ohad.smartRoommates.Utils.Validator;

import org.w3c.dom.Text;

public class Fragment_Edit_Expense extends Fragment {

    private AppCompatActivity activity ;
    private String MyGroupID ;
    private TextInputLayout description_Et_edit ;
    private TextInputLayout cost_Et_edit;
    private MaterialButton submit_Expense_Btn ;
    private CallBack_Edit_expense callback;
    private String ExpenseId ;
    private MyExpense oldExpense;
    private TextView Edit_payer;


    public interface CallBack_Edit_expense{
        void save();
    }


    public Fragment_Edit_Expense setCallback(CallBack_Edit_expense callback) {
        this.callback = callback;
        return this;
    }


    public Fragment_Edit_Expense setActivity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }

    public Fragment_Edit_Expense setExpenseId(String ExpenseId) {
        this.ExpenseId = ExpenseId;
        return this;
    }


    public Fragment_Edit_Expense setMyGroupID(String myGroupID) {
        MyGroupID = myGroupID;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saved ){
        View view = inflater.inflate(R.layout.fragment_edit_expense, container , false );
        findViews(view);
        initViews();
        return view;
    }

    private void initViews() {
        MyFireBaseRTDB.getExpenseById(ExpenseId, MyGroupID, new MyFireBaseRTDB.Callback_get_Expense_By_Id() {
            @Override
            public void dataReady(MyExpense value) {
                oldExpense = value;
                description_Et_edit.getEditText().setText(value.getDescription());

                cost_Et_edit.getEditText().setText( "" + (int)value.getCost() ) ;
                MyFireBaseRTDB.getUserByPhoneNumber(value.getPayer(), new MyFireBaseRTDB.CallBack_user() {
                    @Override
                    public void dataReady(MyUser value) {
                        Edit_payer.setText("Payed by : " + value.getFirstName() +" " + value.getLastName());
                    }
                });

            }
        });


        submit_Expense_Btn.setOnClickListener(new View.OnClickListener() {
            Validator v1 = Validator.Builder
                    .make(cost_Et_edit)
                    .addWatcher(new Validator.Watcher_Integer("only whole numbers"))
                    .addWatcher(new Validator.Watcher_Positive("only positive"))
                    .build();


            @Override
            public void onClick(View v) {


                if(v1.validateIt()) {
                    if (callback != null) {
                        MyFireBaseRTDB.EditExpense(oldExpense, description_Et_edit.getEditText().getText().toString(), Double.parseDouble(cost_Et_edit.getEditText().getText().toString()));
                        callback.save();
                    }
                }
                else{
                    Toast.makeText(activity, "not valid cost", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void findViews(View view) {
        description_Et_edit = view.findViewById(R.id.description_Et_edit);
        cost_Et_edit = view.findViewById(R.id.cost_Et_edit);
        submit_Expense_Btn = view.findViewById(R.id.submit_Expense_Btn);
        Edit_payer = view.findViewById(R.id.Edit_payer);
    }

}
