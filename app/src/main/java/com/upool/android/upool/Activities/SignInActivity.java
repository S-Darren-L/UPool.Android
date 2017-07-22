package com.upool.android.upool.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upool.android.upool.R;
import com.upool.android.upool.Utils.CommonMethods;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
//    public static final String EXTRA_LOG_IN_EMAIL = "com.upool.android.upool.LOGINEMAIL";
//    public static final String EXTRA_LOG_IN_PASSWORD = "com.upool.android.upool.LOGINPASSWORD";

    @BindView(R.id.signInProgressBar)
    ProgressBar signInProgressBar;
    @BindView(R.id.editEmail)
    EditText emailText;
    @BindView(R.id.editPassword)
    EditText passwordText;

    private String email;
    private String password;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        firebaseAuth = firebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    navigateToVehicleRequestActivity(user);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @OnTextChanged(R.id.editEmail)
    public void onEmailTextChanged() {
        email = emailText.getText().toString();
    }

    @OnTextChanged(R.id.editPassword)
    public void onPasswordTextChanged() {
        password = passwordText.getText().toString();
    }

    @OnClick(R.id.buttonSignIn)
    public void onSignInClick() {
        if(CommonMethods.isStringValid(email) && CommonMethods.isStringValid(password)) {
            signInProgressBar.setVisibility(View.VISIBLE);
            signInAccount(email, password);
        }

        // For easy testing:
//        Intent signInIntent = new Intent(this, VehicleRequestActivity.class);
//        startActivity(signInIntent);
    }

    @OnClick(R.id.buttonSignUp)
    public void onSignUpClick() {
        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        startActivity(signUpIntent);
    }

    //Dismiss keyboard when touch outside of EditText
    @Override
    public boolean onTouchEvent(MotionEvent event){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void signInAccount(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                signInProgressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    user = firebaseAuth.getCurrentUser();
                    navigateToVehicleRequestActivity(user);
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToVehicleRequestActivity(FirebaseUser user) {
        Intent signInIntent = new Intent(this, VehicleRequestActivity.class);
        startActivity(signInIntent);
    }
}
