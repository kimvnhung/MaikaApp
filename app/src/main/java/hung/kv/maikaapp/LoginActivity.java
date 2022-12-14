package hung.kv.maikaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hung.kv.maikaapp.database.DataManager;
import hung.kv.maikaapp.database.SchoolPerson;
import hung.kv.maikaapp.database.Student;
import hung.kv.maikaapp.database.Teacher;
import hung.kv.maikaapp.views.SuggestionAdapter;
import hung.kv.maikaapp.voicehandle.MaikaAssistant;
import hung.kv.maikaapp.voicehandle.UserType;

public class LoginActivity extends MaikaActivity implements DataManager.LoadingDataListenner, MaikaAssistant.AssistanceControlListenner {
    private final String TAG = LoginActivity.class.getName();

    Button loginBtn;
    EditText usernameEdt,passwordEdt;
    TextView loginAsGuest;
    TextView hintText;
    CheckBox savePass;
    boolean isSaveAcc = false;

    public static DataManager db = null;

    Handler handler = new Handler();

    private String[] PERMISSION_NAME = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
    };

    RelativeLayout botLayout;
    ListView botTutorial;
    ImageView chatBotIcon;
    boolean isBotLayoutExpanding = true;
    ArrayList<String> listsuggestion = new ArrayList<>();
    SuggestionAdapter dataAdapter;

    LinearLayout loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkPermission();

