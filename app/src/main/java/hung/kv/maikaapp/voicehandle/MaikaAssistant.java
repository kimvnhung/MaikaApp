package hung.kv.maikaapp.voicehandle;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import hung.kv.maikaapp.LoginActivity;
import hung.kv.maikaapp.database.DataManager;
import hung.kv.maikaapp.database.SchoolPerson;
import hung.kv.maikaapp.database.Teacher;
import maikadata.Maikadata;


public class MaikaAssistant implements AssistantLifeCycle, SpeechDetector.SpeechDetectListenner, TextToSpeech.OnUtteranceCompletedListener {
    private final String TAG = MaikaAssistant.class.getName();

    private final int tickCountDown = 1000;
    private final int detectKeywordInterval = 5000;
    private final int resetSessionInterval = 18000;
    private final int resetLoginState = 15000;
    private boolean isKeywordDetected = false;
    private boolean isSpeaking = false;

    TextToSpeech speaker = null;
    TextToSpeech speakerOther = null;
    SpeechDetector detector;
    private LoginState state = LoginState.LOGGED_OUT;
    private SchoolPerson userTemplate = null;
    private AssistanceControlListenner loggingListenner = null;


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
            if (loggingListenner != null){
                loggingListenner.onDetectingKeyword();
            }
            position = "";
            destination = "";
        }
    };

    CountDownTimer resetLoginStateTimer = new CountDownTimer(resetLoginState,tickCountDown) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
