package com.example.pankkiappi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import androidx.core.widget.NestedScrollView;

import android.view.View;

import android.widget.CheckBox;
import android.widget.Toast;


import com.example.pankkiappi.R;
import com.example.pankkiappi.helpers.InputValidation;

import com.example.pankkiappi.model.User;
import com.example.pankkiappi.sql.DatabaseHelper;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity = LoginActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;
    private CheckBox checkBox;
    private AppCompatTextView textViewLinkRegister;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    private User lastProfileUsed;
    private Gson gson;
    private String json;
    private SharedPreferences userPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }


    private void initViews() {
        userPreferences = this.getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);


        textViewLinkRegister = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);


    }


    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);


    }


    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        inputValidation = new InputValidation(activity);

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                verifyFromSQLite();


                break;
            case R.id.textViewLinkRegister:
                // Navigate to RegisterActivity
                RegisterActivity r = RegisterActivity.getInstance();
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }


    private void verifyFromSQLite() {
        //verifying account from database
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_email))) {
            return;
        }


        if (databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim()
                , textInputEditTextPassword.getText().toString().trim())) {
            String email = textInputEditTextEmail.getText().toString().trim();


            ArrayList<User> users = databaseHelper.getAllUser();

            boolean match = false;
            if (users.size() > 0) {
                for (int i = 0; i < users.size(); i++) {
                    if (textInputEditTextEmail.getText().toString().equals(users.get(i).getEmail())) {


                       match = true;

                        lastProfileUsed = users.get(i);

                        //saving last profile used to json
                        SharedPreferences.Editor prefsEditor = userPreferences.edit();
                        gson = new Gson();
                        json = gson.toJson(lastProfileUsed);
                        prefsEditor.putString("LastProfileUsed", json).apply();

                        //starting digitcode activity as login credentials were correct
                        Intent digitCode = new Intent(activity, digitCode.class);
                        emptyInputEditText();
                        digitCode.putExtra("EMAIL", email);
                        startActivity(digitCode);
                    }

                }
                if (!match) {
                    Toast toast = Toast.makeText(this, "Wrong Email or Password", Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {

                Toast toast = Toast.makeText(this, "Wrong Email or Password", Toast.LENGTH_LONG);
                toast.show();
            }
        }



        }
    private void emptyInputEditText () {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }
    }