//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (!notificationManager.isNotificationPolicyAccessGranted()) {
//
//            Intent intent = new Intent(
//                    android.provider.Settings
//                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//
//            startActivity(intent);
//        }



        initView();
        initBotView();

        if (db == null){
            db = new DataManager(this,this);
        }
    }

    private void initBotView() {
        botLayout = findViewById(R.id.include_layout);
        botLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (isBotLayoutExpanding){
//                    isBotLayoutExpanding = false;
//                    botLayout.setLayoutResource(R.layout.tini_bot_layout);
//                    botLayout.inflate();
//                }else {
//                    isBotLayoutExpanding = true;
//                    botLayout.setLayoutResource(R.layout.expand_bot_layout);
//                    View inflated = botLayout.inflate();
//
//
//                }

                LoginActivity.this.onOpenLoginLayout();
            }
        });

        botTutorial = findViewById(R.id.bot_tutorial);
        chatBotIcon = findViewById(R.id.chat_bot_icon);

        listsuggestion.add("Hey, Maika");
        listsuggestion.add("Xin chào, Maika");

        // Creating adapter for spinner
        dataAdapter = new SuggestionAdapter(LoginActivity.this, listsuggestion);

        // attaching data adapter to spinner
        botTutorial.setAdapter(dataAdapter);
        botTutorial.setDivider(null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initAssistant("Bạn", UserType.GUEST,this);
    }

    // Function to check and request permission
    public void checkPermission()
    {
        // Checking if permission is not granted

        for (int i=0;i<PERMISSION_NAME.length;i++){
            if (ContextCompat.checkSelfPermission(LoginActivity.this, PERMISSION_NAME[i]) == PackageManager.PERMISSION_DENIED) {
                break;
            }
            if (i==PERMISSION_NAME.length-1){
                return;
            }
        }

        ActivityCompat.requestPermissions(LoginActivity.this, PERMISSION_NAME, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    boolean currentMode = false;
    private void initView() {

        loginLayout = findViewById(R.id.login_layout);

        usernameEdt = findViewById(R.id.username_edt);
        passwordEdt = findViewById(R.id.password_edt);
        hintText = findViewById(R.id.hint_text_login);
        savePass = findViewById(R.id.save_acc_cb);
        savePass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isSaveAcc = b;
            }
        });

        if (Utils.GetPreference(this,"isSaveAcc",false)){
            savePass.setChecked(true);
            isSaveAcc = true;
        }

        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEdt.getText().toString();
                if (username.isEmpty()){
                    hintText.setText("Tên tài khoản không được để trống!");
                    return;
                }

                String password = passwordEdt.getText().toString();

                SchoolPerson person = db.isValidAccount(username,password);

                if (person == null){
                    hintText.setText("Tài khoản hoặc mật khẩu không đúng!");
                }else if (person instanceof Student){
                    startStudentActivity((Student) person);
                }else {
                    startTeacherActivity((Teacher) person);
                }
            }
        });

        loginAsGuest = findViewById(R.id.login_as_guest_tv);
        loginAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGuestActivity("");
            }
        });

        if (isSaveAcc){
            String[] acc = Utils.GetLastAccount(this);
            usernameEdt.setText(acc[0]);
            passwordEdt.setText(acc[1]);
        }
    }

    private void switchSound(boolean isMute){
        Log.d(TAG,"isMute : "+isMute);
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (isMute){
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }

    private void startTeacherActivity(Teacher teacher){
        if (db != null && !db.isUpdating()){
            if (isSaveAcc){
                Utils.SaveLastAccount(this,teacher.getUsername(),teacher.getPassword());
            }else{
                Utils.SaveLastAccount(this,"","");
            }
            Intent startIntent = new Intent(this,TeacherActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.putExtra("username",teacher.getUsername());
            startIntent.putExtra("password",teacher.getPassword());
            startActivity(startIntent);
        }else {
            handler.postDelayed(()->startTeacherActivity(teacher),500);
        }
    }

    private void startStudentActivity(Student student){
        if (db != null && !db.isUpdating()){
            if (isSaveAcc){
                Utils.SaveLastAccount(this,student.getUsername(),student.getPassword());
            }else {
                Utils.SaveLastAccount(this,"","");
            }
            Intent startIntent = new Intent(this,StudentActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.putExtra("username",student.getUsername());
            startIntent.putExtra("password",student.getPassword());
            startActivity(startIntent);
        }else {
            handler.postDelayed(()->startStudentActivity(student),500);
        }
    }

    private void startGuestActivity(String destination){
        if (db != null && !db.isUpdating()){
            Intent startIntent = new Intent(this,GuestActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.putExtra("destination",destination);
            startActivity(startIntent);
        }else {
            handler.postDelayed(()->startGuestActivity(destination),200);
        }
    }

    @Override
    public void onDataLoadCompleted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loading_layout).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onLoggedIn(String username, String password) {
        SchoolPerson person = db.isValidAccount(username,password);

        if (person == null){
            return;
        }

       if (person instanceof Student){
            startStudentActivity((Student) person);
        }else {
            startTeacherActivity((Teacher) person);
        }
    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onDetectingKeyword() {
        chatBotIcon.setImageDrawable(getDrawable(R.drawable.chatbot));
        botTutorial.setVisibility(View.VISIBLE);
        botLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetectedKeyword() {
        chatBotIcon.setImageDrawable(getDrawable(R.drawable.chatbot));

        loginLayout.setVisibility(View.GONE);
        botLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListening() {
        chatBotIcon.setImageDrawable(getDrawable(R.drawable.listen));
        botTutorial.setVisibility(View.GONE);
    }

    @Override
    public void onSpeaking() {
        chatBotIcon.setImageDrawable(getDrawable(R.drawable.chatbot));
        botTutorial.setVisibility(View.GONE);
    }

    @Override
    public void onOpenLoginLayout() {
        loginLayout.setVisibility(View.VISIBLE);
        botLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPostSuggestion(ArrayList<String> sugestions) {
        this.listsuggestion.clear();
        this.listsuggestion.addAll(sugestions);
        dataAdapter.notifyDataSetChanged();
        botTutorial.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCallGuide(String destination) {
        startGuestActivity(destination);
    }

    @Override
    public void onDetectedPositon(String position) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.SavePreference(this,"isSaveAcc",isSaveAcc);
    }
}