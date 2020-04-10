package com.example.pankkiappi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    private Bundle bundle;
    private String username;
    private String passwword;

    private EditText editUsername;
    private EditText editPassword;
    private Button logIn;
    private CheckBox rememberAcc;
    private Button createAcc;
}
