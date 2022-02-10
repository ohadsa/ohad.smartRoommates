package com.ohad.smartRoommates.App.Groups;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ohad.smartRoommates.App.User.Adapter_User;
import com.ohad.smartRoommates.R;

import java.util.ArrayList;


public class Adapter_Groups extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private ArrayList<MyGroup> groups = new ArrayList<>();
    private GroupItemClickListener groupItemClickListener;

    public Adapter_Groups(Activity activity, ArrayList<MyGroup> groups) {
        this.activity = activity;
        this.groups = groups;
    }

    public Adapter_Groups setGroupItemClickListener(GroupItemClickListener groupItemClickListener) {
        this.groupItemClickListener = groupItemClickListener;
        return this;
    }

    @Override
    public RecyclerView.ViewHolder
    onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_groups, viewGroup, false);
        return new GroupViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        GroupViewHolder movieViewHolder = (GroupViewHolder) holder;
        MyGroup group = getItem(position);
        Log.d("pttt", "position= " + position);
        movieViewHolder.Group_LBL_title.setText(group.getGroupName());
        Adapter_User adapter_user = new Adapter_User(activity, group.getAllUsers());
        adapter_user.setCanBeEdited(false);
        movieViewHolder.main_LST_Users.setLayoutManager(new GridLayoutManager(activity, 1));
        movieViewHolder.main_LST_Users.setHasFixedSize(true);
        movieViewHolder.main_LST_Users.setItemAnimator(new DefaultItemAnimator());
        movieViewHolder.main_LST_Users.setAdapter(adapter_user);


    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    private MyGroup getItem(int position) {
        return groups.get(position);
    }

    public interface GroupItemClickListener {
        void ItemClicked(MyGroup group, int position);
        void ItemDeleteBtnClicked(MyGroup group , int Position);
        void ItemEditBtnClicked(MyGroup group , int Position);
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        public TextView Group_LBL_title ;
        public TextView Group_LBL_members ;
        public RecyclerView main_LST_Users ;
        public LinearLayout options_layout ;
        public MaterialButton edit_btn ;
        public MaterialButton delete_btn ;




        public GroupViewHolder(final View itemView) {
            super(itemView);
            this.Group_LBL_title = itemView.findViewById(R.id.Group_LBL_title);
            this.Group_LBL_members = itemView.findViewById(R.id.Group_LBL_members);
            this.main_LST_Users = itemView.findViewById(R.id.main_LST_Users);
            this.options_layout = itemView.findViewById(R.id.options_layout);
            this.edit_btn = itemView.findViewById(R.id.edit_btn);
            this.delete_btn = itemView.findViewById(R.id.delete_btn);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupItemClickListener.ItemClicked(getItem(getAdapterPosition()), getAdapterPosition());
                    options_layout.setVisibility(View.GONE);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    options_layout.setVisibility(View.VISIBLE);
                    return true;
                }
            });

            edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupItemClickListener.ItemEditBtnClicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupItemClickListener.ItemDeleteBtnClicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });



        }
    }
}

