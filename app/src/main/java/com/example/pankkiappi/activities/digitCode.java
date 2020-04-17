package com.example.pankkiappi.activities;

import android.app.Activity;
import android.content.Intent;
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
import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

import javax.xml.datatype.Duration;

public class digitCode extends AppCompatActivity {
    private final AppCompatActivity activity = digitCode.this;

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digitcode);

        getSupportActionBar().hide();
        random();

    }

    public void random() {
        final Random random = new Random();
        int randomNumber = random.nextInt(999999 - 100000) + 100000;

        Snackbar snackbar = Snackbar.make(findViewById(R.id.top_coordinator), "Your verification code is " + randomNumber , Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        //Toast toast = Toast.makeText(this, "Your verification code is " + randomNumber, Toast.LENGTH_SHORT);
       // toast.setGravity(Gravity.TOP,0,0);
        //toast.show();
        final String r = String.valueOf(randomNumber);

        final android.widget.Button button = (android.widget.Button) findViewById(R.id.codeBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editText = (EditText) findViewById(R.id.digitCode);
                final String temp = editText.getText().toString();
                System.out.println(r);
                System.out.println(temp);

                if (r.equals(temp)) {
                    Intent drawerIntent = new Intent(activity, DrawerActivity.class);
                    startActivity(drawerIntent);
                } else {
                    editText.setText(null);
                    Toast toast= Toast.makeText(getApplicationContext(),
                            "Try again", Toast.LENGTH_SHORT);
                    toast.show();
                    random();


                }
            }
        });


    }


}
