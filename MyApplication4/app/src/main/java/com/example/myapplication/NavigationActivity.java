package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayDeque;
import java.util.Deque;

public class  NavigationActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;

    private HomeFragment homeFragment;
    private MypageFragment mypageFragment;
    private SetupFragment setupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bn_home:
                        setFrag(0);
                        break;
                    case R.id.bn_mypage:
                        setFrag(1);
                        break;
                    case R.id.bn_setup:
                        setFrag(2);
                        break;
                }

            }
        });
        homeFragment =new HomeFragment();
        mypageFragment = new MypageFragment();
        setupFragment = new SetupFragment();

        setFrag(0);

    }

    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n){
            case 0:
                ft.replace(R.id.fragment, homeFragment);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.fragment, mypageFragment);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.fragment, setupFragment);
                ft.commit();
                break;
        }
    }

}