package com.example.pankkiappi.activities;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;

import android.view.View;
import android.widget.Toast;

import com.example.pankkiappi.R;
import com.example.pankkiappi.helpers.InputValidation;

import com.example.pankkiappi.model.User;
import com.example.pankkiappi.sql.DatabaseHelper;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = RegisterActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;
    private TextInputLayout textInputLayoutCity;
    private TextInputLayout textInputLayoutPostalCode;
    private TextInputLayout textInputLayoutAddress;

    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;
    private TextInputEditText textInputEditTextCity;
    private TextInputEditText textInputEditTextPostalCode;
    private TextInputEditText textInputEditTextAddress;

    private AppCompatButton appCompatButtonRegister;
    private AppCompatTextView appCompatTextViewLoginLink;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    private User user;

    private static RegisterActivity r = new RegisterActivity();
    public static RegisterActivity getInstance(){
        return r;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }

    private void initViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);
        textInputLayoutCity = (TextInputLayout) findViewById(R.id.textInputLayoutCity);
        textInputLayoutPostalCode = (TextInputLayout) findViewById(R.id.textInputLayoutPostalCode);
        textInputLayoutAddress = (TextInputLayout) findViewById(R.id.textInputLayoutAddress);

        textInputEditTextName = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmPassword = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);
        textInputEditTextCity = (TextInputEditText) findViewById(R.id.textInputEditTextCity);
        textInputEditTextPostalCode = (TextInputEditText) findViewById(R.id.textInputEditTextPostalCode);
        textInputEditTextAddress = (TextInputEditText) findViewById(R.id.textInputEditTextAddress);

        appCompatButtonRegister = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewLoginLink = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);

    }


    private void initListeners() {
        appCompatButtonRegister.setOnClickListener(this);
        appCompatTextViewLoginLink.setOnClickListener(this);

    }


    private void initObjects() {
        inputValidation = new InputValidation(activity);
        databaseHelper = new DatabaseHelper(activity);
        user = new User();




    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.appCompatButtonRegister:
                postDataToSQLite();
                break;

            case R.id.appCompatTextViewLoginLink:
                finish();
                break;
        }
    }


    private void postDataToSQLite() {
        //validating user inputs, like is email in correct form (requires @), does password match good password patterns.
        if (!inputValidation.isInputEditTextFilled(textInputEditTextName, textInputLayoutName, getString(R.string.error_message_name))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextCity, textInputLayoutCity, getString(R.string.error_message_city))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPostalCode, textInputLayoutPostalCode, getString(R.string.error_message_postal_code))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextAddress, textInputLayoutAddress, getString(R.string.error_message_address))) {
            return;
        }

        if (!inputValidation.isValidPassword(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_invalid_password))) {
            return;
        }

        if (!inputValidation.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword,
                textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return;
        }

        if (!databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim())) {

            user.setName(textInputEditTextName.getText().toString().trim());
            user.setEmail(textInputEditTextEmail.getText().toString().trim());
            user.setCity(textInputEditTextCity.getText().toString().trim());
            user.setPostalCode(textInputEditTextPostalCode.getText().toString().trim());
            user.setAddress(textInputEditTextAddress.getText().toString().trim());

            //Adds admin rights to user
            if (user.getEmail().equals("admin@admin.com") && (user.getName().equals("admin"))) {
                user.setType("admin");
            } else {
                user.setType("user");
            }
            //SecureRandom for generating random salt
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[8];
            random.nextBytes(bytes);

            String saltString = new String(bytes, StandardCharsets.ISO_8859_1);

            String generatedPassword = null;
            String passwordToHash = textInputEditTextPassword.getText().toString().trim()+saltString;
            //Hashing password
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(bytes);

                //Using ISO-8859-1 encoding so that byte[] -> string and string -> byte[] conversion won't break

                byte[] hashedPassword = md.digest(passwordToHash.getBytes(StandardCharsets.ISO_8859_1)); //Käytetään iso-8859-1 enkoodausta jotta byte[] -> string ja takaisin string -> byte[] muunnos ei mene vituiks
                StringBuilder sb = new StringBuilder();
                for(int i=0; i< hashedPassword.length ;i++){
                    sb.append(Integer.toString((hashedPassword[i] & 0xff) + 0x100, 16).substring(1));
                }
                generatedPassword = sb.toString();

                user.setSalt(saltString);
                user.setPassword(generatedPassword);
                databaseHelper.addUser(user);
            } catch(NoSuchAlgorithmException x) {
                x.printStackTrace();
            }

            Toast toast = Toast.makeText(this, "Registration succesful", Toast.LENGTH_LONG);
            toast.show();
            Intent loginactivity = new Intent(activity, LoginActivity.class);
            startActivity(loginactivity);
            emptyInputEditText();


        } else {

            Toast toast = Toast.makeText(this, "Email Already Exists", Toast.LENGTH_LONG);
            toast.show();
        }


    }


    private void emptyInputEditText() {
        textInputEditTextName.setText(null);
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
        textInputEditTextConfirmPassword.setText(null);
        textInputEditTextCity.setText(null);
        textInputEditTextPostalCode.setText(null);
        textInputEditTextAddress.setText(null);
    }
}

