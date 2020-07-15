package com.example.pankkiappi.model;

public class Card {

    private String cardNumber;
    private String cvc;
    private String linkedAccount;
    private long dbID;

    public Card(String cardNumber, String cvc, String linkedAccount, long dbID) {
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.linkedAccount = linkedAccount;
        this.dbID = dbID;
    }

    public String getCardNumber() { return cardNumber;}

    public String getCvc() {return cvc;}
}
