package com.ohad.smartRoommates.App.Groups;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ohad.smartRoommates.App.User.MyUser;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;
import com.ohad.smartRoommates.Utils.Utilities;
import com.ohad.smartRoommates.Utils.Validator;

import java.util.ArrayList;

public class Fragment_Add_New_Group extends Fragment {

    private String groupName;
    private ArrayList<MyUser> users ;
    private AppCompatActivity activity ;
    private Callback_Add_New_Group callback ;
    private TextView new_group_LBl ;
    private MaterialButton phoneContinueBtn;
    private MaterialButton phoneAddMemberBtn;
    private MaterialButton phoneFinishBtn;
    private TextInputLayout Group_Name_Et;
    private TextInputLayout Add_Member_Et;


    public interface Callback_Add_New_Group {
        void finishAddProcess();
    }


    public Fragment_Add_New_Group setActivity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }


    public Fragment_Add_New_Group setCallback(Callback_Add_New_Group callback) {
        this.callback = callback;
        return this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saved ){
        View view = inflater.inflate(R.layout.fragment_add_new_group, container , false );
        users =new ArrayList<>();
        findViews(view);
        initViews();
        return view;
    }

    private void initViews() {
        phoneContinueBtn.setOnClickListener(new View.OnClickListener() {
            Validator v1 = Validator.Builder
                    .make(Group_Name_Et)
                    .addWatcher(new Validator.Watcher_Group_Name("only English letters"))
                    .build();
            @Override
            public void onClick(View v) {


                if(v1.validateIt()){
                    groupName = Group_Name_Et.getEditText().getText().toString();
                    new_group_LBl.setText("lets add members");
                    phoneContinueBtn.setVisibility(View.GONE);
                    Group_Name_Et.setVisibility(View.GONE);
                    Add_Member_Et.setVisibility(View.VISIBLE);
                    phoneAddMemberBtn.setVisibility(View.VISIBLE);
                    phoneFinishBtn.setVisibility(View.VISIBLE);
                    MyFireBaseRTDB.getUserByPhoneNumber(MyFireBaseRTDB.getMyPhoneNumber(), new MyFireBaseRTDB.CallBack_user() {
                        @Override
                        public void dataReady(MyUser value) {
                            users.add(value);
                        }
                    });
                }
                else {
                    Toast.makeText(activity, "only English letters", Toast.LENGTH_SHORT).show();
                }
            }
        });

        phoneAddMemberBtn.setOnClickListener(new View.OnClickListener() {
            Validator v1 = Validator.Builder
                    .make(Add_Member_Et)
                    .addWatcher(new Validator.Watcher_Integer("not valid phone"))
                    .addWatcher(new Validator.Watcher_Positive("not valid phone"))
                    .addWatcher(new Validator.Watcher_Length("not valid phone"))
                    .build();

            @Override
            public void onClick(View v) {

                if(v1.validateIt()) {
                    String phone = Add_Member_Et.getEditText().getText().toString();
                    phone = Utilities.reWriteNumber(phone);
                    addUserToList(phone);
                }
                else{
                    Toast.makeText(activity, "enter valid phone", Toast.LENGTH_SHORT).show();
                }
            }
        });



        phoneFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyFireBaseRTDB.saveGroup(new MyGroup()
                        .setGroupName(groupName)
                        .addUsersList(users));
                finishAddProcess();
            }

        });



    }

    private void finishAddProcess() {
       if(callback != null){
           callback.finishAddProcess();
       }
    }

    private void addUserToList(String phone) {
        MyFireBaseRTDB.getUserByPhoneNumber(phone, new MyFireBaseRTDB.CallBack_user() {
            @Override
            public void dataReady(MyUser value) {
                if(value == null ||phone.equals("")){
                    Toast.makeText(activity, "number not in our system", Toast.LENGTH_SHORT).show();
                }
                else if(phoneAlreadyAdded(phone)){
                    Toast.makeText(activity, "number already added ", Toast.LENGTH_SHORT).show();
                }
                else{
                    users.add(value);
                    Toast.makeText(activity, "added ! ", Toast.LENGTH_SHORT).show();
                    for(MyUser u : users)
                        Log.d("TAG", "dataReady: " + u.toString() );
                }
            }
        });
    }

    private boolean phoneAlreadyAdded(String phone ) {
        for(MyUser u : users) {
            if (u.getPhNumber().equals(phone))
                return true;
        }
        return false;
    }

    private void findViews(View view) {
        new_group_LBl = view.findViewById(R.id.new_group_LBl);
        phoneContinueBtn = view.findViewById(R.id.phoneContinueBtn);
        Group_Name_Et = view.findViewById(R.id.Group_Name_Et);
        phoneContinueBtn = view.findViewById(R.id.phoneContinueBtn);
        phoneFinishBtn = view.findViewById(R.id.phoneFinishBtn);
        Add_Member_Et = view.findViewById(R.id.Add_Member_Et);
        phoneAddMemberBtn = view.findViewById(R.id.phoneAddMemberBtn);

    }

}
