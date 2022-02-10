package com.ohad.smartRoommates.App.Groups;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.ohad.smartRoommates.App.User.Adapter_User;
import com.ohad.smartRoommates.App.User.MyUser;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;
import com.ohad.smartRoommates.Utils.Utilities;
import com.ohad.smartRoommates.Utils.Validator;

import java.util.ArrayList;

public class Fragment_edit_group extends Fragment {

    private Callback_edit_group callback ;
    private AppCompatActivity activity ;
    private String MyGroupID ;
    private TextInputLayout groupName_LO ;
    private TextInputLayout new_phone_number ;
    private RecyclerView main_LST_Users_edit ;
    private MaterialButton addMemberBtn ;
    private MaterialButton saveBtn ;

    public interface Callback_edit_group{
        void delete_user_from_group();
        void add_user_to_group();
        void save();
    }

    public Fragment_edit_group setCallback(Callback_edit_group callback) {
        this.callback = callback;
        return this;
    }


    public Fragment_edit_group setActivity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }


    public Fragment_edit_group setMyGroupID(String myGroupID) {
        MyGroupID = myGroupID;
        return this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saved ){
        View view = inflater.inflate(R.layout.fragment_edit_group, container , false );
        findViews(view);
        initViews();
        return view;
    }


    private void initViews() {


        MyFireBaseRTDB.getGroupNameById(MyGroupID, new MyFireBaseRTDB.Callback_Get_Group_name() {
            @Override
            public void dataReady(String value) {
                groupName_LO.getEditText().setText(value);
            }
        });

        MyFireBaseRTDB.get_all_users_of_group(MyGroupID, new MyFireBaseRTDB.Callback_get_all_users_of_group() {

            @Override
            public void dataReady(ArrayList<MyUser> value) {
                Adapter_User adapter_user = new Adapter_User(activity, value).setCanBeEdited(true);
                main_LST_Users_edit.setLayoutManager(new GridLayoutManager(activity, 1));
                main_LST_Users_edit.setHasFixedSize(true);
                main_LST_Users_edit.setItemAnimator(new DefaultItemAnimator());
                main_LST_Users_edit.setAdapter(adapter_user);
                adapter_user.setUserItemClickListener(new Adapter_User.UserItemClickListener() {
                    @Override
                    public void deleteBtnClicked(MyUser user, int position) {

                        MyFireBaseRTDB.isGroupBalanced(MyGroupID, new MyFireBaseRTDB.CallBack_balanced() {
                            @Override
                            public void dataReady(Boolean value) {
                                if(value){
                                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity);
                                    alert.setTitle("delete ? ");
                                    alert.setMessage("this action can not be un done !");
                                    alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MyFireBaseRTDB.deleteUserFromGroup(MyGroupID, user.getPhNumber());

                                            if (callback != null)
                                                callback.delete_user_from_group();
                                        }
                                    });
                                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    alert.show();

                                }
                                else
                                {
                                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity);
                                    alert.setTitle("Error");
                                    alert.setMessage("need to balance the group before delete!");
                                    alert.show();
                                }
                            }
                        });


                    }

                    @Override
                    public void userClicked() {

                    }
                });
            }
        });


        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            Validator v1 = Validator.Builder
                    .make(new_phone_number)
                    .addWatcher(new Validator.Watcher_Integer("not valid phone"))
                    .addWatcher(new Validator.Watcher_Positive("not valid phone"))
                    .addWatcher(new Validator.Watcher_Length("not valid phone"))
                    .build();
            @Override
            public void onClick(View v) {

                if (v1.validateIt()) {
                    String phone = Utilities.reWriteNumber(new_phone_number.getEditText().getText().toString());
                    MyFireBaseRTDB.isGroupBalanced(MyGroupID, new MyFireBaseRTDB.CallBack_balanced() {
                        @Override
                        public void dataReady(Boolean value) {
                            if (value) {
                                MyFireBaseRTDB.addUserToGroup(MyGroupID, phone, new MyFireBaseRTDB.Callback_Add_User_to_Group() {
                                    @Override
                                    public void onSuccesses() {
                                        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity);
                                        alert.setTitle("massage");
                                        alert.setMessage("member added");
                                        alert.show();
                                        callback.add_user_to_group();

                                    }

                                    @Override
                                    public void onFail(int error) {
                                        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity);
                                        alert.setTitle("Error");
                                        if (error == 1)
                                            alert.setMessage("number not on the system");
                                        if (error == 2)
                                            alert.setMessage("number Already in the group");
                                        alert.show();

                                    }
                                });
                            } else {
                                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity);
                                alert.setTitle("Error");
                                alert.setMessage("need to balance the group before adding!");
                                alert.show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(activity, "enter valid phone", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {

            Validator v1 = Validator.Builder
                    .make(groupName_LO)
                    .addWatcher(new Validator.Watcher_Group_Name("only English letters"))
                    .build();


            @Override
            public void onClick(View v) {

                if(v1.validateIt()) {
                    MyFireBaseRTDB.changeGroupName(MyGroupID, groupName_LO.getEditText().getText().toString());
                    callback.save();
                }
                else{
                    Toast.makeText(activity, "not valid Group name", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void findViews(View view) {
        groupName_LO = view.findViewById(R.id.groupName_LO );
        main_LST_Users_edit = view.findViewById(R.id.main_LST_Users_edit );
        addMemberBtn = view.findViewById(R.id.addMemberBtn );
        new_phone_number = view.findViewById(R.id.new_phone_number);
        saveBtn = view.findViewById(R.id.saveBtn);
    }
}
