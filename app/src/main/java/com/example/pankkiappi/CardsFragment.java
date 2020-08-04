package com.example.pankkiappi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.pankkiappi.adapters.CardsAdapter;
import com.example.pankkiappi.model.Account;
import com.example.pankkiappi.model.Card;
import com.example.pankkiappi.model.User;
import com.example.pankkiappi.sql.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.security.SecureRandom;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CardsFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView lstCards;
    private TextView txtTitleMessage;

    private Button btnCancel;
    private Button btnAddCard;
    private Spinner spinner;
    private EditText edtAccountName;
    private EditText edtInitAccountBalance;

    private Gson gson;
    private SharedPreferences userPreferences;
    private User user;
    private Account account;

    private View.OnClickListener addCardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == btnCancel.getId()) {
                CardDialog.dismiss();
                Toast toast = Toast.makeText(getActivity(), "Card Creation Cancelled", Toast.LENGTH_SHORT);
                toast.show();
            } else if (v.getId() == btnAddCard.getId()) {
                addCard();


            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cards, container, false);

        fab = view.findViewById(R.id.floating_action_btn);

        lstCards = view.findViewById(R.id.lst_cards);
        txtTitleMessage = view.findViewById(R.id.txt_title_msg);


        setValues();



        return view;
    }
    private Dialog CardDialog;

    private void displayCardDialog() {

        CardDialog = new Dialog(getActivity());
        CardDialog.setContentView(R.layout.card_dialog);

        CardDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        CardDialog.setCanceledOnTouchOutside(true);
        CardDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(getActivity(), "Card Creation Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        edtAccountName = CardDialog.findViewById(R.id.edt_payee_name);
        //edtInitAccountBalance = CardDialog.findViewById(R.id.edt_init_bal);
        spinner = CardDialog.findViewById(R.id.accountSpinner);
        btnCancel = CardDialog.findViewById(R.id.btn_cancel_dialog);
        btnAddCard = CardDialog.findViewById(R.id.btn_add_payee);

        btnCancel.setOnClickListener(addCardClickListener);
        btnAddCard.setOnClickListener(addCardClickListener);

        ArrayList<Account> accounts = user.getAccounts();
        ArrayList<String> cardList = new ArrayList<>();

        for (Account acc : accounts) {
            cardList.add(acc.getAccountName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, cardList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        if(user.getAccounts().size() == 0) {
            Toast toast = Toast.makeText(getContext(), "Create account first", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }


        CardDialog.show();

    }

    private void setValues() {


        userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        user = gson.fromJson(json, User.class);
        account = gson.fromJson(json, Account.class);

        DatabaseHelper db = new DatabaseHelper(getActivity().getApplicationContext());
        this.account.setCardsFromDB(db.getCardsFromCurrentProfile(user.getId()));
        ArrayList<Card> cards = this.account.getCards();
        CardsAdapter adapter = new CardsAdapter(this.getActivity(), R.layout.lst_cards, cards );
        lstCards.setAdapter(adapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayCardDialog();
            }
        });



        txtTitleMessage.setText("Select an Account to view Transactions");

        lstCards.setVisibility(View.VISIBLE);


    }

    public void addCard() {
        String account = spinner.getSelectedItem().toString();
        DatabaseHelper db = new DatabaseHelper(getActivity().getApplicationContext());

        final SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(999999);

        String cardNumber = Integer.toString(randomNumber);

        final SecureRandom randomm = new SecureRandom();
        int cvcnumber = randomm.nextInt(999);

        String cvc = Integer.toString(cvcnumber);

        //user.getAccounts().get(user.getAccounts().size()-1)
        db.saveNewCard(user, account , cardNumber,  cvc);
       // ArrayList<Card> cards = new ArrayList<>();
       // for (Account acc : user.getAccounts())  {
        //   cards.addAll(acc.getCards());
       // }
        this.account.setCardsFromDB(db.getCardsFromCurrentProfile(user.getId()));
        ArrayList<Card> cards = this.account.getCards();
        CardsAdapter adapter = new CardsAdapter(this.getActivity(), R.layout.lst_cards, cards );
        lstCards.setAdapter(adapter);
        SharedPreferences.Editor prefsEditor = userPreferences.edit();
        String json = gson.toJson(user);
        prefsEditor.putString("LastProfileUsed", json).apply();

        CardDialog.dismiss();

        db.close();



    }


}
