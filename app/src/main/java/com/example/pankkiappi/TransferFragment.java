package com.example.pankkiappi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pankkiappi.model.Account;
import com.example.pankkiappi.model.User;
import com.example.pankkiappi.sql.DatabaseHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class TransferFragment extends Fragment {

    private Spinner spnSendingAccount;
    private EditText edtTransferAmount;
    private Spinner spnReceivingAccount;
    private Button btnConfirmTransfer;

    ArrayList<Account> accounts;
    ArrayAdapter<Account> accountAdapter;

    SharedPreferences userPreferences;
    Gson gson;
    String json;
    User user;

    public TransferFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_transfer, container, false);

        spnSendingAccount = rootView.findViewById(R.id.spn_select_sending_acc);
        edtTransferAmount = rootView.findViewById(R.id.edt_transfer_amount);
        spnReceivingAccount = rootView.findViewById(R.id.spn_select_receiving_acc);
        btnConfirmTransfer = rootView.findViewById(R.id.btn_confirm_transfer);

        setValues();

        return rootView;
    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setValues() {

        userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);

        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        user = gson.fromJson(json, User.class);

        btnConfirmTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmTransfer();
            }
        });

        setAdapters();
    }

    /**
     * method that sets up the adapters
     */
    private void setAdapters() {
        accounts = user.getAccounts();
        accountAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, accounts);
        accountAdapter.setDropDownViewResource(R.layout.spinner_item);

        spnSendingAccount.setAdapter(accountAdapter);
        spnReceivingAccount.setAdapter(accountAdapter);
        spnReceivingAccount.setSelection(1);
    }

    /**
     * method that confirms the transfer
     */
    private void confirmTransfer() {

        int receivingAccIndex = spnReceivingAccount.getSelectedItemPosition();
        boolean isNum = false;
        double transferAmount = 0;

        try {
            transferAmount = Double.parseDouble(edtTransferAmount.getText().toString());
            isNum = true;
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Please enter an amount to transfer", Toast.LENGTH_SHORT).show();
        }
        if (isNum) {
            if (spnSendingAccount.getSelectedItemPosition() == receivingAccIndex) {
                Toast.makeText(getActivity(), "You cannot make a transfer to the same account", Toast.LENGTH_SHORT).show();
            }
            else if(transferAmount < 0.01) {
                Toast.makeText(getActivity(), "The minimum amount for a transfer is $0.01", Toast.LENGTH_SHORT).show();

            } else if (transferAmount > user.getAccounts().get(spnSendingAccount.getSelectedItemPosition()).getAccountBalance()) {

                Account acc = (Account) spnSendingAccount.getSelectedItem();
                Toast.makeText(getActivity(), "The account," + " " + acc.toString() + " " + "does not have sufficient funds to make this transfer", Toast.LENGTH_LONG).show();
            } else {

                int sendingAccIndex = spnSendingAccount.getSelectedItemPosition();

                Account sendingAccount = (Account) spnSendingAccount.getItemAtPosition(sendingAccIndex);
                Account receivingAccount = (Account) spnReceivingAccount.getItemAtPosition(receivingAccIndex);

                user.addTransferTransaction(sendingAccount, receivingAccount, transferAmount);

                spnSendingAccount.setAdapter(accountAdapter);
                spnReceivingAccount.setAdapter(accountAdapter);

                spnSendingAccount.setSelection(sendingAccIndex);
                spnReceivingAccount.setSelection(receivingAccIndex);

                DatabaseHelper Db = new DatabaseHelper(getActivity().getApplicationContext());

                Db.overwriteAccount(user, sendingAccount);
                Db.overwriteAccount(user, receivingAccount);

                Db.saveNewTransaction(user, sendingAccount.getAccountNo(),
                        sendingAccount.getTransactions().get(sendingAccount.getTransactions().size()-1));
                Db.saveNewTransaction(user, receivingAccount.getAccountNo(),
                        receivingAccount.getTransactions().get(receivingAccount.getTransactions().size()-1));


                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                json = gson.toJson(user);
                prefsEditor.putString("LastProfileUsed", json).apply();

                Toast.makeText(getActivity(), "Transfer of $" + String.format(Locale.getDefault(), "%.2f",transferAmount) + " successfully made", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
