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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;

import java.util.ArrayList;

public class Fragment_Group_List extends Fragment {

    public interface Callback_Groups{
        void addNewGroupBtn();
        void setChosenGroup(MyGroup group, int position);
        void deleteGroup();
        void EditGroup(MyGroup group);
    }


    private AppCompatActivity activity ;
    private Callback_Groups callback ;
    private RecyclerView main_LST_Groups;
    private MaterialCardView newGroup_MCV ;


    public Fragment_Group_List(){}

    public Fragment_Group_List setCallback(Callback_Groups callback) {
        this.callback = callback;
        return this;
    }

    public Fragment_Group_List setActivity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container,  Bundle saved ){
        View view = inflater.inflate(R.layout.fragment_group_list, container ,   false );
        findViews(view);
        initViews();
        UpdateGroupsByUserPhoneNumber();


        return view;
    }

    private void UpdateGroupsByUserPhoneNumber() {

        MyFireBaseRTDB.getAllGroupsOfUser(MyFireBaseRTDB.getMyPhoneNumber(), new MyFireBaseRTDB.CallBack_groupList() {

            @Override
            public void dataReady(ArrayList<MyGroup> value) {

                Adapter_Groups adapter_groups = new Adapter_Groups(activity, value);
                main_LST_Groups.setLayoutManager(new GridLayoutManager(activity, 1));
                main_LST_Groups.setHasFixedSize(true);
                main_LST_Groups.setItemAnimator(new DefaultItemAnimator());
                main_LST_Groups.setAdapter(adapter_groups);
                adapter_groups.setGroupItemClickListener(new Adapter_Groups.GroupItemClickListener() {
                    @Override
                    public void ItemClicked(MyGroup group, int position) {
                        if(callback != null)
                            callback.setChosenGroup(group,position);
                        Toast.makeText(activity, group.getGroupName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void ItemDeleteBtnClicked(MyGroup group, int Position) {

                        MaterialAlertDialogBuilder alert = new  MaterialAlertDialogBuilder(activity);
                        alert.setTitle("delete ? ");
                        alert.setMessage("this action can not be un done !");
                        alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("TAG_123", "onSwipeLeft: here we deleting !!" );
                                MyFireBaseRTDB.deleteGroup(group);
                                if(callback!=null)
                                    callback.deleteGroup();
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
                    public void ItemEditBtnClicked(MyGroup group, int Position) {
                        if(callback != null) {
                            callback.EditGroup(group);
                            Log.d("TAG_123", "ItemEditBtnClicked: " +group.getGroupName() );
                        }
                    }

                });

            }
        });

    }


    private void initViews() {
        newGroup_MCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null){
                    callback.addNewGroupBtn();
                }
            }
        });

    }

    private void findViews(View view) {

        main_LST_Groups = view.findViewById(R.id.main_LST_Groups);
        newGroup_MCV = view.findViewById(R.id.newGroup_MCV);
    }

}
