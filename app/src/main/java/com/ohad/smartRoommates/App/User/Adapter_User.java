package com.ohad.smartRoommates.App.User;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ohad.smartRoommates.R;

import java.util.ArrayList;

public class Adapter_User extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private ArrayList<MyUser> users = new ArrayList<>();
    private UserItemClickListener userItemClickListener;
    private Boolean canBeEdited ;


    public interface UserItemClickListener {
        void deleteBtnClicked(MyUser user, int position);
        void userClicked();
    }

    public Boolean getCanBeEdited() {
        return canBeEdited;
    }

    public Adapter_User setCanBeEdited(Boolean canBeEdited) {
        this.canBeEdited = canBeEdited;
        return this;
    }

    public Adapter_User(Activity activity, ArrayList<MyUser> groups) {
        this.activity = activity;
        this.users = groups;
    }

    public Adapter_User setUserItemClickListener(UserItemClickListener userItemClickListener) {
        this.userItemClickListener = userItemClickListener;
        return this;
    }


    @Override
    public RecyclerView.ViewHolder
    onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_user, viewGroup, false);
        return new UserViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        UserViewHolder expensesViewHolder = (UserViewHolder) holder;
        MyUser user = getItem(position);
        expensesViewHolder.memberDetailName.setText(user.getFirstName() +" "+ user.getLastName());
        expensesViewHolder.memberDetailNumber.setText(user.getPhNumber());
        try {
            byte[] incode = Base64.decode(user.getUserImg(), Base64.DEFAULT);
            expensesViewHolder.memberDetailImage.setImageBitmap(BitmapFactory.decodeByteArray(incode, 0, incode.length));
        }
        catch (Exception e){
            expensesViewHolder.memberDetailImage.setImageResource(R.drawable.no_picture);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private MyUser getItem(int position) {
        return users.get(position);
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView memberDetailName ;
        public TextView memberDetailNumber ;
        public RoundedImageView memberDetailImage ;
        public MaterialButton deleteUserBtn ;
        public Boolean clicked = false ;

        public UserViewHolder(final View itemView) {
            super(itemView);

            this.memberDetailName = itemView.findViewById(R.id.memberDetailName);
            this.memberDetailNumber = itemView.findViewById(R.id.memberDetailNumber);
            this.memberDetailImage = itemView.findViewById(R.id.memberDetailImage);
            this.deleteUserBtn = itemView.findViewById(R.id.deleteUserBtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canBeEdited) {
                        if (clicked) {
                            deleteUserBtn.setVisibility(View.GONE);
                            clicked = false;
                        } else {
                            deleteUserBtn.setVisibility(View.VISIBLE);
                            clicked = true;
                        }

                        if (userItemClickListener != null)
                            userItemClickListener.userClicked();
                    }
                }
            });


            deleteUserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userItemClickListener.deleteBtnClicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });


        }
    }




}



