package hung.kv.maikaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getName();

    Button loginBtn;
    EditText usernameEdt,passwordEdt;
    TextView loginAsGuest;

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
        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTeacherActivity();
            }
        });

        loginAsGuest = findViewById(R.id.login_as_guest_tv);
        loginAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchSound(currentMode);
                currentMode = !currentMode;
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

    private void startTeacherActivity(){
        Intent startIntent = new Intent(this,TeacherActivity.class);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startIntent);
    }

    private void startStudentActivity(){

    }

    private void startGuestActivity(){

    }
}