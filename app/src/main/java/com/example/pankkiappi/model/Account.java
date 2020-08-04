package com.example.pankkiappi.model;

import java.util.ArrayList;
import java.util.Locale;

public class Account {

    private String accountName;
    private String accountNo;
    private double accountBalance;
    private ArrayList<Transaction> transactions;
    private ArrayList<Card> cards;
    private boolean paymentsAllowed;
    private long dbID;

    //Account constructor
    public Account (String accountName, String accountNo, double accountBalance, boolean paymentsAllowed) {
        this.accountName = accountName;
        this.accountNo = accountNo;
        this.accountBalance = accountBalance;
        cards = new ArrayList<>();
        transactions = new ArrayList<>();
        this.paymentsAllowed = paymentsAllowed;
    }

    //Account constructor
    public Account (String accountName, String accountNo, double accountBalance, long dbID, boolean paymentsAllowed) {
        this(accountName, accountNo, accountBalance, paymentsAllowed);
        this.dbID = dbID;
    }


    public String getAccountName() {
        return accountName;
    }
    public String getAccountNo() {
        return accountNo;
    }
    public double getAccountBalance() {
        return accountBalance;
    }


    public void setDbID(long dbID) { this.dbID = dbID; }

    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public ArrayList<Card> getCards() { return cards;}

    public void setCardsFromDB(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public boolean isPaymentsAllowed() {return paymentsAllowed;}


    public void addDepositTransaction(double amount) {
        accountBalance += amount;


        int depositsCount = 0;

        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.DEPOSIT)  {
                depositsCount++;
            }
        }

        Transaction deposit = new Transaction("T" + (transactions.size() + 1) + "-D" + (depositsCount+1), amount);
        transactions.add(deposit);
    }

    public String toString() {
        return (accountName + " (€" + String.format(Locale.getDefault(), "%.2f",accountBalance) + ")");
    }

    public String toTransactionString() { return (accountName + " (" + accountNo + ")"); }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }
}
