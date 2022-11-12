package hung.kv.maikaapp.voicehandle;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

import hung.kv.maikaapp.R;

public class MaikaAssistant implements AssistantLifeCycle, SpeechDetector.SpeechDetectListenner, TextToSpeech.OnUtteranceCompletedListener {
    private final String TAG = MaikaAssistant.class.getName();

    private Context mContext = null;

    private final int tickCountDown = 1000;
    private final int detectKeywordInterval = 3000;
    private final int detectCommandInterval = 5000;
    private final double scaleAfterFailed = 1.5;
    private boolean isKeywordDetected = false;
    private boolean isSpeaking = false;
    private int failedCount = 3;

    TextToSpeech speaker = null;
    SpeechDetector detector = null;


    CountDownTimer detectKeywordTimer = new CountDownTimer(detectKeywordInterval,tickCountDown) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            if (detector != null){
                detector.stopListen();
            }
        }
    };

    CountDownTimer detectCommandTimer = new CountDownTimer(detectCommandInterval,tickCountDown) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            if (detector != null){
                detector.stopListen();
            }
        }
    };

    String user = "bạn";
    Handler handler = new Handler();

    public MaikaAssistant(Context context,String user){
        this.mContext = context;
        detector = new SpeechDetector(context,this);


        speaker = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR){
                    speaker.setLanguage(Locale.getDefault());
                    speaker.setOnUtteranceCompletedListener(MaikaAssistant.this);
                }
            }
        });

        if(!user.isEmpty()){
            this.user = user;
        }


//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                speak("Chào "+user+", tôi là Maika, tôi có thể giúp gì cho bạn?");
//
//            }
//        } ,500);
        start();
    }


    @Override
    public void onDetectKeyword() {

    }

    @Override
    public void onCommand(String commandContent) {

    }

    @Override
    public void onResponse() {
        if (isKeywordDetected){
            detectCommandTimer.start();
        }else {
            detectKeywordTimer.start();
        }
    }

    @Override
    public void start() {
//        detector.listen();
        if (isSpeaking){
            handler.postDelayed(() -> start(),500);
        }else {
            detector.listen(!isKeywordDetected);
            if (isKeywordDetected){
                detectCommandTimer.start();
            }else {
                detectKeywordTimer.start();
            }
        }
    }

    @Override
    public void finish() {

    }

    private void speak(String content) {
        if (speaker != null ){
            isSpeaking = true;
            Log.d(TAG,"speak content : "+content);
            speaker.speak(content,TextToSpeech.QUEUE_FLUSH, null,"");
        }
    }

    private boolean detectKeyword(String content){
        if (content.length() < 10 && content.toLowerCase().contains("maika")){
            return true;
        }
        return false;
    }

    @Override
    public void onDetectedSpeech(String result) {
        if (result.isEmpty() && failedCount--==0){
            failedCount = 3;
            isKeywordDetected = false;
        }

        if (!isKeywordDetected){
            isKeywordDetected = detectKeyword(result);
            if (isKeywordDetected){
                speak("Chào "+user+", tôi có thể giúp gì cho bạn?");
            }
        }
        start();
    }

    @Override
    public void onUtteranceCompleted(String s) {
        Log.d(TAG,"onUtteranceCompleted");
        isSpeaking = false;
    }
}

