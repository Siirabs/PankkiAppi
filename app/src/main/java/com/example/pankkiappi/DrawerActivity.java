package com.example.pankkiappi;

import androidx.appcompat.app.AppCompatActivity;

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

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.pankkiappi.activities.RegisterActivity;
import com.example.pankkiappi.activities.LoginActivity;
import com.example.pankkiappi.activities.UsersListActivity;
import com.example.pankkiappi.model.User;
import com.example.pankkiappi.sql.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private User user;
    private Gson gson;
    private String json;
    private SharedPreferences userPreferences;

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
            case R.id.nav_transfer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TransferFragment()).commit();
                break;
            case R.id.nav_payment:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PaymentFragment()).commit();
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
}