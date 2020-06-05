package com.example.pankkiappi.model;

public class Card {

    private String cardNumber;
    private int cvc;

    public  Card(String cardNumber, int cvc) {
        this.cardNumber = cardNumber;
        this.cvc = cvc;
    }

    public String getCardNumber() { return cardNumber;}

    public int getCvc() {return cvc;}
}
