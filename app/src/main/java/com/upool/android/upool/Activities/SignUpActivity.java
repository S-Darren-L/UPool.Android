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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upool.android.upool.Models.User;
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

    private User user;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        if(user == null)
            user = new User();

        setSupportActionBar(signUpToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        titleSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        user.setTitle(titleSpinner.getItemAtPosition(0).toString());

        firebaseAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_users));

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null) {
                    navigateToVehicleRequestActivity(firebaseUser);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void navigateToVehicleRequestActivity(FirebaseUser user) {
        Intent vehicleRequestIntent = new Intent(this, VehicleRequestActivity.class);
        vehicleRequestIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(vehicleRequestIntent);
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
        user.setEmail(emailET.getText().toString());
    }

    @OnTextChanged(R.id.editTextPassword)
    public void onPasswordTextChanged() {
        user.setPassword(passwordET.getText().toString());
    }

    @OnTextChanged(R.id.editTextFirstName)
    public void onFirstNameTextChanged() {
        user.setFirstName(firstNameET.getText().toString());
    }

    @OnTextChanged(R.id.editTextLastName)
    public void onLastNameTextChanged() {
        user.setLastName(lastNameET.getText().toString());
    }

    @OnClick(R.id.buttonRegister)
    public void onRegisterClicked() {
        if(CommonMethods.isStringValid(user.getEmail()) && CommonMethods.isStringValid(user.getPassword()) && CommonMethods.isStringValid(user.getFirstName()) && CommonMethods.isStringValid(user.getLastName())) {
            signUpProgressBar.setVisibility(View.VISIBLE);
            signUpAccount(user.getEmail(), user.getPassword());
        }
    }

    private void signUpAccount(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                signUpProgressBar.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    user.setUserID(firebaseAuth.getCurrentUser().getUid());
                    DatabaseReference currentUserDatabase = userDatabase.child(user.getUserID());
                    currentUserDatabase.child(getString(R.string.db_user_title)).setValue(user.getTitle());
                    currentUserDatabase.child(getString(R.string.last_name)).setValue(user.getLastName());
                    currentUserDatabase.child(getString(R.string.first_name)).setValue(user.getFirstName());
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
            user.setTitle(adapterView.getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
