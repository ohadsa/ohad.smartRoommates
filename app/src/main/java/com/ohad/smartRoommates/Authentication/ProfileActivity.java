package com.ohad.smartRoommates.Authentication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ohad.smartRoommates.App.AppActivity;
import com.ohad.smartRoommates.App.User.MyUser;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;
import com.ohad.smartRoommates.Utils.Utilities;
import com.ohad.smartRoommates.Utils.Validator;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth ;
    private TextInputLayout firstNameEt;
    private TextInputLayout familyNameEt;
    private TextInputLayout emailEt;
    private MaterialButton privateDetailsBtn ;
    private RoundedImageView userImage_Propile_Activity ;
    private TextView txt_userImage_Propile_Activity ;
    private String encodeImage;
    private FrameLayout userImage_Frame ;

    private final ActivityResultLauncher<Intent> pickImg =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData()!=null){
                        Uri imageUri = result.getData().getData();
                        try{
                            Picasso
                                    .get()
                                    .load(imageUri)
                                    .into(userImage_Propile_Activity);

                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            txt_userImage_Propile_Activity.setVisibility(View.GONE);
                            encodeImage = Utilities.encodeImage(bitmap);
                        }
                        catch (FileNotFoundException e){

                        }
                    }
                }
            }
    );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();
        initViews();
        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

    }

    private void initViews() {
        userImage_Frame.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImg.launch(intent);
        });
    }

    private void findViews() {
        firstNameEt = findViewById(R.id.firstNameEt);
        familyNameEt = findViewById(R.id.familyNameEt);
        txt_userImage_Propile_Activity = findViewById(R.id.txt_userImage_Propile_Activity);
        privateDetailsBtn = findViewById(R.id.privateDetailsBtn);
        emailEt = findViewById(R.id.emailEt);
        userImage_Frame = findViewById(R.id.userImage_Frame);
        userImage_Propile_Activity = findViewById(R.id.userImage_Propile_Activity);
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser !=null ) {

            privateDetailsBtn.setOnClickListener(new View.OnClickListener() {
                Validator v1 = Validator.Builder
                        .make(firstNameEt)
                        .addWatcher(new Validator.Watcher_Name("only English letters"))
                        .build();
                Validator v2 = Validator.Builder
                        .make(familyNameEt)
                        .addWatcher(new Validator.Watcher_Name("only English letters"))
                        .build();
                Validator v3 = Validator.Builder
                        .make(emailEt)
                        .addWatcher(new Validator.Watcher_Email("not Email"))
                        .build();

                @Override
                public void onClick(View v) {



                    String fName = firstNameEt.getEditText().getText().toString().trim();
                    String lName = familyNameEt.getEditText().getText().toString().trim();
                    String mail = emailEt.getEditText().getText().toString().trim();

                    if( v1.validateIt() && v2.validateIt() && v3.validateIt()){
                        MyUser user = new MyUser()
                                .setFirstName(fName)
                                .setLastName(lName)
                                .setEmail(mail)
                                .setPhNumber(firebaseUser.getPhoneNumber().trim())
                                .setUserImg(encodeImage);

                        MyFireBaseRTDB.saveNewUser(user);

                        startActivity(new Intent(ProfileActivity.this , AppActivity.class));
                        finish();

                    }
                    else{
                        Toast.makeText(ProfileActivity.this , "wrong inputs" , Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        else {
            //user is not logged in
            finish();
        }
    }






    private boolean validValues() {
        return true;
    }
}