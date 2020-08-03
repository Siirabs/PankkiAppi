package com.example.pankkiappi.model;

public class Card {

    private String cardNumber;
    private String cvc;
    private String accountNo;
    private long dbID;

    public Card(long dbID, String accountNo, String cardNumber, String cvc) {
        this.dbID = dbID;
        this.accountNo = accountNo;
        this.cardNumber = cardNumber;
        this.cvc = cvc;
    }

    public String getCardNumber() { return cardNumber;}

    public String getCvc() {return cvc;}

    public String getAccountNo() {return accountNo;}
}
