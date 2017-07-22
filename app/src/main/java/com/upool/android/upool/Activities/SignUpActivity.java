package com.upool.android.upool.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    @BindView(R.id.signUpToolbar)
    Toolbar signUpToolbar;
    @BindView(R.id.signUpProgressBar)
    ProgressBar signUpProgressBar;
    @BindView(R.id.editTextEmail)
    EditText emailET;
//    @BindView(R.id.textViewEmailError)
//    TextView emailErrorTextView;
    @BindView(R.id.editTextPassword)
    EditText passwordET;
//    @BindView(R.id.textViewPasswordError)
//    TextView passwordErrorTextView;
    @BindView(R.id.spinnerTitle)
    Spinner titleSpinner;
    @BindView(R.id.editTextFirstName)
    EditText firstNameET;
    @BindView(R.id.editTextLastName)
    EditText lastNameET;

    private String email;
//    private String emailError;
    private String password;
//    private String passwordError;
    private String title;
    private String firstName;
    private String lastName;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        setSupportActionBar(signUpToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        titleSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        title = titleSpinner.getItemAtPosition(0).toString();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user != null) {
            navigateToVehicleRequestActivity(user);
        }
    }

    private void navigateToVehicleRequestActivity(FirebaseUser user) {
        Intent signInIntent = new Intent(this, VehicleRequestActivity.class);
        startActivity(signInIntent);
    }

    //Dismiss keyboard when touch outside of EditText
    @Override
    public boolean onTouchEvent(MotionEvent event){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    //Add navigation back button on ToolBar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnTextChanged(R.id.editTextEmail)
    public void onEmailTextChanged() {
        email = emailET.getText().toString();
    }

    @OnTextChanged(R.id.editTextPassword)
    public void onPasswordTextChanged() {
        password = passwordET.getText().toString();
    }

    @OnTextChanged(R.id.editTextFirstName)
    public void onFirstNameTextChanged() {
        firstName = firstNameET.getText().toString();
    }

    @OnTextChanged(R.id.editTextLastName)
    public void onLastNameTextChanged() {
        lastName = lastNameET.getText().toString();
    }

    @OnClick(R.id.buttonRegister)
    public void onRegisterClicked() {
        if(CommonMethods.isStringValid(email) && CommonMethods.isStringValid(password) && CommonMethods.isStringValid(firstName) && CommonMethods.isStringValid(lastName)) {
            signUpProgressBar.setVisibility(View.VISIBLE);
            signUpAccount(email, password);
        }
    }

    private void signUpAccount(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                signUpProgressBar.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            title = adapterView.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
