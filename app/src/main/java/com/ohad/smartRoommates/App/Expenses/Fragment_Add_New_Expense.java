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
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;
import com.ohad.smartRoommates.Utils.Validator;

public class Fragment_Add_New_Expense extends Fragment {


    private AppCompatActivity activity ;
    private TextView new_Expense_LBl ;
    private MaterialButton submit_Expense_Btn;
    private TextInputLayout description_Et;
    private TextInputLayout cost_Et;
    private Callback_Add_New_Expense callback;
    private String groupID ;


    public Fragment_Add_New_Expense setGroupID(String groupID) {
        this.groupID = groupID;
        return this;
    }

    public interface Callback_Add_New_Expense {
        void finishAddProcess();
    }

    public Fragment_Add_New_Expense setActivity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }

    public Fragment_Add_New_Expense setCallback(Callback_Add_New_Expense callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saved ){
        View view = inflater.inflate(R.layout.fragment_add_new_expense, container , false );
        findViews(view);
        initViews();
        return view;
    }

    private void initViews() {
        submit_Expense_Btn.setOnClickListener(new View.OnClickListener() {
            Validator v1 = Validator.Builder
                    .make(cost_Et)
                    .addWatcher(new Validator.Watcher_Integer("only whole numbers"))
                    .addWatcher(new Validator.Watcher_Positive("only positive"))
                    .build();
            @Override
            public void onClick(View v) {

                if (v1.validateIt()) {
                    if (callback != null) {
                        MyExpense tmp = new MyExpense()
                                .setCost(Integer.parseInt(cost_Et.getEditText().getText().toString()))
                                .setDescription(description_Et.getEditText().getText().toString())
                                .setGroup(groupID);
                        MyFireBaseRTDB.saveNewExpense(tmp);

                        callback.finishAddProcess();
                    }
                } else {
                    Toast.makeText(activity, "not valid cost", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void findViews(View view) {
        submit_Expense_Btn = view.findViewById(R.id.submit_Expense_Btn);
        new_Expense_LBl = view.findViewById(R.id.new_Expense_LBl);
        description_Et = view.findViewById(R.id.description_Et);
        cost_Et = view.findViewById(R.id.cost_Et);
    }
}
