package com.ohad.smartRoommates.App.Transaction;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ohad.smartRoommates.App.User.MyUser;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;

import java.util.ArrayList;

public class AdapterTransactions extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Activity activity;
    private ArrayList<MyTransactions> transactions = new ArrayList<>();
    private TransactionsItemClickListener transactionsItemClickListener;

    public AdapterTransactions(Activity activity, ArrayList<MyTransactions> transactions) {
        this.activity = activity;
        this.transactions = transactions;
    }

        public AdapterTransactions setTransactionsItemClickListener(TransactionsItemClickListener transactionsItemClickListener) {
            this.transactionsItemClickListener = transactionsItemClickListener;
            return this;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_transactions, viewGroup, false);

            return new TransactionViewHolder(view);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            TransactionViewHolder transactionViewHolder = (TransactionViewHolder) holder;
            MyTransactions transaction = getItem(position);

            if(transaction.getReceiverID().equals(MyFireBaseRTDB.getMyPhoneNumber())) {
                transactionViewHolder.receiver_LBL_transaction.setText("You");
            }
            else{
                transactionViewHolder.receiver_LBL_transaction.setText(transaction.getReceiverName());
            }
            if(transaction.getSenderID().equals(MyFireBaseRTDB.getMyPhoneNumber())) {
                transactionViewHolder.sender_LBL_transaction.setText("You");
            }
            else{
                transactionViewHolder.sender_LBL_transaction.setText(transaction.getSenderName());
            }
            transactionViewHolder.transaction_LBL_Cost.setText(""+transaction.getMoney());

            MyFireBaseRTDB.get_all_users_of_group(transaction.getGroupID(), new MyFireBaseRTDB.Callback_get_all_users_of_group() {
                @Override
                public void dataReady(ArrayList<MyUser> value) {
                    Boolean val1 = false ;
                    Boolean val2 = false ;
                    for(MyUser u : value){
                        if(u.getPhNumber().equals(transaction.getSenderID())){
                            val1 = true ;
                        }
                        if(u.getPhNumber().equals(transaction.getReceiverID())){
                            val2 = true ;
                        }
                    }

                    Boolean finalVal = val1 && val2;
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if(finalVal) {
                                transactionViewHolder.delete_layout_transaction.setVisibility(View.VISIBLE);
                            }
                            else{
                                Log.d("maze", "onLongClick: ________________");
                                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity);
                                alert.setTitle("Error");
                                alert.setMessage("cant edit \nmember no longer in the group!");
                                alert.show();
                            }
                            return true;
                        }
                    });

                }
            });





        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        private MyTransactions getItem(int position) {
            return transactions.get(position);
        }

    public interface TransactionsItemClickListener {
        void DeleteBtnClicked(MyTransactions transaction, int position);
    }


        public class TransactionViewHolder extends RecyclerView.ViewHolder {

            public TextView sender_LBL_transaction;
            public TextView receiver_LBL_transaction;
            public TextView transaction_LBL_Cost;
            public MaterialButton  delete_Transaction_btn ;
            public LinearLayout delete_layout_transaction;



            public TransactionViewHolder(final View itemView) {
                super(itemView);
                this.sender_LBL_transaction = itemView.findViewById(R.id.sender_LBL_transaction);
                this.receiver_LBL_transaction = itemView.findViewById(R.id.reciever_LBL_transaction);
                this.transaction_LBL_Cost = itemView.findViewById(R.id.transaction_LBL_Cost);
                this.delete_Transaction_btn = itemView.findViewById(R.id.delete_Transaction_btn);
                this.delete_layout_transaction = itemView.findViewById(R.id.delete_layout_transaction);




                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete_layout_transaction.setVisibility(View.GONE);
                    }

                });

                delete_Transaction_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transactionsItemClickListener.DeleteBtnClicked(getItem(getAdapterPosition()), getAdapterPosition());
                    }
                });

            }
        }
}
