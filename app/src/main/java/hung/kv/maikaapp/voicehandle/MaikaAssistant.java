package hung.kv.maikaapp.voicehandle;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.api.client.util.Maps;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hung.kv.maikaapp.R;
import maikadata.Maikadata;


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
//        start();
//        ArrayList<String> texts = new ArrayList<>();
//        texts.add("Hi");
//        try {
//            detectIntentTexts("goassistant-36628",texts,"sessionID","vi-VN");
//        }catch (Exception e){
//            Log.e(TAG,"error : "+e.getMessage());
//        }
        TestDialogFlow();
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

    public void TestDialogFlow(){
        String result = Maikadata.dialogFlowQuery("goassistant-36628","sessionID","Xin chào","vi-VN");
        Log.d(TAG,"result : "+result);
    }

    public Map<String, QueryResult> detectIntentTexts(
            String projectId, List<String> texts, String sessionId, String languageCode)
            throws IOException, ApiException {
        Map<String, QueryResult> queryResults = Maps.newHashMap();
        // Instantiates a client
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            System.out.println("Session Path: " + session.toString());

            // Detect intents for each text input
            for (String text : texts) {
                // Set the text (hello) and language code (en-US) for the query
                TextInput.Builder textInput =
                        TextInput.newBuilder().setText(text).setLanguageCode(languageCode);

                // Build the query with the TextInput
                QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

                // Performs the detect intent request
                DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

                // Display the query result
                QueryResult queryResult = response.getQueryResult();

                System.out.println("====================");
                System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
                System.out.format(
                        "Detected Intent: %s (confidence: %f)\n",
                        queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
                System.out.format(
                        "Fulfillment Text: '%s'\n",
                        queryResult.getFulfillmentMessagesCount() > 0
                                ? queryResult.getFulfillmentMessages(0).getText()
                                : "Triggered Default Fallback Intent");

                queryResults.put(text, queryResult);
            }
        }
        return queryResults;
    }
}

