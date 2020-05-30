package com.example.pankkiappi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.pankkiappi.activities.RegisterActivity;
import com.example.pankkiappi.activities.LoginActivity;
import com.example.pankkiappi.activities.UsersListActivity;
import com.example.pankkiappi.model.User;
import com.example.pankkiappi.model.Account;
import com.example.pankkiappi.sql.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.Locale;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public enum manualNavID {
        DASHBOARD_ID,
        ACCOUNTS_ID
    }

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private User user;
    private Gson gson;
    private String json;
    private SharedPreferences userPreferences;

    private Dialog depositDialog;
    private Spinner spnAccounts;
    private ArrayAdapter<Account> accountAdapter;
    private EditText edtDepositAmount;
    private Button btnCancel;
    private Button btnDeposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userPreferences = this.getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        user = gson.fromJson(json, User.class);

        loadFromDB();
        SharedPreferences.Editor prefsEditor = userPreferences.edit();
        json = gson.toJson(user);
        prefsEditor.putString("LastProfileUsed", json).apply();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_accounts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AccountsFragment()).commit();
                break;
            case R.id.nav_deposit:
                displayDepositDialog();
            case R.id.nav_transfer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TransferFragment()).commit();
                break;
            case R.id.nav_payment:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PaymentFragment()).commit();
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).commit();
                break;
            case R.id.nav_logout:
                Toast toast = Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT);
                toast.show();
                Intent logIntent = new Intent(this, LoginActivity.class);
                startActivity(logIntent);
                break;

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void loadFromDB() {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());

        //user.setPayeesFromDB(db.getPayeesFromCurrentProfile(user.getId()));
        user.setAccountsFromDB(db.getAccountsFromCurrentProfile(user.getId()));

        for (int iAccount = 0; iAccount < user.getAccounts().size(); iAccount++) {
            user.getAccounts().get(iAccount).setTransactions(db.getTransactionsFromCurrentAccount(user.getId(), user.getAccounts().get(iAccount).getAccountNo()));
        }
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer((GravityCompat.START));
        } else {
            super.onBackPressed();
        }
    }

    private void displayDepositDialog() {

        depositDialog = new Dialog(this);
        depositDialog.setContentView(R.layout.deposit_dialog);

        depositDialog.setCanceledOnTouchOutside(true);
        depositDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                Toast.makeText(DrawerActivity.this, "Deposit Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        spnAccounts = depositDialog.findViewById(R.id.dep_spn_accounts);
        accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, user.getAccounts());
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAccounts.setAdapter(accountAdapter);
        spnAccounts.setSelection(0);

        edtDepositAmount = depositDialog.findViewById(R.id.edt_deposit_amount);

        btnCancel = depositDialog.findViewById(R.id.btn_cancel_deposit);
        btnDeposit = depositDialog.findViewById(R.id.btn_deposit);

        btnCancel.setOnClickListener(depositClickListener);
        btnDeposit.setOnClickListener(depositClickListener);

        depositDialog.show();

    }

    private View.OnClickListener depositClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnCancel.getId()) {
                depositDialog.dismiss();

                Toast.makeText(DrawerActivity.this, "Deposit Cancelled", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == btnDeposit.getId()) {
                makeDeposit();
            }
        }
    };



    private void makeDeposit() {

        int selectedAccountIndex = spnAccounts.getSelectedItemPosition();

        double depositAmount = 0;
        boolean isNum = false;

        try {
            depositAmount = Double.parseDouble(edtDepositAmount.getText().toString());
            isNum = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (depositAmount < 0.01 && !isNum) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        } else {

            Account account = user.getAccounts().get(selectedAccountIndex);
            account.addDepositTransaction(depositAmount);

            SharedPreferences.Editor prefsEditor = userPreferences.edit();
            gson = new Gson();
            json = gson.toJson(user);
            prefsEditor.putString("LastProfileUsed", json).apply();

            DatabaseHelper Db = new DatabaseHelper(getApplicationContext());
            Db.overwriteAccount(user, account);
            Db.saveNewTransaction(user, account.getAccountNo(),
                    account.getTransactions().get(account.getTransactions().size()-1));

            Toast.makeText(this, "Deposit of $" + String.format(Locale.getDefault(), "%.2f",depositAmount) + " " + "made successfully", Toast.LENGTH_SHORT).show();

            accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, user.getAccounts());
            accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnAccounts.setAdapter(accountAdapter);

            //TODO: Add checkbox if the user wants to make more than one deposit

            depositDialog.dismiss();

        }
    }
}