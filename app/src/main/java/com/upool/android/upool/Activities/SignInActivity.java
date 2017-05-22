package com.upool.android.upool.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.upool.android.upool.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                email = emailText.getText().toString();
            }
        });

        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                password = passwordText.getText().toString();
            }
        });
    }

    //Dismiss keyboard when touch outside of EditText
    @Override
    public boolean onTouchEvent(MotionEvent event){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void SignIn(View view){
        Intent signInIntent = new Intent(this, VehicleRequestActivity.class);
//        signInIntent.putExtra(EXTRA_LOG_IN_EMAIL, email);
//        signInIntent.putExtra(EXTRA_LOG_IN_PASSWORD, password);
        startActivity(signInIntent);
    }

    public void SignUp(View view){
        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        startActivity(signUpIntent);
    }
}
