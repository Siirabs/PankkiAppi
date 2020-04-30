package com.example.pankkiappi.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import com.example.pankkiappi.R;
import com.example.pankkiappi.DrawerActivity;
import com.example.pankkiappi.sql.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;

import java.security.SecureRandom;

import javax.xml.datatype.Duration;

public class digitCode extends AppCompatActivity {
    private final AppCompatActivity activity = digitCode.this;
    private DatabaseHelper databaseHelper;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digitcode);
        getSupportActionBar().hide();


        initObjects();
        random();

    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
    }

    public void random() {
        final SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(999999);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.top_coordinator), "Your verification code is " + String.format("%06d", randomNumber), Snackbar.LENGTH_INDEFINITE);

        snackbar.show();
        //Toast toast = Toast.makeText(this, "Your verification code is " + randomNumber, Toast.LENGTH_SHORT);
        // toast.setGravity(Gravity.TOP,0,0);
        //toast.show();
        final String randomString = String.format("%06d", randomNumber);

        final android.widget.Button button = (android.widget.Button) findViewById(R.id.codeBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editText = (EditText) findViewById(R.id.digitCode);
                final String temp = editText.getText().toString();
                System.out.println(temp);
                Bundle bundle = getIntent().getExtras();
                String email = bundle.getString("EMAIL");

                if (randomString.equals(temp)) {
                        if (!databaseHelper.checkAdmin(email)) {
                            Intent drawerIntent = new Intent(activity, DrawerActivity.class);
                            System.out.println("user paneeli");
                            startActivity(drawerIntent);
                        } else {
                            Intent drawerIntent = new Intent(activity, DrawerActivity.class);
                            System.out.println("admin paneeli");
                            startActivity(drawerIntent);
                        }
                    } else {
                        editText.setText(null);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Try again", Toast.LENGTH_SHORT);
                        toast.show();
                        random();
                }
            }
        });
    }
}
