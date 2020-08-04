package com.example.pankkiappi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.pankkiappi.adapters.TransactionAdapter;
import com.example.pankkiappi.model.Account;
import com.example.pankkiappi.model.User;
import com.example.pankkiappi.model.Transaction;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TransactionFragment extends Fragment {

    public enum TransactionTypeFilter {
        ALL_TRANSACTIONS(0),
        TRANSFERS(1),
        DEPOSITS(2);

        private final int transFilterID;
        TransactionTypeFilter(int transFilterID) {
            this.transFilterID = transFilterID;
        }

        public TransactionTypeFilter getTransFilter(int index) {
            for (TransactionTypeFilter filter : TransactionTypeFilter.values()) {
                if (filter.transFilterID == index) {
                    return filter;
                }
            }
            return null;
        }
    }

    public enum DateFilter {
        OLDEST_NEWEST(0),
        NEWEST_OLDEST(1);

        private final int dateFilterID;
        DateFilter(int dateFilterID) {
            this.dateFilterID = dateFilterID;
        }

        public DateFilter getDateFilter(int index) {
            for (DateFilter filter : DateFilter.values()) {
                if (filter.dateFilterID == index) {
                    return filter;
                }
            }
            return null;
        }

    }

    class TransactionComparator implements Comparator<Transaction> {
        public int compare(Transaction transOne, Transaction transTwo) {

            Date dateOne = null;
            Date dateTwo = null;

            try {
                dateOne = Transaction.DATE_FORMAT.parse(transOne.getTimestamp());
                dateTwo = Transaction.DATE_FORMAT.parse(transTwo.getTimestamp());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (dateOne.compareTo(dateTwo) > 0) {
                return (1);
            } else if (dateOne.compareTo(dateTwo) < 0) {
                return (-1);
            } else if (dateOne.compareTo(dateTwo) == 0) {
                return (1);
            }
            return (1);
        }
    }

    private TextView txtAccountName;
    private TextView txtAccountBalance;

    private TextView txtTransactionMsg;
    private TextView txtTransfersMsg;

    private TextView txtDepositMsg;

    private Spinner spnAccounts;
    private Spinner spnTransactionTypeFilter;
    private Spinner spnDateFilter;

    private TransactionTypeFilter transFilter;
    private DateFilter dateFilter;

    Spinner.OnItemSelectedListener spnClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView.getId() == spnAccounts.getId()) {
                selectedAccountIndex = i;
                txtAccountName.setText("Account: " + user.getAccounts().get(selectedAccountIndex).toTransactionString());
                txtAccountBalance.setText("Balance: €" + String.format(Locale.getDefault(), "%.2f",user.getAccounts().get(selectedAccountIndex).getAccountBalance()));
            }
            else if (adapterView.getId() == spnTransactionTypeFilter.getId()) {
                transFilter = transFilter.getTransFilter(i);
            }
            else if (adapterView.getId() == spnDateFilter.getId()) {
                dateFilter = dateFilter.getDateFilter(i);
            }

            setupTransactionAdapter(selectedAccountIndex, transFilter, dateFilter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private ListView lstTransactions;

    private User user;

    private int selectedAccountIndex;

    public TransactionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        getActivity().setTitle("Transactions");
        selectedAccountIndex = bundle.getInt("SelectedAccount", 0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_transaction, container, false);

        txtAccountName = rootView.findViewById(R.id.txt_account_name);
        txtAccountBalance = rootView.findViewById(R.id.txt_account_balance);

        txtTransactionMsg = rootView.findViewById(R.id.txt_no_transactions);

        txtTransfersMsg = rootView.findViewById(R.id.txt_no_transfers);
        txtDepositMsg = rootView.findViewById(R.id.txt_no_deposits);

        spnAccounts = rootView.findViewById(R.id.spn_accounts);
        spnTransactionTypeFilter = rootView.findViewById(R.id.spn_type_filter);
        spnDateFilter = rootView.findViewById(R.id.spn_date_filter);

        lstTransactions = rootView.findViewById(R.id.lst_transactions);


        setValues();
        return rootView;
    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setValues() {

        SharedPreferences userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        user = gson.fromJson(json, User.class);

        transFilter = TransactionTypeFilter.ALL_TRANSACTIONS;
        dateFilter = DateFilter.OLDEST_NEWEST;

        setupTransactionAdapter(selectedAccountIndex, transFilter, dateFilter);

        setupSpinners();
        spnAccounts.setSelection(selectedAccountIndex);

        txtAccountName.setText("Account: " + user.getAccounts().get(selectedAccountIndex).toTransactionString());
        txtAccountBalance.setText("Balance: €" + String.format(Locale.getDefault(), "%.2f",user.getAccounts().get(selectedAccountIndex).getAccountBalance()));
    }

    private void setupSpinners() {

        ArrayAdapter<Account> accountAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, user.getAccounts());
        accountAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnAccounts.setAdapter(accountAdapter);

        ArrayAdapter<String> transTypeAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.transaction_filters));
        transTypeAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnTransactionTypeFilter.setAdapter(transTypeAdapter);

        ArrayAdapter<String> dateFilterAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.date_filters));
        dateFilterAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnDateFilter.setAdapter(dateFilterAdapter);

        spnAccounts.setOnItemSelectedListener(spnClickListener);
        spnTransactionTypeFilter.setOnItemSelectedListener(spnClickListener);
        spnDateFilter.setOnItemSelectedListener(spnClickListener);

    }

    /**
     * method used to setup the adapters
     */
    private void setupTransactionAdapter(int selectedAccountIndex, TransactionTypeFilter transFilter, DateFilter dateFilter) {
        ArrayList<Transaction> transactions = user.getAccounts().get(selectedAccountIndex).getTransactions();

        txtDepositMsg.setVisibility(GONE);
        txtTransfersMsg.setVisibility(GONE);


        if (transactions.size() > 0) {

            txtTransactionMsg.setVisibility(GONE);
            lstTransactions.setVisibility(VISIBLE);

            if (dateFilter == DateFilter.OLDEST_NEWEST) {
                Collections.sort(transactions, new TransactionComparator());
            } else if (dateFilter == DateFilter.NEWEST_OLDEST) {
                Collections.sort(transactions, Collections.reverseOrder(new TransactionComparator()));
            }

            if (transFilter == TransactionTypeFilter.ALL_TRANSACTIONS) {
                TransactionAdapter transactionAdapter = new TransactionAdapter(getActivity(), R.layout.lst_transactions, transactions);
                lstTransactions.setAdapter(transactionAdapter);
            }
            else if (transFilter == TransactionTypeFilter.TRANSFERS) {
                displayTransfers(transactions);
            }
            else if (transFilter == TransactionTypeFilter.DEPOSITS) {
                displayDeposits(transactions);
            }

        } else {
            txtTransactionMsg.setVisibility(VISIBLE);
            lstTransactions.setVisibility(GONE);
        }

    }



    private void displayTransfers(ArrayList<Transaction> transactions) {
        ArrayList<Transaction> transfers = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.TRANSFER) {
                transfers.add(transactions.get(i));
            }
        }
        if (transfers.size() == 0) {
            txtTransfersMsg.setVisibility(VISIBLE);
            lstTransactions.setVisibility(GONE);
        } else {
            lstTransactions.setVisibility(VISIBLE);
            TransactionAdapter transactionAdapter = new TransactionAdapter(getActivity(), R.layout.lst_transactions, transfers);
            lstTransactions.setAdapter(transactionAdapter);
        }
    }

    private void displayDeposits(ArrayList<Transaction> transactions) {
        ArrayList<Transaction> deposits = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.DEPOSIT) {
                deposits.add(transactions.get(i));
            }
        }
        if (deposits.size() == 0) {
            txtDepositMsg.setVisibility(VISIBLE);
            lstTransactions.setVisibility(GONE);
        } else {
            lstTransactions.setVisibility(VISIBLE);
            TransactionAdapter transactionAdapter = new TransactionAdapter(getActivity(), R.layout.lst_transactions, deposits);
            lstTransactions.setAdapter(transactionAdapter);
        }
    }

}
