package com.example.pankkiappi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pankkiappi.model.User;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    public HomeFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setupViews();
        return view;
    }


    private void setupViews() {
        SharedPreferences userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        User user = gson.fromJson(json, User.class);
    }
}
