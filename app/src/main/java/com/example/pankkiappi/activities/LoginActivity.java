package com.example.pankkiappi.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import androidx.core.widget.NestedScrollView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pankkiappi.DrawerActivity;
import com.example.pankkiappi.R;
import com.example.pankkiappi.helpers.InputValidation;
import com.example.pankkiappi.model.User;
import com.example.pankkiappi.sql.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Random;
import java.util.regex.Matcher;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity = LoginActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private AppCompatTextView textViewLinkRegister;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }


    /**
     * This method is to initialize views
     */
    private void initViews() {

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        inputValidation = new InputValidation(activity);
        user = new User();

    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
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

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyFromSQLite() {
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
                , textInputEditTextPassword.getText().toString().trim()+user.getSalt().toString())) {
            /*byte[] generatedSalt = user.getSalt();
            System.out.println(user.getName());
            System.out.println(generatedSalt);
            System.out.println(user.getEmail());*/
            if (databaseHelper.checkUser(textInputEditTextPassword.getText().toString().trim()+user.getSalt().toString())) {
                String generatedPassword = null;
                String passwordToHash = textInputEditTextPassword.getText().toString().trim()+user.getSalt().toString();
                System.out.println(textInputEditTextPassword.getText().toString().trim()+user.getSalt());
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-512");
                    md.update(user.getSalt());
                    byte[] hashedPassword = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i< hashedPassword.length ;i++){
                        sb.append(Integer.toString((hashedPassword[i] & 0xff) + 0x100, 16).substring(1));
                    }
                    generatedPassword = sb.toString();
                    String password = generatedPassword;
                    System.out.println(password);
                } catch(NoSuchAlgorithmException x) {
                    // do proper exception handling
                }


                emptyInputEditText();
                Intent digitCode = new Intent(activity, digitCode.class);
                startActivity(digitCode);
                //Intent accountsIntent = new Intent(activity, UsersListActivity.class);
                //Intent drawerIntent = new Intent(activity, DrawerActivity.class);
                // accountsIntent.putExtra("EMAIL", textInputEditTextEmail.getText().toString().trim());

                //startActivity(drawerIntent);


            } else {
                // Snack Bar to show success message that record is wrong
                //Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
                Toast toast = Toast.makeText(this, "Wrong Email or Password", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }
}