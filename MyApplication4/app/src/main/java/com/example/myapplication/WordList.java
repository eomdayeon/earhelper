package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class WordList extends AppCompatActivity implements View.OnClickListener {
    LinearLayout layoutlist;
    ImageButton addbutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        layoutlist=findViewById(R.id.layout_list);

        addbutton=findViewById(R.id.add);
        addbutton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        addView();
    }

    private void addView() {

        final View cricketerView = getLayoutInflater().inflate(R.layout.edit_text_frame,null,false);
        EditText editText=(EditText)cricketerView.findViewById(R.id.recogedit);
        ImageView imageClose=(ImageView)cricketerView.findViewById(R.id.removelist);

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(cricketerView);
            }
        });
        layoutlist.addView(cricketerView);
    }

    private void removeView(View view){
        layoutlist.removeView(view);
    }
}