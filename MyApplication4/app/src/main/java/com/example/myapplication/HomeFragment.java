package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;




public class HomeFragment extends Fragment {
    private View view;
    CardView word;
    CardView watchout;
    Button btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ActionBar actionBar = ((NavigationActivity)getActivity()).getSupportActionBar();
        actionBar.hide();

        word =view.findViewById(R.id.word);
        watchout =view.findViewById(R.id.watchout);
        btn = view.findViewById(R.id.btnConnect);


        word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),WordRecognition.class);
                startActivity(intent);
            }
        });

        watchout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Watchout.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Bluetooth.class);
                startActivity(intent);
            }
        });

        return view;
    }
}