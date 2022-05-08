package com.example.earui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.earui.R;

public class MainActivity extends AppCompatActivity {
    Switch switch1;
    ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1=findViewById(R.id.watchswitch);
        imageview=findViewById(R.id.imageView2);
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch1.isChecked())
                {
                    imageview.setImageDrawable(getResources().getDrawable(R.drawable.watchout_on));
                }
                else
                {
                    imageview.setImageDrawable(getResources().getDrawable(R.drawable.watchout_off));
                }
            }
        });
    }
}