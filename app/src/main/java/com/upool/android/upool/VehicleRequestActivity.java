package com.upool.android.upool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VehicleRequestActivity extends AppCompatActivity {

//    @BindView(R.id.textViewEmail) TextView emailTextView;
//    @BindView(R.id.textViewPassword) TextView passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_request);
        ButterKnife.bind(this);

//        Intent intent = getIntent();
//        String email = intent.getStringExtra(SignInActivity.EXTRA_LOG_IN_EMAIL);
//        String password = intent.getStringExtra(SignInActivity.EXTRA_LOG_IN_PASSWORD);
//
//        emailTextView.setText(email);
//        passwordTextView.setText(password);
    }
}
