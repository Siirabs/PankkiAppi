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

        Card card = getItem(position);

        TextView txtCardNo = convertView.findViewById(R.id.txt_card_no);
        txtCardNo.setText(card.getCardNumber());

        TextView txtAccNo = convertView.findViewById(R.id.txt_acc_no);
       // txtAccNo.setText(card.getCardNumber());

        TextView txtCvc = convertView.findViewById(R.id.txt_cvc);
        //txtCvc.setText(card.getCardNumber());

        return convertView;
    }
}