//            state = LoginState.LOGGED_OUT;
            userTemplate = null;
        }
    };

    String user = "b???n";
    UserType userType = UserType.GUEST;
    Handler handler = new Handler();

    String position = "";
    String destination = "";

    public MaikaAssistant(Context context,String user, UserType userType, AssistanceControlListenner loggingListenner){
        detector = new SpeechDetector(context,this);
        this.loggingListenner = loggingListenner;
        this.userType = userType;
        if (userType != UserType.GUEST){
            state= LoginState.LOGGED_IN;
            isKeywordDetected = true;
        }else {
            state = LoginState.LOGGED_OUT;
        }

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

        if (userType != UserType.GUEST){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MaikaAssistant.this.speak(new HashMap<>(),"????ng nh???p th??nh c??ng");
                    MaikaAssistant.this.start();
                }
            },500);
        }else {
            start();
        }

    }

    public void fakeResult(String result) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (detector != null){
                    detector.fakeResult(result);
                    isKeywordDetected = true;
                }
            }
        },100);
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
            handler.postDelayed(() -> start(),100);
        }else {
            detector.listen();
            detectKeywordTimer.start();
            if (loggingListenner != null && isKeywordDetected){
                loggingListenner.onListening();
            }
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
            if (loggingListenner != null){
                loggingListenner.onSpeaking();
            }
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
        String[] detects = new String[]{
                "maika","maica","mycar","maicah","micah"
        };
        for (int i=0;i<detects.length;i++){
            if (content.toLowerCase().replace(" ","").equalsIgnoreCase(detects[i])){
                return true;
            }
        }
        return false;
    }


    @Override
    public void onDetectedSpeech(String result) {
        if (!result.isEmpty()){
            Maikadata.pushLog(result);
            Log.d(TAG,"result "+result);
            if (!isKeywordDetected){
                isKeywordDetected = detectKeyword(result);
            }

            if (isKeywordDetected){
                if (loggingListenner != null){
                    loggingListenner.onDetectedKeyword();
                    loggingListenner.onPostSuggestion(new ArrayList<>(Arrays.asList(new String[]{
                            "????ng nh???p",
                            "Ch??? ???????ng cho t??i t???i ...",
                            "Cho t??i g???p th???y/c??/c??n b??? ..."
                    })));
                }
                Map<String,String> jeyt = new HashMap<>();
                jeyt.put("@user",user);
                String rs = "";
                if (state == LoginState.LOGGED_IN){
                    if (result.equalsIgnoreCase("????ng xu???t")){
                        if (loggingListenner != null){
                            speak(jeyt,"????ng xu???t");
                            handler.postDelayed(() -> loggingListenner.onLoggedOut(),3000);
                        }else {
                            rs = "????ng xu???t kh??ng th??nh c??ng!";
                        }
                    }else{
                        rs = getResponse(result);
                        Log.d(TAG,"rs "+rs);
                        if (rs.contains("Gi???")){
                            Date toDay = new Date();
                            rs = "B??y gi??? l?? "+toDay.getHours()+" gi??? "+toDay.getMinutes()+" ph??t";
                        }else if (result.toLowerCase().contains("kem")){
                            jeyt.put("kem","true");
                        }else if(rs.contains("kh??a bi???u")){
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date date = format.parse(rs.replace("Th???i kh??a bi???u",""));
                                if (userType == UserType.TEACHER){
                                    rs = LoginActivity.db.getTkbGv(user,date);
                                }else {
                                    rs = LoginActivity.db.getTkbHs(user,date);
                                }
                            } catch (ParseException e) {
                                Log.e(TAG,"error : "+e.getMessage());
                            }
                        }else if (rs.contains("L???ch ho???t ?????ng")){
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date date = format.parse(rs.replace("L???ch ho???t ?????ng",""));
                                if (userType == UserType.TEACHER){
                                    rs = LoginActivity.db.getLHDGV(user,date);
                                }else {
                                    rs = LoginActivity.db.getTkbHs(user,date);
                                }
                            } catch (ParseException e) {
                                Log.e(TAG,"error : "+e.getMessage());
                            }
                        }else if (rs.contains("Tr???ng")){
                            if (LoginActivity.db.getUserByName(user) != null){
                                if (((Teacher)LoginActivity.db.getUserByName(user)).getChucVu().equalsIgnoreCase("Hi???u tr?????ng")){
                                    String[] splits = rs.split(" ");
                                    if (splits.length >=4){
                                        int date = 0;
                                        int period = 0;
                                        if (!splits[2].equals("@date-time")){
                                            try{
                                                Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(splits[2]);
                                                date = DataManager.GetDayInWeek(date1);
                                            }catch (Exception e){
                                                Log.e(TAG, "onDetectedSpeech: "+e.getMessage());
                                            }
                                        }
                                        if (!splits[3].equals("@period")){
                                            try {
                                                period = Integer.parseInt(splits[4]);
                                            }catch (Exception e){
                                                Log.e(TAG, "onDetectedSpeech: "+e.getMessage());
                                            }
                                        }

                                        if (date != 0 || period != 0){
                                            rs = LoginActivity.db.getMissingPeriodTeacher(date,period);
                                        }else {
                                            rs = "T??i kh??ng nghe r??";
                                        }
                                    }
                                }else {
                                    rs = "Ch??? hi???u tr?????ng m???i ???????c xem l???ch tr???ng c???a c??n b???";
                                }
                            }else {
                                rs = "Ch??? hi???u tr?????ng m???i ???????c xem l???ch tr???ng c???a c??n b???";
                            }
                        }
                    }
                }else if (state == LoginState.USER_NAME_WATING){
                    Log.d(TAG,"Listenned name :"+result);
                    userTemplate = LoginActivity.db.getUserByName(result);
                    if (userTemplate == null){
                        rs = "T??i kh??ng t??m th???y t??n c???a b???n,vui l??ng n??i l???i!";
                    }else {
                        rs = "M???t kh???u ????ng nh???p c???a b???n l?? g???";
                        state = LoginState.PASSWORD_WAITING;
                    }
                    resetLoginStateTimer.cancel();
                    resetLoginStateTimer.start();
                }else if (state == LoginState.PASSWORD_WAITING){
                    Log.d(TAG,"Listenned class :"+result);
                    if (userTemplate != null){
                        if (result.replace(" ","").equalsIgnoreCase(userTemplate.getPassword())){
                            state = LoginState.LOGGED_IN;
                            user = userTemplate.getName();
                            speak(jeyt,"????ng nh???p th??nh c??ng!");
                            if (loggingListenner != null){
                                handler.postDelayed(() -> loggingListenner.onLoggedIn(userTemplate.getUsername(),userTemplate.getPassword()),3000);
                            }
                        }else{
                            resetLoginStateTimer.cancel();
                            resetLoginStateTimer.start();
                        }
                    }
                }else if (result.equalsIgnoreCase("????ng nh???p")){
//                    rs = "T??n b???n l?? g???";
//                    state = LoginState.USER_NAME_WATING;
//                    resetLoginStateTimer.cancel();
//                    resetLoginStateTimer.start();
                    if (loggingListenner != null){
                        loggingListenner.onOpenLoginLayout();
                    }
                }else {
                    rs = getResponse(result);
                    Log.d(TAG,"rs "+rs);
                    if (rs.contains("G???p m???t")){
                        String name = rs.substring(13);
                        if (!name.equals("T??n")){
                            String lic = LoginActivity.db.getLCT(name);
                            rs = lic;
                        }else {
                            if (new Random().nextInt()%2==0){
                                rs = "B???n mu???n g???p ai c???";
                            }else {
                                rs = "B???n mu???n g???p ai? T??i kh??ng nghe r??!";
                            }
                        }
                    }else if (rs.contains("Ch??? ???????ng t???i khu")){
                        String place = LoginActivity.db.getPlace(rs.replace("Ch??? ???????ng t???i khu",""));
                        if (!place.isEmpty()){
                            destination = place;
                            if (loggingListenner != null){
                                loggingListenner.onCallGuide(destination);
                            }
                        }
                        if (position.equals("")){

                            int rd = new Random().nextInt();
                            if (rd%3 == 0){
                                rs = "Cho t??i bi???t v??? tr?? xu???t ph??t c???a b???n?";
                            }else if (rd%3==1){
                                rs = "B???n ??ang ??? ????u ?????";
                            }else {
                                rs = "B???n ??i t??? ????u?";
                            }
                        }else if (place.equals("")){
                            int rd = new Random().nextInt();
                            if (rd%3 == 0){
                                rs = "B???n mu???n ??i t???i ????u?";
                            }else if (rd%3==1){
                                rs = "B???n c???n t??m ???????ng t???i ????u?";
                            }else {
                                rs = "B???n mu???n ??i t???i ch??? n??o nh????";
                            }
                        }else {
                            rs = LoginActivity.db.getHDD(position,place);
                        }
                    }else if(rs.contains("V??? tr?? hi???n t???i")){
                        String place = LoginActivity.db.getPlace(rs.replace("V??? tr?? hi???n t???i",""));
                        if (!place.isEmpty()){
                            position = place;
                            if (loggingListenner != null){
                                loggingListenner.onDetectedPositon(position);
                            }
                        }
                        if (destination.equals("")){
                            int rd = new Random().nextInt();
                            if (rd%3 == 0){
                                rs = "B???n mu???n ??i t???i ????u?";
                            }else if (rd%3==1){
                                rs = "B???n c???n t??m ???????ng t???i ????u?";
                            }else {
                                rs = "B???n mu???n ??i t???i ch??? n??o nh????";
                            }
                        }else if (place.equals("")){
                            int rd = new Random().nextInt();
                            if (rd%3 == 0){
                                rs = "Cho t??i bi???t v??? tr?? xu???t ph??t c???a b???n?";
                            }else if (rd%3==1){
                                rs = "B???n ??ang ??? ????u ?????";
                            }else {
                                rs = "B???n ??i t??? ????u?";
                            }
                        }else {
                            rs = LoginActivity.db.getHDD(position,destination);
                        }
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

    public interface AssistanceControlListenner{
        void onLoggedIn(String username, String password);
        void onLoggedOut();
        void onDetectingKeyword();
        void onDetectedKeyword();
        void onListening();
        void onSpeaking();
        void onOpenLoginLayout();
        void onPostSuggestion(ArrayList<String> sugestions);
        void onCallGuide(String destination);
        void onDetectedPositon(String position);
    }

}

