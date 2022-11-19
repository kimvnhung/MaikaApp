package hung.kv.maikaapp;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hung.kv.maikaapp.database.DataManager;
import hung.kv.maikaapp.database.SchoolPerson;
import hung.kv.maikaapp.database.Student;
import hung.kv.maikaapp.database.Teacher;

public class LoginActivity extends AppCompatActivity implements DataManager.LoadingDataListenner {
    private final String TAG = LoginActivity.class.getName();

    Button loginBtn;
    EditText usernameEdt,passwordEdt;
    TextView loginAsGuest;
    TextView hintText;

    public static DataManager db = null;

    Handler handler = new Handler();

    private String[] PERMISSION_NAME = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        for (int i=0;i<PERMISSION_NAME.length;i++){
            checkPermission(PERMISSION_NAME[i],i);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (!notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }

        initView();

        if (db == null){
            db = new DataManager(this,this);
        }
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(LoginActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[] { permission }, requestCode);
        }
    }


    boolean currentMode = false;
    private void initView() {
        usernameEdt = findViewById(R.id.username_edt);
        passwordEdt = findViewById(R.id.password_edt);
        hintText = findViewById(R.id.hint_text_login);

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
                startGuestActivity();
            }
        });
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
            Intent startIntent = new Intent(this,StudentActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.putExtra("username",student.getUsername());
            startIntent.putExtra("password",student.getPassword());
            startActivity(startIntent);
        }else {
            handler.postDelayed(()->startStudentActivity(student),500);
        }
    }

    private void startGuestActivity(){
        if (db != null && !db.isUpdating()){
            Intent startIntent = new Intent(this,GuestActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }else {
            handler.postDelayed(()->startGuestActivity(),500);
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
}