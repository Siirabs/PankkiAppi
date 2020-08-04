package com.example.pankkiappi.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pankkiappi.R;
import com.example.pankkiappi.model.Account;
import com.example.pankkiappi.model.Card;

import java.util.ArrayList;

public class CardsAdapter extends ArrayAdapter<Card> {

    private Context context;
    private int resource;

    public CardsAdapter(Context context, int resource, ArrayList<Card> cards) {
        super(context, resource, cards);

        this.context = context;
        this.resource = resource;
    }

    public View getView (int position, android.view.View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }

        //Using adapter to place cards linked accounts, card numbers and cvc to correct places
        Card card = getItem(position);
        String Cvc = card.getCvc();
        String CardNumber = card.getAccountNo();
        String AccountNo = card.getCardNumber();

        TextView linkedAccount = convertView.findViewById(R.id.txt_linked_account);
        linkedAccount.setText("Linked Account: " + AccountNo);

        TextView cardNo = convertView.findViewById(R.id.txt_card_no);
        cardNo.setText("Card Number: " + Cvc);

        TextView cvc = convertView.findViewById(R.id.txt_cvc);
        cvc.setText("CVC: " + CardNumber);




        return convertView;
    }
}
