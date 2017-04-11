package com.upool.android.upool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
