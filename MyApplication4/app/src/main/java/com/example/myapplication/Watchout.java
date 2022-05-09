package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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


    //
    String modelPath = "yamnet_classification.tflite";
    float probabilityThreshold = 0.2f;
    AudioClassifier classifier;
    private TensorAudio tensor;
    private AudioRecord record;
    private TimerTask timerTask;

    static final String CHANNEL_ID = "MY_CH";
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;

    String textTitle = "사이렌이 울리고 있어요";
    String textContent = "주변을 살펴보세요";

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    //

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

                            // Updating the UI
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalOutput.isEmpty()) {

                                    } else {
                                       if(s1.contains("Siren")){
                                           Toast toast = Toast.makeText(getApplicationContext(), "사이렌이 울리고 있어요",Toast.LENGTH_SHORT);
                                           toast.show();
                                           sendNotification();
                                       }
                                    }
                                }
                            });
                        }
                    };

                    new Timer().scheduleAtFixedRate(timerTask, 1, 500);
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
        // Android8.0 이상인지 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID
                    ,"Test Notification",mNotificationManager.IMPORTANCE_HIGH);
            // 중요도 설정, Android7.1 이하는 다른 방식으로 지원한다.(위에서 설명)
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("사이렌이 울리고 있어요!")
                .setContentText("주변을 살펴보세요~")
                .setSmallIcon(R.drawable.alarm);
        return notifyBuilder;
    }

    // Notification을 보내는 메소드
    public void sendNotification(){
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());
    }
}