package com.example.pankkiappi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.example.pankkiappi.adapters.AccountAdapter;
import com.example.pankkiappi.model.Account;
import com.example.pankkiappi.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CardsFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView lstCards;
    private TextView txtTitleMessage;
    private TextView txtDetailMessage;
    private Button btnCancel;
    private Button btnAddCard;
    private Spinner spinner;
    private EditText edtAccountName;
    private EditText edtInitAccountBalance;

    private Gson gson;
    private SharedPreferences userPreferences;
    private User user;

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
        txtDetailMessage = view.findViewById(R.id.txt_details_msg);

        setValues();



        return view;
    }
    private Dialog CardDialog;

    private void displayAccountDialog() {

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



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAccountDialog();
            }
        });

    }

    public void addCard() {





    }


}
