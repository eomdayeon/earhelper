// Copyright 2022 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.mysoundclassification
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.os.Build
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


class MainActivity : AppCompatActivity() {
    var TAG = "MainActivity"

    // TODO 2.1: defines the model to be used
    var modelPath = "lite-model_yamnet_classification_tflite_1.tflite"

    // TODO 2.2: defining the minimum threshold
    var probabilityThreshold: Float = 0.25f

    lateinit var textView: TextView

    // 추가 부분 시작
    val channel_name: String = "CHANNEL_1"
    val CHANNEL_ID: String = "MY_CH"
    val notificationId: Int = 1002

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognitionListener: RecognitionListener

    var builder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("사이렌이 울리고 있어요")
        .setContentText("주위를 살펴보세요")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    var builder2 = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("화재 경보가 울리고 있어요")
        .setContentText("주위를 살펴보세요")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
    var srflag = true


    // 추가 부분 끝

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val REQUEST_RECORD_AUDIO = 1337
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO)

        textView = findViewById<TextView>(R.id.output)
        val recorderSpecsTextView = findViewById<TextView>(R.id.textViewAudioRecorderSpecs)

        // TODO 2.3: Loading the model from the assets folder
        val classifier = AudioClassifier.createFromFile(this, modelPath)

        // TODO 3.1: Creating an audio recorder
        val tensor = classifier.createInputTensorAudio()

        // TODO 3.2: showing the audio recorder specification
        val format = classifier.requiredTensorAudioFormat
        val recorderSpecs = "Number Of Channels: ${format.channels}\n" +
                "Sample Rate: ${format.sampleRate}"
        recorderSpecsTextView.text = recorderSpecs

        // TODO 3.3: Creating
        val record = classifier.createAudioRecord()


        // 추가 부분 시작
        var temporarySwitch = findViewById<Switch>(R.id.temporarySwitch1);
        var temporarySwitch2 = findViewById<Switch>(R.id.temporarySwitch2);
        temporarySwitch.isChecked = false;
        temporarySwitch2.isChecked = false;

        var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

        //flag

        var sirenFlag = false
        //



        temporarySwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                record.startRecording()

                Timer().scheduleAtFixedRate(1, 500) {



                    // TODO 4.1: Classifing audio data
                    val numberOfSamples = tensor.load(record)
                    val output = classifier.classify(tensor)

                    // TODO 4.2: Filtering out classifications with low probability
                    val filteredModelOutput = output[0].categories.filter {
                        it.score > probabilityThreshold
                    }

                    // TODO 4.3: Creating a multiline string with the filtered results
                    val outputStr =
                        filteredModelOutput.sortedBy { -it.score }
                            .joinToString(separator = "\n") { "${it.label} -> ${it.score} " }

                    // TODO 4.4: Updating the UI

                    // 사이렌 울릴 때

                    if (outputStr.contains("Police car (siren") && outputStr.contains("Alarm")){
                        sirenFlag = true

                    }
                    // 화재 경보 울릴 때
                    if (outputStr.contains("Chime") && outputStr.contains("Wind chime")){
                        createNotificationChannel(builder2, notificationId)
                    }
                    if (outputStr.isNotEmpty())
                        runOnUiThread {
                            textView.text = outputStr
                        }
                }
            }
            else{
                record.stop()
            }
        }
        Timer().scheduleAtFixedRate(1, 5000) {
            if(sirenFlag == true){
                createNotificationChannel(builder, notificationId)
                sirenFlag = false
            }
        }



        temporarySwitch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setListener()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
                speechRecognizer.setRecognitionListener(recognitionListener)
                speechRecognizer.startListening(intent)

                /*
                Timer().scheduleAtFixedRate(100, 500) {
                    while(srflag == true){
                        srflag = false

                        speechRecognizer.startListening(intent)
                    }

                }
                */


            }


            else {

            }
        }

        // 추가 부분 끝
    }

    // 푸시 알림 함수 시작
    private fun createNotificationChannel(
        builder: NotificationCompat.Builder,
        notificationId: Int
    ) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "1번 채널입니다. "//getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channel_name, importance).apply {
                description = descriptionText
            }

            channel.lightColor = Color.BLUE
            channel.enableVibration(true)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            notificationManager.notify(notificationId, builder.build())

//            with(NotificationManagerCompat.from(this)) {
//                // notificationId is a unique int for each notification that you must define
//                notify(notificationId, builder.build())
//            }
        } else {
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, builder.build())
        }
    }
    // 푸시 알림 함수 끝
    //
    private fun setListener() {
        recognitionListener = object: RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                Toast.makeText(applicationContext, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onBeginningOfSpeech() {

            }

            override fun onRmsChanged(rmsdB: Float) {

            }

            override fun onBufferReceived(buffer: ByteArray?) {

            }

            override fun onEndOfSpeech() {
                srflag = true
            }

            override fun onError(error: Int) {
                var message: String

                when (error) {
                    SpeechRecognizer.ERROR_AUDIO ->
                        message = "오디오 에러"
                    SpeechRecognizer.ERROR_CLIENT ->
                        message = "클라이언트 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                        message = "퍼미션 없음"
                    SpeechRecognizer.ERROR_NETWORK ->
                        message = "네트워크 에러"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                        message = "네트워크 타임아웃"
                    SpeechRecognizer.ERROR_NO_MATCH ->
                        message = "찾을 수 없음"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                        message = "RECOGNIZER가 바쁨"
                    SpeechRecognizer.ERROR_SERVER ->
                        message = "서버가 이상함"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                        message = "말하는 시간초과"
                    else ->
                        message = "알 수 없는 오류"
                }
                Toast.makeText(applicationContext, "에러 발생 $message", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                var matches: ArrayList<String> = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>

                for (i in 0 until matches.size) {
                    textView.text = matches[i]
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {

            }

            override fun onEvent(eventType: Int, params: Bundle?) {

            }

        }
    }
}





