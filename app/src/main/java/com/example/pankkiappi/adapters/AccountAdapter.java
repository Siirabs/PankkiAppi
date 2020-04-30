package com.example.pankkiappi.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.pankkiappi.model.Account;

import java.util.ArrayList;

public class AccountAdapter extends ArrayAdapter<Account> {

    private Context context;
    private int resource;

    public AccountAdapter(Context context, int resource, ArrayList<Account> accounts) {
        super(context, resource, accounts);

        this.context = context;
        this.resource = resource;
    }
}

