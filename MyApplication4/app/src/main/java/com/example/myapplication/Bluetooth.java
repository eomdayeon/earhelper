package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarItemView;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import java.util.Set;

public class Bluetooth extends AppCompatActivity implements View.OnClickListener{

    private BluetoothSPP bt;
    private Foreground_Service mservice=null;
    private boolean mbound=false;
    private BroadcastReceiver mReceiver=null;
    private final String BROADCAST_MESSAGE_COONECT="com.jtmcompany.waist_guard_project.connect";
    private ServiceConnection mConnection; //서비스 바인드를 위한 콜백변수
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private boolean bluetoohConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        init();
        initSet();
    }

    public void init(){
        mScanBtn = findViewById(R.id.scan);
        mOffBtn=findViewById(R.id.off);

        //블루투스 연결되어있는지 정보가져오기
        SharedPreferences auto=getSharedPreferences("bt_connect?", MODE_PRIVATE);
        bluetoohConn=auto.getBoolean("mconnected",false);
    }

    public void initSet(){
        mScanBtn.setOnClickListener(this);
        mOffBtn.setOnClickListener(this);
    }

    public void initCallback(){
        mConnection=new ServiceConnection() {
            //바인드되면 포그라운드서비스 시작시킴
            //바인드되면 service=mbinder가됨
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Foreground_Service.Mybinder binder= (Foreground_Service.Mybinder)service;
                mservice=binder.getService();
                mbound= true;
                Intent mIntent = new Intent(getApplicationContext(), Foreground_Service.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startForegroundService(mIntent);
                else
                    startService(mIntent);


                //서비스 시작시 ,블루투스관련코드
                bt = mservice.getBlueToothSPP();
                if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
                    Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
                    finish();
                }

                if (!bt.isBluetoothEnabled()) { //블루투스가 꺼져있다면 키도록 액션요청
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
                } else {
                    if (!bt.isServiceAvailable()) {
                        bt.setupService();
                        bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                    }
                }
            }

            //예기치않은 상황 kill됬을때 호출됨
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mservice=null;
                mbound=false;
            }
        };
    }


    /**포그라운드서비스에서 블루트스가 연결됬다는 메시지를 수신(BroadCast) **/
    private void registerReceiver(){
        if(mReceiver !=null) return;
        final IntentFilter mFilter=new IntentFilter();
        mFilter.addAction(BROADCAST_MESSAGE_COONECT);
        mReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //블루투스가 연결됬다는 메시지를 받았다면->메인화면으로 이동
                if(intent.getAction().equals(BROADCAST_MESSAGE_COONECT)){
                    Intent sensorintent=new Intent(getApplicationContext(), NavigationActivity.class);
                    startActivity(sensorintent);
                    Toast.makeText(getApplicationContext(), "Bluetooth 연결되었습니다.", Toast.LENGTH_SHORT).show();
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

    //액티비티 화면이 안보일때 바인드 해제
    @Override
    protected void onStop() {
        super.onStop();
        if(mbound){
            unbindService(mConnection);
            mbound=false;
        }

    }

    //액티비티 화면이 종료될때 바인드 해제
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == RESULT_OK)
                mservice.getBlueToothSPP().connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) { //블루투스 활성화? OK버튼을눌렀을때
                mservice.getBlueToothSPP().setupService();
                mservice.getBlueToothSPP().startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mScanBtn) {
            initCallback();

            bindService(new Intent(this, Foreground_Service.class), mConnection,BIND_AUTO_CREATE); //서비스생성 & 바인드
            registerReceiver(); //브로드캐스트 등록

            //블루투스가연결되있으면 자동으로 화면전환
            if(bluetoohConn) {
                Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                startActivity(intent);
                finish();
            }

            //페어링 가능한 리스트 출력
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

        } else if (v == mOffBtn) {
            Intent intent = new Intent(getApplicationContext(), Foreground_Service.class);
            stopService(intent);
            finish();
        }
    }
}
