package hung.kv.maikaapp.voicehandle;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import maikadata.Maikadata;


public class MaikaAssistant implements AssistantLifeCycle, SpeechDetector.SpeechDetectListenner, TextToSpeech.OnUtteranceCompletedListener {
    private final String TAG = MaikaAssistant.class.getName();

    private final int tickCountDown = 1000;
    private final int detectKeywordInterval = 3000;
    private final int resetSessionInterval = 180000;
    private boolean isKeywordDetected = false;
    private boolean isSpeaking = false;

    TextToSpeech speaker = null;
    SpeechDetector detector;


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

    CountDownTimer resetSessionTimer = new CountDownTimer(resetSessionInterval,tickCountDown) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            isKeywordDetected = false;
        }
    };

    String user = "bạn";
    Handler handler = new Handler();

    public MaikaAssistant(Context context,String user){
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
            resetSessionTimer.start();
        }else {
            detectKeywordTimer.start();
        }
    }

    @Override
    public void start() {
        if (isSpeaking){
            handler.postDelayed(() -> start(),500);
        }else {
            detector.listen();
            detectKeywordTimer.start();
        }
    }

    @Override
    public void finish() {

    }

    private void speak(Map<String,String> replacing, String content) {
        if (speaker != null ){
            isSpeaking = true;
            Log.d(TAG,"speak content : "+content);
            Set keys = replacing.keySet();

            for (Iterator i = keys.iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                String value = replacing.get(key);
                content = content.replace(key,value);
            }
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
        if (!result.isEmpty()){
            if (!isKeywordDetected){
                isKeywordDetected = detectKeyword(result);
            }

            if (isKeywordDetected){
                Map<String,String> jeyt = new HashMap<>();
                jeyt.put("@user",user);
                speak(jeyt,getResponse(result));
            }

            resetSessionTimer.cancel();
        }else {
            resetSessionTimer.start();
        }
        start();
    }

    @Override
    public void onUtteranceCompleted(String s) {
        Log.d(TAG,"onUtteranceCompleted");
        isSpeaking = false;
    }

    public String getResponse(String question){
        return Maikadata.dialogFlowQuery("goassistant-36628","sessionID",question,"vi-VN");
    }




}

