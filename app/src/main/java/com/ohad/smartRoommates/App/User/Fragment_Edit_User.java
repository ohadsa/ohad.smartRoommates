package com.ohad.smartRoommates.App.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ohad.smartRoommates.App.Groups.Fragment_edit_group;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;
import com.ohad.smartRoommates.Utils.Utilities;
import com.ohad.smartRoommates.Utils.Validator;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Fragment_Edit_User extends Fragment {

    private Callback_edit_User callback ;
    private AppCompatActivity activity ;
    private RoundedImageView userImage_edit_user;
    private TextInputLayout firstNameEt_edit_user;
    private TextInputLayout  familyNameEt_edit_user;
    private TextInputLayout  emailEt_edit_user;
    private MaterialButton saveBtn_User_Edit;
    private MaterialButton logoutBtn_User_Edit;
    private FrameLayout userImage_Frame_edit ;
    private String encodeImage;

    private final ActivityResultLauncher<Intent> pickImg = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == activity.RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Picasso
                                    .get()
                                    .load(imageUri)
                                    .into(userImage_edit_user);

                            InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            encodeImage = Utilities.encodeImage(bitmap);
                        } catch (FileNotFoundException e) {

                        }
                    }
                }
            }
    );


    public interface Callback_edit_User{
        void save(String img);
        void logOut();
    }

    public Fragment_Edit_User setCallback(Callback_edit_User callback) {
        this.callback = callback;
        return this;
    }


    public Fragment_Edit_User setActivity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saved ){
        View view = inflater.inflate(R.layout.fragment_edit_user, container , false );
        findViews(view);
        initViews();
        return view;
    }

    private void initViews() {

        userImage_Frame_edit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImg.launch(intent);
        });


        logoutBtn_User_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!=null)
                    callback.logOut();
            }
        });
        saveBtn_User_Edit.setOnClickListener(new View.OnClickListener() {

            Validator v1 = Validator.Builder
                    .make(firstNameEt_edit_user)
                    .addWatcher(new Validator.Watcher_Name("only English letters"))
                    .build();
            Validator v2 = Validator.Builder
                    .make(familyNameEt_edit_user)
                    .addWatcher(new Validator.Watcher_Name("only English letters"))
                    .build();
            Validator v3 = Validator.Builder
                    .make(emailEt_edit_user)
                    .addWatcher(new Validator.Watcher_Email("not Email"))
                    .build();

            @Override
            public void onClick(View v) {

                if(v1.validateIt() && v2.validateIt() && v3.validateIt()) {
                    if (callback != null) {
                        MyUser user = new MyUser()
                                .setUserImg(encodeImage)
                                .setEmail(emailEt_edit_user.getEditText().getText().toString())
                                .setFirstName(firstNameEt_edit_user.getEditText().getText().toString())
                                .setLastName(familyNameEt_edit_user.getEditText().getText().toString())
                                .setPhNumber(MyFireBaseRTDB.getMyPhoneNumber());
                        MyFireBaseRTDB.EditUser(user, new MyFireBaseRTDB.Callback_Edit_User() {
                            @Override
                            public void onSuccesses() {
                                callback.save(user.getUserImg());
                            }
                        });

                    }
                }else
                {

                }
            }
        });


        MyFireBaseRTDB.getUserByPhoneNumber(MyFireBaseRTDB.getMyPhoneNumber(), new MyFireBaseRTDB.CallBack_user() {
            @Override
            public void dataReady(MyUser value) {
                try {
                    byte[] incode = Base64.decode(value.getUserImg(), Base64.DEFAULT);
                    encodeImage = value.getUserImg();
                    userImage_edit_user.setImageBitmap(BitmapFactory.decodeByteArray(incode, 0, incode.length));
                }
                catch (Exception e){}
                firstNameEt_edit_user.getEditText().setText(value.getFirstName());
                familyNameEt_edit_user.getEditText().setText(value.getLastName());
                emailEt_edit_user.getEditText().setText(value.getEmail());
            }
        });
    }

    private void findViews(View view) {


        userImage_edit_user = view.findViewById(R.id.userImage_edit_user);
        firstNameEt_edit_user = view.findViewById(R.id.firstNameEt_edit_user);
        familyNameEt_edit_user = view.findViewById(R.id.familyNameEt_edit_user);
        emailEt_edit_user = view.findViewById(R.id.emailEt_edit_user);
        saveBtn_User_Edit = view.findViewById(R.id.saveBtn_User_Edit);
        logoutBtn_User_Edit = view.findViewById(R.id.logoutBtn_User_Edit);
        userImage_Frame_edit = view.findViewById(R.id.userImage_Frame_edit);


    }
}
