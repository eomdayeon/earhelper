package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

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