package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayDeque;
import java.util.Deque;

public class  NavigationActivity extends AppCompatActivity {
    private BroadcastReceiver mReceiver;
    private final String BROADCAST_MESSAGE_DISCONNECT="com.jtmcompany.waist_guard_project.connect";

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

        registerReceiver(); //브로드캐스트등록

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

    //브로드캐스트리시버등록
    //포그라운드서비스에서 블루투스 연결이 끊어지면 메시지보냄-> UserServiceActivity엑티비티로돌아감
    private void registerReceiver(){
        if(mReceiver !=null) return;
        final IntentFilter mFilter= new IntentFilter();
        mFilter.addAction(BROADCAST_MESSAGE_DISCONNECT);
        mReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(BROADCAST_MESSAGE_DISCONNECT)){
                    Intent sensorintent = new Intent(getApplicationContext(), Bluetooth.class);
                    startActivity(sensorintent);

                    finish();
                }
            }
        };
        registerReceiver(mReceiver,mFilter);
    }

    private void unregisterReceiver(){
        if(mReceiver !=null){
            unregisterReceiver(mReceiver);
            mReceiver=null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.off) {
            //블루투스 연결해제 알림
            SharedPreferences auto = getSharedPreferences("bt_connect?", MODE_PRIVATE);
            SharedPreferences.Editor auto_convert = auto.edit();
            auto_convert.putBoolean("mconnected", false);
            auto_convert.commit();

        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    public static class DataEvent {

        public final String helloEventBus;

        public DataEvent(String helloEventBus) {
            this.helloEventBus = helloEventBus;
        }
    }

}