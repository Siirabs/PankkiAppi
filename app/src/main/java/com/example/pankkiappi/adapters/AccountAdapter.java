package com.example.pankkiappi.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.example.pankkiappi.R;
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

    public View getView (int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);

        }

        Account account = getItem(position);

        TextView txtAccountName = convertView.findViewById(R.id.txt_account_name);
        txtAccountName.setText(account.getAccountName());

        TextView txtAccountNo = convertView.findViewById(R.id.txt_acc_no);
        txtAccountNo.setText(context.getString(R.string.account_no) + " " + account.getAccountNo());

        TextView txtAccountBalance = convertView.findViewById(R.id.txt_balance);
        txtAccountBalance.setText("Account balance: €" + String.format("%.2f",account.getAccountBalance()));




        return convertView;
    }


}

