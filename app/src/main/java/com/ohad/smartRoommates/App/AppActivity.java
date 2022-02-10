package com.ohad.smartRoommates.App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.ohad.smartRoommates.App.Expenses.Fragment_Add_New_Expense;
import com.ohad.smartRoommates.App.Expenses.Fragment_Edit_Expense;
import com.ohad.smartRoommates.App.Groups.Fragment_Add_New_Group;
import com.ohad.smartRoommates.App.Balance.Fragment_Balance;
import com.ohad.smartRoommates.App.Expenses.Fragment_Expenses;
import com.ohad.smartRoommates.App.Groups.Fragment_Group_List;
import com.ohad.smartRoommates.App.Groups.Fragment_edit_group;
import com.ohad.smartRoommates.App.Transaction.Fragment_Transaction;
import com.ohad.smartRoommates.App.User.Fragment_Edit_User;
import com.ohad.smartRoommates.Authentication.LogInActivity;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;
import com.ohad.smartRoommates.App.Groups.MyGroup;
import com.ohad.smartRoommates.App.User.MyUser;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.Utilities;

import java.io.File;

public class AppActivity extends AppCompatActivity {

    private TextView txt ;
    private FrameLayout frame1 ;
    private Fragment_Group_List fragGroupList;
    private Fragment_Expenses fragExpenses ;
    private Fragment_Add_New_Expense fragAddNewExpenses ;
    private Fragment_Balance fragBalance;
    private Fragment_Edit_Expense fragEditExpense;
    private Fragment_Add_New_Group fragAddNewGroup;
    private Fragment_Transaction fragTransactions ;
    private String groupID;
    private BottomNavigationView bottom_navigation;
    private ImageView IMG_Profile_AppActivity;
    private Fragment_edit_group fragEditGroup;
    private Fragment_Edit_User fragEditUser ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        checkIfUserIsOk();
        findViews();
        initFragments();
        getSupportFragmentManager().beginTransaction().add(R.id.frame1 , fragGroupList).commit() ;
        initMenuClickListeners();
        initClickLIseners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfUserIsOk();
    }


    private void initClickLIseners() {
        IMG_Profile_AppActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AppActivity.this , "press long to edit" , Toast.LENGTH_SHORT).show();
            }
        });
        IMG_Profile_AppActivity.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v){
                getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragEditUser).commit();
                return true;
            }
        });
    }

    private void initMenuClickListeners() {

        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getTitle().toString().equals("groups") )
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragGroupList).commit();
                if(item.getTitle().toString().equals("expenses") )
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragExpenses).commit();
                if(item.getTitle().toString().equals("transactions"))
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragTransactions).commit();
                if(item.getTitle().toString().equals("balance") ) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame1, fragBalance).commit();
                }
                return true;
            }
        });

    }

    private void initFragmentBalance() {
        fragBalance =  new Fragment_Balance()
                .setActivity(this)
                .setMyGroupID(groupID)
                .setCallback(new Fragment_Balance.Callback_Balance() {

                    @Override
                    public void addErrorLabel() {
                        Toast.makeText(AppActivity.this , "PLEASE SELECT GROUP FIRST !" , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void settled() {
                        fragBalance = null;
                        initFragments();
                        fragBalance.setMyGroupID(groupID);
                        fragExpenses.setMyGroupId(groupID);
                        fragTransactions.setMyGroupID(groupID);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragBalance).commit();
                    }
                });
    }

    private void initFragments() {
        initFragmentGroup();
        initFragmentTransactions();
        initFragmantEditExpense();
        initFragmentBalance();
        initFragmentEditGroup();
        initFragmentExpense();
        initFragmantEditUser();


    }

    private void initFragmantEditUser() {
        fragEditUser = new Fragment_Edit_User()
                .setActivity(this)
                .setCallback(new Fragment_Edit_User.Callback_edit_User() {
                    @Override
                    public void save(String img) {
                        fragEditUser = null;

                        byte[] incode = Base64.decode(img, Base64.DEFAULT);
                        IMG_Profile_AppActivity.setImageBitmap(BitmapFactory.decodeByteArray(incode, 0, incode.length));
                        initFragments();
                        fragBalance.setMyGroupID(groupID);
                        fragExpenses.setMyGroupId(groupID);
                        fragTransactions.setMyGroupID(groupID);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragGroupList).commit();
                    }

                    @Override
                    public void logOut() {
                        Utilities.clearApplicationData(AppActivity.this);
                        startActivity(new Intent(AppActivity.this, LogInActivity.class));
                        finish();


                    }
                });
    }

    private void initFragmentExpense() {
        fragExpenses = new Fragment_Expenses()
                .setActivity(this)
                .setCallback(new Fragment_Expenses.Callback_Expenses() {
                    @Override
                    public void addNewExpense() {
                        fragAddNewExpenses = getNewExpenseFregment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragAddNewExpenses).commit();
                    }

                    @Override
                    public void addErrorLabel() {
                        Toast.makeText(AppActivity.this , "PLEASE SELECT GROUP FIRST !" , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void delete_Expense() {
                        initFragments();
                        fragExpenses.setMyGroupId(groupID);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragExpenses).commit();

                    }

                    @Override
                    public void editExpenseFrag(String Expenseid, String groupId) {
                        fragEditExpense.setMyGroupID(groupId);
                        fragEditExpense.setExpenseId(Expenseid);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragEditExpense).commit();
                    }


                });

    }

    private void initFragmentEditGroup() {
        fragEditGroup = new Fragment_edit_group()
                .setActivity(this)
                .setMyGroupID(groupID)
                .setCallback(new Fragment_edit_group.Callback_edit_group() {

                    @Override
                    public void delete_user_from_group() {
                        initFragments();
                        fragExpenses.setMyGroupId(groupID);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragEditGroup).commit();
                    }

                    @Override
                    public void add_user_to_group() {
                        initFragments();
                        fragExpenses.setMyGroupId(groupID);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragEditGroup).commit();
                    }

                    @Override
                    public void save() {
                        initFragments();
                        fragExpenses.setMyGroupId(groupID);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragGroupList).commit();
                    }
                });

    }

    private void initFragmentTransactions() {
        fragTransactions = new Fragment_Transaction()
                .setActivity(this)
                .setMyGroupID(groupID)
                .setCallback(new Fragment_Transaction.Callback_Transaction() {

                    @Override
                    public void deletedTransaction() {
                        fragTransactions = null;
                        initFragments();
                        fragBalance.setMyGroupID(groupID);
                        fragExpenses.setMyGroupId(groupID);
                        fragTransactions.setMyGroupID(groupID);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragTransactions).commit();
                    }

                    @Override
                    public void addErrorLabel() {

                    }
                });
    }

    private void initFragmantEditExpense() {
        fragEditExpense = new Fragment_Edit_Expense()
                .setActivity(this)
                .setMyGroupID(groupID)
                .setCallback(new Fragment_Edit_Expense.CallBack_Edit_expense() {
                    @Override
                    public void save() {
                        initFragments();
                        fragExpenses.setMyGroupId(groupID);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragExpenses).commit();
                    }
                });
    }

    private void initFragmentGroup() {

        fragGroupList = new Fragment_Group_List()
                .setActivity(this)
                .setCallback(new Fragment_Group_List.Callback_Groups() {

                    @Override
                    public void addNewGroupBtn() {
                        fragAddNewGroup = getNewGroupFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragAddNewGroup).commit() ;
                    }

                    @Override
                    public void setChosenGroup(MyGroup group, int position) {
                        setChosnGroup(group);
                    }

                    @Override
                    public void deleteGroup() {
                        fragGroupList = null;
                        groupID = null;
                        txt.setText("Select a group");
                        initFragments();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragGroupList).commit();
                    }

                    @Override
                    public void EditGroup(MyGroup group) {
                        setChosnGroup(group);
                        fragEditGroup.setMyGroupID(groupID);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragEditGroup ).commit();
                    }
                });
    }

    private void setChosnGroup(MyGroup group) {
        txt.setText(group.getGroupName());
        groupID = group.getGroupId();
        updateAppDataByGroup(group);
    }

    private Fragment_Add_New_Expense getNewExpenseFregment() {

        return new Fragment_Add_New_Expense()
                .setActivity(this)
                .setGroupID(groupID)
                .setCallback(new Fragment_Add_New_Expense.Callback_Add_New_Expense() {
                    @Override
                    public void finishAddProcess() {
                        fragAddNewExpenses = null;
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragExpenses).commit() ;
                    }
                });
    }


    private void updateAppDataByGroup(MyGroup group) {
        if(fragExpenses != null){
            fragExpenses.setMyGroupId(groupID);
        }
        if(fragTransactions != null)
            fragTransactions.setMyGroupID(groupID);
        if(fragBalance!=null)
            fragBalance.setMyGroupID(groupID);
    }



    private Fragment_Add_New_Group getNewGroupFragment() {
       return new Fragment_Add_New_Group()
                .setActivity(this)
                .setCallback(new Fragment_Add_New_Group.Callback_Add_New_Group() {
                    @Override
                    public void finishAddProcess() {
                        fragAddNewGroup = null;
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1 , fragGroupList).commit() ;
                    }
                });
    }

    private void findViews() {

        txt = findViewById(R.id.txt);
        frame1 = findViewById(R.id.frame1);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        IMG_Profile_AppActivity = findViewById(R.id.IMG_Profile_AppActivity);



    }

    private void checkIfUserIsOk() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null ) {
            MyFireBaseRTDB.getUserByPhoneNumber(MyFireBaseRTDB.getMyPhoneNumber(), new MyFireBaseRTDB.CallBack_user() {
                @Override
                public void dataReady(MyUser user) {
                    if (user == null) {
                        startActivity(new Intent(AppActivity.this, LogInActivity.class));
                        finish();
                    }
                    MyFireBaseRTDB.getUserImage(new MyFireBaseRTDB.Callback_Get_users_Img() {

                        @Override
                        public void dataReady(String value) {
                            try {
                                byte[] incode = Base64.decode(value, Base64.DEFAULT);
                                IMG_Profile_AppActivity.setImageBitmap(BitmapFactory.decodeByteArray(incode, 0, incode.length));
                            }
                            catch (Exception e){
                                IMG_Profile_AppActivity.setImageResource(R.drawable.no_picture);
                            }
                        }
                    });
                }
            });
        }
        else{
            startActivity(new Intent(AppActivity.this, LogInActivity.class));
            finish();
        }
    }

}