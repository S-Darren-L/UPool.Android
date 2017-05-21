package com.upool.android.upool.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.upool.android.upool.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.editTextEmail)
    EditText emailEditText;
    @BindView(R.id.textViewEmailError)
    TextView emailErrorTextView;
    @BindView(R.id.editTextPassword)
    EditText passwordEditText;
    @BindView(R.id.textViewPasswordError)
    TextView passwordErrorTextView;
    @BindView(R.id.spinnerTitle)
    Spinner titleSpinner;
    @BindView(R.id.editTextFirstName)
    EditText firstNameEditText;
    @BindView(R.id.editTextLastName)
    EditText lastNameEditText;

    private String email;
    private String emailError;
    private String password;
    private String passwordError;
    private String title;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        titleSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void Register(View view){

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
