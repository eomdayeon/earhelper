package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginTabFragment extends Fragment {
    EditText id, pass;
    Button login;
    TextView forgetPass;
    ImageView login_logo;
    float v = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);

        id = root.findViewById(R.id.input_id);
        pass = root.findViewById(R.id.input_pw);
        login = root.findViewById(R.id.btn_login);
        forgetPass = root.findViewById(R.id.forget_pass);
        login_logo = root.findViewById(R.id.login_logo);

        id.setTranslationX(800);
        pass.setTranslationX(800);
        login.setTranslationX(800);
        forgetPass.setTranslationX(800);
        login_logo.setTranslationX(800);

        login_logo.setAlpha(v);
        id.setAlpha(v);
        pass.setAlpha(v);
        login.setAlpha(v);
        forgetPass.setAlpha(v);

        login_logo.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(100).start();
        id.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        pass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        forgetPass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),NavigationActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}
