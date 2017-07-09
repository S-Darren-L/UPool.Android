package com.upool.android.upool.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.upool.android.upool.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class SignInActivity extends AppCompatActivity {
//    public static final String EXTRA_LOG_IN_EMAIL = "com.upool.android.upool.LOGINEMAIL";
//    public static final String EXTRA_LOG_IN_PASSWORD = "com.upool.android.upool.LOGINPASSWORD";

    @BindView(R.id.editEmail)
    EditText emailText;
    @BindView(R.id.editPassword)
    EditText passwordText;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
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
        Intent signInIntent = new Intent(this, VehicleRequestActivity.class);
//        signInIntent.putExtra(EXTRA_LOG_IN_EMAIL, email);
//        signInIntent.putExtra(EXTRA_LOG_IN_PASSWORD, password);
        startActivity(signInIntent);
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
}
