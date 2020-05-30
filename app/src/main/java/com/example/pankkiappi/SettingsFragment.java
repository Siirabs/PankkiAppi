package com.example.pankkiappi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pankkiappi.model.User;
import com.example.pankkiappi.sql.DatabaseHelper;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    private EditText name;
    private EditText city;
    private EditText postalCode;
    private EditText address;
    private User user;
    private Gson gson;
    private SharedPreferences userPreferences;
    private Button button;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        name = view.findViewById(R.id.updname);
        city = view.findViewById(R.id.updcity);
        postalCode = view.findViewById(R.id.updpostalcode);
        address = view.findViewById(R.id.updaddress);

        setInfo();

        button = view.findViewById(R.id.changes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        return view;
    }

    public void setInfo() {
        userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        user = gson.fromJson(json, User.class);

        name.setText(user.getName());
        city.setText(user.getCity());
        postalCode.setText(user.getPostalCode());
        address.setText(user.getAddress());
    }

    public void save() {
        String upName = name.getText().toString();
        String upCity = city.getText().toString();
        String upCode = postalCode.getText().toString();
        String upAddress = address.getText().toString();

        user.setName(upName);
        user.setCity(upCity);
        user.setPostalCode(upCode);
        user.setAddress(upAddress);

        DatabaseHelper db = new DatabaseHelper(getActivity().getApplicationContext());

        db.updateUser(user);

        db.close();
    }
}
