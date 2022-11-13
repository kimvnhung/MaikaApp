package hung.kv.maikaapp.voicehandle;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import hung.kv.maikaapp.R;

public class SpeechDetector implements RecognitionListener {
    private final String TAG = SpeechDetector.class.getName();
    private String result = "";
    SpeechRecognizer speechRecognizer = null;
    SpeechDetectListenner listenner;
    Context mContext;
    private Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);


    public SpeechDetector(Context context, SpeechDetectListenner listenner){
        this.listenner = listenner;
        this.mContext = context;
        onCreate();
    }

    protected void onCreate() {
        if (speechRecognizer == null && mContext != null){
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
            speechRecognizer.setRecognitionListener(this);
            switchSound(true);
        }else {
            Log.e(TAG,"create speech recognizer failed or context is null");
        }
    }

    public void listen(){
        if (speechRecognizer != null){
            speechRecognizer.startListening(listenIntent);
        }
    }

    public void stopListen(){
        if (speechRecognizer != null){
            speechRecognizer.stopListening();
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d(TAG,"onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG,"onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float v) {
//        Log.d(TAG,"onRmsChanged "+v);
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.d(TAG,"onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG,"onEndOfSpeech");
    }

    @Override
    public void onError(int i) {
//        Log.d(TAG,"onError");

        if (listenner != null){
            Log.d(TAG,"error "+i);
            listenner.onDetectedSpeech("");
        }
    }

    @Override
    public void onResults(Bundle bundle) {
        Log.d(TAG,"onResults");
        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d(TAG,"result "+data.get(0));
        if (listenner != null){
            listenner.onDetectedSpeech(data.get(0));
        }
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.d(TAG,"onPartialResults");
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.d(TAG,"onEvent");
    }

    private void switchSound(boolean isMute){
        Log.d(TAG,"isMute : "+isMute);
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (isMute){
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }

    public interface SpeechDetectListenner {
        void onDetectedSpeech(String result);
    }
}
