package com.ohad.smartRoommates.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.ohad.smartRoommates.App.AppActivity;
import com.ohad.smartRoommates.Utils.MyFireBaseRTDB;
import com.ohad.smartRoommates.App.User.MyUser;
import com.ohad.smartRoommates.R;
import com.ohad.smartRoommates.Utils.Utilities;
import com.ohad.smartRoommates.Utils.Validator;

import java.util.concurrent.TimeUnit;


public class LogInActivity extends AppCompatActivity {

    //if code send failed, will used to resend code OTP
    private PhoneAuthProvider.ForceResendingToken forceResendingToken ;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks ;
    private String mVerificationId; // will hold OTP/verification code
    private static final String TAG = "MAIN_TAG" ;
    private LinearLayout phoneLl ;
    private LinearLayout codeLl ;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd ;
    private MaterialButton phoneContinueBtn ;
    private MaterialTextView resendCodeTv;
    private MaterialButton codeSubmitBtn;
    private MaterialTextView codeSentDescription ;
    private TextInputLayout phoneEt;
    private TextInputLayout codeEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);
        findViews();
        phoneLl.setVisibility(View.VISIBLE);
        codeLl.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this) ;
        pd.setTitle("Please wait ...");
        pd.setCanceledOnTouchOutside(false);
        mCallbacks = initPhoneCallbacks();
        initClickLiseners() ;

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks initPhoneCallbacks() {

        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                /*
                this callback will invoke 2 situations:
                    1 - Instant verification. In some cases the phone number can
                        be instantly verified without needing to send or enter a
                        verification code.
                    2 - Auto retrieval. On some devices Google play service can
                        automatically detect the incoming verification SMS and perform
                        verification without user action.
                 */

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                /*
                this callback is invoke in an invalid request for verification
                is made, for instance if the phone number format is not valid
                 */

                pd.dismiss();
                Toast.makeText(LogInActivity.this , "sent" , Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String VerificationId  ,@NonNull PhoneAuthProvider.ForceResendingToken token){
                super.onCodeSent(VerificationId , forceResendingToken);
                /*
                the SMS verification code has been sent to the provided
                phone number, we now need to ask the user to enter the
                code and then construct a credential by combining the code
                with a verification ID .
                 */

                Log.d(TAG , "onCodeSent: "+VerificationId);

                mVerificationId = VerificationId ;
                forceResendingToken = token ;
                pd.dismiss();

                //hide phone layout
                phoneLl.setVisibility(View.GONE);
                codeLl.setVisibility(View.VISIBLE);
                Toast.makeText(LogInActivity.this , "Verification code sent..." , Toast.LENGTH_SHORT).show();
                codeSentDescription.setText("Please type verification code we sent \nto " + phoneEt.getEditText().getText().toString().trim());

            }
        };
    }

    private void initClickLiseners() {

        phoneContinueBtn.setOnClickListener(new View.OnClickListener() {
            Validator v1 = Validator.Builder
                    .make(phoneEt)
                    .addWatcher(new Validator.Watcher_Integer("not valid phone"))
                    .addWatcher(new Validator.Watcher_Positive("not valid phone"))
                    .addWatcher(new Validator.Watcher_Length("not valid phone"))
                    .build();
            @Override
            public void onClick(View v) {

                String phone = phoneEt.getEditText().getText().toString().trim();
                phone = Utilities.reWriteNumber(phone);

                if(!v1.validateIt()){
                    Toast.makeText(LogInActivity.this , "please enter phone number" , Toast.LENGTH_SHORT).show();
                }
                else {
                    startPhoneNumberVerification(phone);
                }
            }
        });

        resendCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = phoneEt.getEditText().getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(LogInActivity.this , "please enter phone number..." , Toast.LENGTH_SHORT).show();
                }
                else {
                    resendVerificationCode(phone , forceResendingToken);
                }

            }
        });

        codeSubmitBtn.setOnClickListener(new View.OnClickListener() {
            Validator v1 = Validator.Builder
                    .make(codeEt)
                    .addWatcher(new Validator.Watcher_Integer("not valid code"))
                    .addWatcher(new Validator.Watcher_Positive("not valid code"))
                    .addWatcher(new Validator.Watcher_Length6("not valid code"))
                    .build();
            @Override
            public void onClick(View v) {

                String code = codeEt.getEditText().getText().toString().trim();
                if(!v1.validateIt()){
                    Toast.makeText(LogInActivity.this , "wrong..." , Toast.LENGTH_SHORT).show();
                }
                else{
                    verifyPhoneNumberWithCode(mVerificationId ,code);
                }
            }
        });


    }




    private void findViews() {
        phoneLl = findViewById(R.id.phoneLl);
        codeLl = findViewById(R.id.codeLl);
        phoneContinueBtn = findViewById(R.id.phoneContinueBtn);
        resendCodeTv = findViewById(R.id.resendCodeTv);
        codeSubmitBtn = findViewById(R.id.codeSubmitBtn);
        phoneEt = findViewById(R.id.phoneEt);
        codeEt = findViewById(R.id.codeEt);
        codeSentDescription = findViewById(R.id.codeSentDescription);

    }



    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Verifying Phone Number");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L , TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phone , PhoneAuthProvider.ForceResendingToken token ) {
        pd.setMessage("Resending code");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L , TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }


    private void verifyPhoneNumberWithCode(String mVerificationId, String code) {

        pd.setMessage("Verifying code");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId , code );
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Logging In");
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //successfully signed in
                        pd.dismiss();
                        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(LogInActivity.this, "Logged In as " + phone, Toast.LENGTH_SHORT).show();
                        //start profile activity

                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        MyUser user;

                        MyFireBaseRTDB.getUserByPhoneNumber(phone , open_activity_by_user_existing(phone));
                    }
                });
    }

    private MyFireBaseRTDB.CallBack_user open_activity_by_user_existing(String phone) {


        MyFireBaseRTDB.CallBack_user res = new MyFireBaseRTDB.CallBack_user() {


            @Override
            public void dataReady(MyUser value) {
                if (value != null ) {

                    startActivity(new Intent(LogInActivity.this, AppActivity.class));
                    finish();

                } else {

                    startActivity(new Intent(LogInActivity.this, ProfileActivity.class));
                    finish();
                }
            }

        };

        return res ;
    }
}