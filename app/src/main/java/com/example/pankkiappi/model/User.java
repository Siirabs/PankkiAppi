package com.example.pankkiappi.model;

import java.util.ArrayList;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String salt;
    private String type;
    private ArrayList<Account> accounts;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public ArrayList<Account> getAccounts() { return accounts; }
    
    public void addAccount(String accountName, double accountBalance) {
        String accNo = "A " + (accounts.size() + 1);
        Account account = new Account(accountName, accNo, accountBalance);
        accounts.add(account);
    }
}