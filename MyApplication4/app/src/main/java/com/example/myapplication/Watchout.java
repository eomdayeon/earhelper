package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

//

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.view.View;
import android.widget.Toast;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//

public class Watchout extends AppCompatActivity {
    Switch switch1;
    ImageView imageview;
    public final static int REQUEST_RECORD_AUDIO = 2033;
    protected TextView outputTextView;
    private static final String TAG = Watchout.class.getSimpleName();
    private static final String CLIENT_ID = "s77xh2ce69";

    //
    String modelPath = "yamnet_classification.tflite";
    float probabilityThreshold = 0.2f;
    AudioClassifier classifier;
    private TensorAudio tensor;
    private AudioRecord record;
    private TimerTask timerTask;
    private TimerTask timerTask2;
    private boolean sirenFlag = false;
    private boolean fireAlarmFlag = false;

    static final String CHANNEL_ID = "MY_CH";
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchout);

        switch1=findViewById(R.id.watchswitch);
        imageview=findViewById(R.id.imageView2);

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }

        // Loading the model from the assets folder
        try {
            classifier = AudioClassifier.createFromFile(this, modelPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Creating an audio recorder
        tensor = classifier.createInputTensorAudio();

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch1.isChecked())
                {
                    imageview.setImageDrawable(getResources().getDrawable(R.drawable.watchout_on));
                    record = classifier.createAudioRecord();
                    record.startRecording();
                    createNotificationChannel();

                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            // Classifying audio data
                            // val numberOfSamples = tensor.load(record)
                            // val output = classifier.classify(tensor)
                            int numberOfSamples = tensor.load(record);
                            List<Classifications> output = classifier.classify(tensor);

                            // Filtering out classifications with low probability
                            List<Category> finalOutput = new ArrayList<>();
                            for (Classifications classifications : output) {
                                for (Category category : classifications.getCategories()) {
                                    if (category.getScore() > probabilityThreshold) {
                                        finalOutput.add(category);
                                    }
                                }
                            }

                            // Sorting the results
                            Collections.sort(finalOutput, (o1, o2) -> (int) (o1.getScore() - o2.getScore()));

                            // Creating a multiline string with the filtered results
                            StringBuilder outputStr = new StringBuilder();
                            for (Category category : finalOutput) {
                                outputStr.append(category.getLabel())
                                        .append(": ").append(category.getScore()).append("\n");
                            }
                            String s1 = outputStr.toString();
                            if(s1.contains("Siren")){
                                sirenFlag = true;
                            }
                            if(s1.contains("Bell") || s1.contains("Tools") || s1.contains("Mechanisms") ||s1.contains("Alarm clock")) {
                                fireAlarmFlag = true;
                            }
                        }
                    };
                    timerTask2 = new TimerTask(){
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(sirenFlag == true){
                                            Toast toast = Toast.makeText(getApplicationContext(), "사이렌이 울리고 있어요",Toast.LENGTH_SHORT);
                                            toast.show();
                                            sendSirenNotification();
                                            sirenFlag = false;
                                        }
                                        if(fireAlarmFlag == true){
                                            Toast toast = Toast.makeText(getApplicationContext(), "화재경보가 울리고 있어요",Toast.LENGTH_SHORT);
                                            toast.show();
                                            sendFireAlarmNotification();
                                            fireAlarmFlag = false;
                                            openDial();
                                        }
                                }
                            });
                        }
                    };
                    new Timer().scheduleAtFixedRate(timerTask, 1, 500);
                    new Timer().scheduleAtFixedRate(timerTask2, 1, 8000);
                }
                else
                {
                    imageview.setImageDrawable(getResources().getDrawable(R.drawable.watchout_off));
                    timerTask.cancel();
                    record.stop();
                }
            }
        });
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID
                    ,"Test Notification",mNotificationManager.IMPORTANCE_HIGH);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
    private NotificationCompat.Builder getSirenNotificationBuilder() {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("사이렌이 울리고 있어요!")
                .setContentText("주변을 살펴보세요~")
                .setSmallIcon(R.drawable.alarm)
                .setAutoCancel(true);
        return notifyBuilder;
    }
    private NotificationCompat.Builder getFireAlarmNotificationBuilder() {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("화재경보가 울리고 있어요!")
                .setContentText("주변을 살펴보세요~")
                .setSmallIcon(R.drawable.alarm)
                .setAutoCancel(true);
        return notifyBuilder;
    }
    // Notification을 보내는 메소드
    public void sendSirenNotification(){
        NotificationCompat.Builder notifyBuilder = getSirenNotificationBuilder();
        mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());
    }
    public void sendFireAlarmNotification(){
        NotificationCompat.Builder notifyBuilder = getFireAlarmNotificationBuilder();
        mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());
    }
    // 긴급전화
    public void openDial(){
        Intent intent=new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:119"));
        startActivity(intent);
    }
}