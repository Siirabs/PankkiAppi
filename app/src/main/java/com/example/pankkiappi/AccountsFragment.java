package com.example.pankkiappi;

import android.accounts.Account;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pankkiappi.adapters.AccountAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import com.example.pankkiappi.model.User;
import com.example.pankkiappi.sql.DatabaseHelper;

import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class AccountsFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView lstAccounts;
    private TextView txtTitleMessage;

    private Button btnCancel;
    private Button btnAddAccount;
    private EditText edtAccountName;
    private EditText edtInitAccountBalance;
    private Switch allowPayments;

    private Gson gson;
    private SharedPreferences userPreferences;
    private User user;
    private Account account;

    private View.OnClickListener addAccountClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == btnCancel.getId()) {
                accountDialog.dismiss();
                Toast toast = Toast.makeText(getActivity(), "Account Creation Cancelled", Toast.LENGTH_SHORT);
                toast.show();
            } else if (v.getId() == btnAddAccount.getId()) {
                    addAccount();


            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        fab = view.findViewById(R.id.floating_action_btn);

        lstAccounts = view.findViewById(R.id.lst_accounts);
        txtTitleMessage = view.findViewById(R.id.txt_title_msg);


        setValues();

        return view;
    }

    private Dialog accountDialog;
    private int selectedAccountIndex;

    private void displayAccountDialog() {

        accountDialog = new Dialog(getActivity());
        accountDialog.setContentView(R.layout.account_dialog);

        accountDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        accountDialog.setCanceledOnTouchOutside(true);
        accountDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(getActivity(), "Account Creation Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        edtAccountName = accountDialog.findViewById(R.id.edt_payee_name);
        edtInitAccountBalance = accountDialog.findViewById(R.id.edt_init_bal);

        btnCancel = accountDialog.findViewById(R.id.btn_cancel_dialog);
        btnAddAccount = accountDialog.findViewById(R.id.btn_add_payee);
        allowPayments = accountDialog.findViewById(R.id.payments);

        btnCancel.setOnClickListener(addAccountClickListener);
        btnAddAccount.setOnClickListener(addAccountClickListener);

        accountDialog.show();

    }

    private void setValues() {
        selectedAccountIndex = 0;

        userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        user = gson.fromJson(json, User.class);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAccountDialog();
            }
        });


        if (user.getAccounts().size() == 0) {
            txtTitleMessage.setText("Add an Account with the button below");

            lstAccounts.setVisibility(View.GONE);
        } else {
            txtTitleMessage.setText("Select an Account to view Transactions");

            lstAccounts.setVisibility(View.VISIBLE);
        }

        AccountAdapter adapter = new AccountAdapter(this.getActivity(), R.layout.lst_accounts, user.getAccounts());
        lstAccounts.setAdapter(adapter);

        lstAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAccountIndex = i;
                viewAccount();
            }
        });
    }

    private void viewAccount() {
        TransactionFragment transactionsFragment = new TransactionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SelectedAccount", selectedAccountIndex);

        transactionsFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, transactionsFragment,"findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    private void addAccount() {
        String balance = edtInitAccountBalance.getText().toString();
        boolean isNum = true;
        double initDepositAmount = 0;
        if(!(edtAccountName.getText().toString().equals(""))) {

            try {
                initDepositAmount =Double.parseDouble(edtInitAccountBalance.getText().toString());
                isNum = true;
            } catch (Exception e) {
                if (!edtInitAccountBalance.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getActivity(), "Enter a valid amount", Toast.LENGTH_SHORT);
                    toast.show();
                    edtInitAccountBalance.getText().clear();
                }
            }

            if (edtAccountName.getText().toString().length() > 10) {

                Toast.makeText(this.getActivity(), "Not over 10 chars", Toast.LENGTH_SHORT).show();
                edtAccountName.getText().clear();

            } else if ((isNum) || balance.equals("")) {

                boolean match = false;


                String editAccountName = edtAccountName.getText().toString();

                for(com.example.pankkiappi.model.Account account : user.getAccounts()){
                    if(account.getAccountName() != null && account.getAccountName() != null){
                        match = editAccountName.contentEquals(account.getAccountName());
                    }
                }

                if (!match) {

                    DatabaseHelper db = new DatabaseHelper(getActivity().getApplicationContext());
                    if (allowPayments.isChecked()) {
                        user.addAccount(edtAccountName.getText().toString(), 0, true);
                    } else {
                        user.addAccount(edtAccountName.getText().toString(), 0, false);
                    }


                    if (!balance.equals("")) {
                        if (isNum) {
                            if (initDepositAmount >= 0.01) {
                                user.getAccounts().get(user.getAccounts().size()-1).addDepositTransaction(initDepositAmount);
                                db.saveNewTransaction(user, user.getAccounts().get(user.getAccounts().size()-1).getAccountNo(), user.getAccounts().get(user.getAccounts().size()-1).getTransactions().get(user.getAccounts().get(user.getAccounts().size()-1).getTransactions().size()-1));
                            }
                        }
                    }

                    db.saveNewAccount(user, user.getAccounts().get(user.getAccounts().size()-1), allowPayments);

                    Toast.makeText(this.getActivity(), "Account created", Toast.LENGTH_SHORT).show();

                    if (user.getAccounts().size() == 1) {
                        txtTitleMessage.setText("Select an Account to view Transactions");

                        lstAccounts.setVisibility(View.VISIBLE);
                    }

                    ArrayList<com.example.pankkiappi.model.Account> accounts = user.getAccounts();

                    AccountAdapter adapter = new AccountAdapter(getActivity(), R.layout.lst_accounts, accounts);
                    lstAccounts.setAdapter(adapter);

                    SharedPreferences.Editor prefsEditor = userPreferences.edit();
                    String json = gson.toJson(user);
                    String json2 = gson.toJson(account);
                    prefsEditor.putString("LastProfileUsed", json).apply();
                    prefsEditor.putString("Account", json2).apply();

                    accountDialog.dismiss();

                } else {
                    Toast.makeText(this.getActivity(), "This account already exists", Toast.LENGTH_SHORT).show();
                    edtAccountName.getText().clear();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Please enter an account name", Toast.LENGTH_SHORT).show();
        }
        }
    }


