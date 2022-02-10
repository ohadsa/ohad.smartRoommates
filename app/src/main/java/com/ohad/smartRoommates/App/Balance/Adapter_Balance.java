package com.ohad.smartRoommates.App.Balance;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ohad.smartRoommates.App.Transaction.MyTransactions;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;

import java.util.ArrayList;


public class Adapter_Balance extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private ArrayList<MyTransactions> transactions = new ArrayList<>();
    private BalanceItemClickListener BalanceItemClickListener;

    public Adapter_Balance(Activity activity, ArrayList<MyTransactions> transactions) {
        this.activity = activity;
        this.transactions = transactions;
    }

    public Adapter_Balance setBalanceItemClickListener(BalanceItemClickListener balanceItemClickListener) {
        this.BalanceItemClickListener = balanceItemClickListener;
        return this;
    }

    @Override
    public RecyclerView.ViewHolder
    onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_suttles, viewGroup, false);
        return new BalanceViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        BalanceViewHolder balanceViewHolder = (BalanceViewHolder) holder;
        MyTransactions transactions = getItem(position);
        if(transactions.getReceiverID().equals(MyFireBaseRTDB.getMyPhoneNumber())) {
            balanceViewHolder.receiver_LBL_members.setText("You");
        }
        else{
            balanceViewHolder.receiver_LBL_members.setText(transactions.getReceiverName());
        }
        if(transactions.getSenderID().equals(MyFireBaseRTDB.getMyPhoneNumber())) {
            balanceViewHolder.Sender_LBL_title.setText("You");
        }
        else{
            balanceViewHolder.Sender_LBL_title.setText(transactions.getSenderName());
        }

        balanceViewHolder.Cost_LBL_members.setText(transactions.getMoney() + " $");

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    private MyTransactions getItem(int position) {
        return transactions.get(position);
    }

    public interface BalanceItemClickListener {
        void SettleBtnClicked(MyTransactions transactions, int position);
    }

    public class BalanceViewHolder extends RecyclerView.ViewHolder {


        public TextView Sender_LBL_title ;
        public TextView receiver_LBL_members;
        public TextView Cost_LBL_members ;
        public LinearLayout settle_layout;
        public MaterialButton settleBtn ;




        public BalanceViewHolder(final View itemView) {
            super(itemView);
            this.Sender_LBL_title = itemView.findViewById(R.id.title_LBL_balance);
            this.receiver_LBL_members = itemView.findViewById(R.id.reciever_LBL_members);
            this.Cost_LBL_members = itemView.findViewById(R.id.total_LBL_balance);
            this.settle_layout = itemView.findViewById(R.id.settle_layout);
            this.settleBtn = itemView.findViewById(R.id.settleBtn);


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    settle_layout.setVisibility(View.VISIBLE);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settle_layout.setVisibility(View.GONE);
                }
            });

            settleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BalanceItemClickListener.SettleBtnClicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });

        }
    }
}

