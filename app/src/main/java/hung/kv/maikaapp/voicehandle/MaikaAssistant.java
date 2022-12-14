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

    String user = "bạn";
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
                    MaikaAssistant.this.speak(new HashMap<>(),"Đăng nhập thành công");
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
                            "Đăng nhập",
                            "Chỉ đường cho tôi tới ...",
                            "Cho tôi gặp thầy/cô/cán bộ ..."
                    })));
                }
                Map<String,String> jeyt = new HashMap<>();
                jeyt.put("@user",user);
                String rs = "";
                if (state == LoginState.LOGGED_IN){
                    if (result.equalsIgnoreCase("Đăng xuất")){
                        if (loggingListenner != null){
                            speak(jeyt,"Đăng xuất");
                            handler.postDelayed(() -> loggingListenner.onLoggedOut(),3000);
                        }else {
                            rs = "Đăng xuất không thành công!";
                        }
                    }else{
                        rs = getResponse(result);
                        Log.d(TAG,"rs "+rs);
                        if (rs.contains("Giờ")){
                            Date toDay = new Date();
                            rs = "Bây giờ là "+toDay.getHours()+" giờ "+toDay.getMinutes()+" phút";
                        }else if (result.toLowerCase().contains("kem")){
                            jeyt.put("kem","true");
                        }else if(rs.contains("khóa biểu")){
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date date = format.parse(rs.replace("Thời khóa biểu",""));
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
                        }else if (rs.contains("Trống")){
                            if (LoginActivity.db.getUserByName(user) != null){
                                if (((Teacher)LoginActivity.db.getUserByName(user)).getChucVu().equalsIgnoreCase("Hiệu trưởng")){
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
                                            rs = "Tôi không nghe rõ";
                                        }
                                    }
                                }else {
                                    rs = "Chỉ hiệu trưởng mới được xem lịch trống của cán bộ";
                                }
                            }else {
                                rs = "Chỉ hiệu trưởng mới được xem lịch trống của cán bộ";
                            }
                        }
                    }
                }else if (state == LoginState.USER_NAME_WATING){
                    Log.d(TAG,"Listenned name :"+result);
                    userTemplate = LoginActivity.db.getUserByName(result);
                    if (userTemplate == null){
                        rs = "Tôi không tìm thấy tên của bạn,vui lòng nói lại!";
                    }else {
                        rs = "Mật khẩu đăng nhập của bạn là gì?";
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
                            speak(jeyt,"Đăng nhập thành công!");
                            if (loggingListenner != null){
                                handler.postDelayed(() -> loggingListenner.onLoggedIn(userTemplate.getUsername(),userTemplate.getPassword()),3000);
                            }
                        }else{
                            resetLoginStateTimer.cancel();
                            resetLoginStateTimer.start();
                        }
                    }
                }else if (result.equalsIgnoreCase("Đăng nhập")){
//                    rs = "Tên bạn là gì?";
//                    state = LoginState.USER_NAME_WATING;
//                    resetLoginStateTimer.cancel();
//                    resetLoginStateTimer.start();
                    if (loggingListenner != null){
                        loggingListenner.onOpenLoginLayout();
                    }
                }else {
                    rs = getResponse(result);
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
                    }else if (rs.contains("Chỉ đường tới khu")){
                        String place = LoginActivity.db.getPlace(rs.replace("Chỉ đường tới khu",""));
                        if (!place.isEmpty()){
                            destination = place;
                            if (loggingListenner != null){
                                loggingListenner.onCallGuide(destination);
                            }
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
                        String place = LoginActivity.db.getPlace(rs.replace("Vị trí hiện tại",""));
                        if (!place.isEmpty()){
                            position = place;
                            if (loggingListenner != null){
                                loggingListenner.onDetectedPositon(position);
                            }
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

