package hung.kv.maikaapp.voicehandle;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.api.services.drive.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import hung.kv.maikaapp.LoginActivity;
import maikadata.Maikadata;


public class MaikaAssistant implements AssistantLifeCycle, SpeechDetector.SpeechDetectListenner, TextToSpeech.OnUtteranceCompletedListener {
    private final String TAG = MaikaAssistant.class.getName();

    private final int tickCountDown = 1000;
    private final int detectKeywordInterval = 3000;
    private final int resetSessionInterval = 180000;
    private boolean isKeywordDetected = false;
    private boolean isSpeaking = false;

    TextToSpeech speaker = null;
    TextToSpeech speakerOther = null;
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
            position = "";
            destination = "";
        }
    };



    String user = "bạn";
    UserType userType = UserType.GUEST;
    Handler handler = new Handler();

    String position = "";
    String destination = "";

    public MaikaAssistant(Context context,String user, UserType userType){
        detector = new SpeechDetector(context,this);
        this.userType = userType;

        speaker = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR){
                    speaker.setLanguage(Locale.getDefault());
                    speaker.setOnUtteranceCompletedListener(MaikaAssistant.this);
                }
            }
        });

        speakerOther = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR){
                    speakerOther.setLanguage(Locale.SIMPLIFIED_CHINESE);
                    speakerOther.setOnUtteranceCompletedListener(MaikaAssistant.this);
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
        Log.d(TAG,"Stop listener");
        detector.destroy();
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
            if (replacing.get("kem") != null && replacing.get("kem").equals("true")){
                speakerOther.speak(content,TextToSpeech.QUEUE_FLUSH, null,"");
            }else{
                speaker.speak(content,TextToSpeech.QUEUE_FLUSH, null,"");
            }
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
            Log.d(TAG,"result "+result);
            if (!isKeywordDetected){
                isKeywordDetected = detectKeyword(result);
            }

            if (isKeywordDetected){
                Map<String,String> jeyt = new HashMap<>();
                jeyt.put("@user",user);
                String rs = getResponse(result);
                Log.d(TAG,"rs "+rs);
                if (rs.contains("Gặp mặt")){
                    String name = rs.substring(13);
                    if (!name.equals("Tên")){
                        String lic = LoginActivity.db.getLCT(name);
                        rs = lic;
                    }else {
                        if (new Random().nextInt()%2==0){
                            rs = "Bạn muốn gặp ai cơ?";
                        }else {
                            rs = "Bạn muốn gặp ai? Tôi không nghe rõ!";
                        }
                    }
                }else if (rs.contains("Giờ")){
                    Date toDay = new Date();
                    rs = "Bây giờ là "+toDay.getHours()+" giờ "+toDay.getMinutes()+" phút";
                }else if (result.toLowerCase().contains("kem")){
                    jeyt.put("kem","true");
                }else if(rs.contains("khóa biểu")){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = format.parse(rs.replace("Thời khóa biểu",""));
//                        String other = "";
                        if (userType == UserType.TEACHER){
                            rs = LoginActivity.db.getTkbGv(user,date);
                        }else {
                            rs = LoginActivity.db.getTkbHs(user,date);
                        }
                    } catch (ParseException e) {
                        Log.e(TAG,"error : "+e.getMessage());
                    }
                }else if (rs.contains("Lịch hoạt động")){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = format.parse(rs.replace("Lịch hoạt động",""));
                        if (userType == UserType.TEACHER){
                            rs = LoginActivity.db.getLHDGV(user,date);
                        }else {
                            rs = LoginActivity.db.getTkbHs(user,date);
                        }
                    } catch (ParseException e) {
                        Log.e(TAG,"error : "+e.getMessage());
                    }
                }else if (rs.contains("Chỉ đường tới khu")){
                    String place = rs.replace("Chỉ đường tới khu","");
                    if (!place.isEmpty()){
                        destination = place;
                    }
                    if (position.equals("")){

                        int rd = new Random().nextInt();
                        if (rd%3 == 0){
                            rs = "Cho tôi biết vị trí xuất phát của bạn?";
                        }else if (rd%3==1){
                            rs = "Bạn đang ở đâu đó?";
                        }else {
                            rs = "Bạn đi từ đâu?";
                        }
                    }else if (place.equals("")){
                        int rd = new Random().nextInt();
                        if (rd%3 == 0){
                            rs = "Bạn muốn đi tới đâu?";
                        }else if (rd%3==1){
                            rs = "Bạn cần tìm đường tới đâu?";
                        }else {
                            rs = "Bạn muốn đi tới chỗ nào nhỉ?";
                        }
                    }else {
                        rs = LoginActivity.db.getHDD(position,place);
                    }
                }else if(rs.contains("Vị trí hiện tại")){
                    String place = rs.replace("Vị trí hiện tại","");
                    if (!place.isEmpty()){
                        position = place;
                    }
                    if (destination.equals("")){

                        int rd = new Random().nextInt();
                        if (rd%3 == 0){
                            rs = "Bạn muốn đi tới đâu?";
                        }else if (rd%3==1){
                            rs = "Bạn cần tìm đường tới đâu?";
                        }else {
                            rs = "Bạn muốn đi tới chỗ nào nhỉ?";
                        }
                    }else if (place.equals("")){
                        int rd = new Random().nextInt();
                        if (rd%3 == 0){
                            rs = "Cho tôi biết vị trí xuất phát của bạn?";
                        }else if (rd%3==1){
                            rs = "Bạn đang ở đâu đó?";
                        }else {
                            rs = "Bạn đi từ đâu?";
                        }
                    }else {
                        rs = LoginActivity.db.getHDD(position,destination);
                    }
                }
                speak(jeyt,rs);
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
        String result = "";
        do {
            result = Maikadata.dialogFlowQuery("goassistant-36628","sessionID",question,"vi-VN", LoginActivity.db.getToken());
            if (result.isEmpty()){
                if (LoginActivity.db.updateToken()){
                    Log.d(TAG,"update ok");
                }
            }
        }while (result.isEmpty());
        return result;
    }




}

