package com.example.pankkiappi.model;

import java.util.ArrayList;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String salt;
    private String city;
    private String postalCode;
    private String address;
    private String type;
    private ArrayList<Account> accounts = new ArrayList<>();

    private ArrayList<Payee> payees;

   // public void setInfo(int id, String name, String email) {
       // this.id = id;
        //this.name = name;
       // this.email = email;

    //}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCity() { return city;}
    public void setCity(String city) {this.city = city;}

    public String getPostalCode() { return postalCode;}
    public void setPostalCode(String postalCode) {this.postalCode = postalCode;}

    public String getAddress() { return address;}
    public void setAddress(String address) {this.address = address;}

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public ArrayList<Account> getAccounts() { return accounts; }
    public ArrayList<Payee> getPayees() { return payees; }
    //public void setAccounts(ArrayList<Account> accounts) {this.accounts = accounts;}

    public void setAccountsFromDB(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(String accountName, double accountBalance, int payments) {
        String accNo = "A" + (accounts.size() + 1);
        Account account = new Account(accountName, accNo, accountBalance, payments);
        accounts.add(account);
    }


    public void setPayeesFromDB(ArrayList<Payee> payees) {
        this.payees = payees;
    }

}