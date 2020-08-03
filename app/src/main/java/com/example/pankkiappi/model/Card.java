package com.example.pankkiappi.model;

public class Card {

    private String cardNumber;
    private String cvc;
    private String accountNo;
    private long dbID;

    public Card(String cardNumber, String cvc, String accountNo, long dbID) {
        this.dbID = dbID;
        this.accountNo = accountNo;
        this.cardNumber = cardNumber;
        this.cvc = cvc;
    }

    public String getCardNumber() { return cardNumber;}

    public String getCvc() {return cvc;}

    public String getAccountNo() {return accountNo;}
}
