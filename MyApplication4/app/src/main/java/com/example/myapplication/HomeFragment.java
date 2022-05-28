package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeFragment extends Fragment {
    private View view;
    TextView h_str;
    TextView t_str;
    ImageView bluetooth;
    CardView word;
    CardView watchout;
    ProgressBar hum_progress;
    ProgressBar temp_progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ActionBar actionBar = ((NavigationActivity) getActivity()).getSupportActionBar();
        actionBar.hide();

        h_str = view.findViewById(R.id.humidity_str);
        t_str = view.findViewById(R.id.temperature_str);
        word = view.findViewById(R.id.word);
        watchout = view.findViewById(R.id.watchout);
        bluetooth = view.findViewById(R.id.bluetooth_btn);

        try{
            EventBus.getDefault().register(this);
        }catch (Exception e){}


        word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WordRecognition.class);
                startActivity(intent);
            }
        });

        watchout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Watchout.class);
                startActivity(intent);
            }
        });

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Bluetooth.class);
                startActivity(intent);

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("TAKMIN", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAKMIN", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("TAKMIN", "onDetach");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void testEvent(NavigationActivity.DataEvent event){
        setDataString(event.helloEventBus);
    }

    private void setDataString(String helloEventBus) {
        String[] s = helloEventBus.split("/");
        String h= s[0];
        String t= s[1];
        h=h+"%";
        t=t+"°C";
        h_str.setText(h);
        t_str.setText(t);
    }
}