package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WordList extends AppCompatActivity implements View.OnClickListener {
    LinearLayout layoutlist;
    ImageButton addbutton;
    SavedWords globalSavedWords = (SavedWords) getApplication();
    ArrayList<String> savedWords = globalSavedWords.getSavedWords();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        layoutlist=findViewById(R.id.layout_list);
        addbutton=findViewById(R.id.add);
        addbutton.setOnClickListener(this);

        loadSavedWords();
    }

    @Override
    public void onClick(View v) {
        addView();
    }

    private void addView() {

        final View cricketerView = getLayoutInflater().inflate(R.layout.edit_text_frame,null,false);
        EditText editText=(EditText)cricketerView.findViewById(R.id.recogedit);
        ImageView imageClose=(ImageView)cricketerView.findViewById(R.id.removelist);
        Button  saveButton=(Button)cricketerView.findViewById(R.id.save);
        Intent intent = new Intent(getApplicationContext(),WordRecognition.class);

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(cricketerView);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( editText.getText().toString().length() == 0 ) {
                    Toast toast = Toast.makeText(getApplicationContext(), "단어를 입력해주세요",Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "저장했어요",Toast.LENGTH_SHORT);
                    toast.show();
                    final View cricketerView2 = getLayoutInflater().inflate(R.layout.edit_text_frame2,null,false);
                    EditText editText2=(EditText)cricketerView2.findViewById(R.id.recogedit);
                    ImageView imageClose2=(ImageView)cricketerView2.findViewById(R.id.removelist);
                    editText2.setText(editText.getText().toString());
                    savedWords.add(editText.getText().toString());
                    removeView(cricketerView);
                    editText2.setClickable(false);
                    editText2.setFocusable(false);
                    intent.putStringArrayListExtra("ArrayList", savedWords);
                    imageClose2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast toast = Toast.makeText(getApplicationContext(), "삭제했어요",Toast.LENGTH_SHORT);
                            toast.show();
                            savedWords.remove(savedWords.size()-1);
                            intent.putStringArrayListExtra("ArrayList", savedWords);
                            removeView(cricketerView2);
                        }
                    });
                    layoutlist.addView(cricketerView2);
                }
            }
        });
        layoutlist.addView(cricketerView);
    }

    private void removeView(View view){
        layoutlist.removeView(view);
    }

    private void loadSavedWords(){
        Intent intent = new Intent(getApplicationContext(),WordRecognition.class);
        for(int i = 0; i<savedWords.size(); i++){
            final View cricketerView = getLayoutInflater().inflate(R.layout.edit_text_frame2,null,false);
            EditText editText=(EditText)cricketerView.findViewById(R.id.recogedit);
            ImageView imageClose=(ImageView)cricketerView.findViewById(R.id.removelist);
            editText.setText(savedWords.get(i).toString());
            editText.setClickable(false);
            editText.setFocusable(false);
            int finalI = i;
            imageClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(getApplicationContext(), "삭제했어요",Toast.LENGTH_SHORT);
                    toast.show();
                    savedWords.remove(savedWords.remove(finalI));
                    intent.putStringArrayListExtra("ArrayList", savedWords);
                    removeView(cricketerView);
                }
            });
            layoutlist.addView(cricketerView);
        }
    }
}

