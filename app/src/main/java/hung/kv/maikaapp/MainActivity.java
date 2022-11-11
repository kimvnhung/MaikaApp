package hung.kv.maikaapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import hung.kv.maikaapp.database.DataManager;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getName();
    private String[] PERMISSION_NAME = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    Button assistantQueryBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        for (int i=0;i<PERMISSION_NAME.length;i++){
            checkPermission(PERMISSION_NAME[i],i);
        }

        assistantQueryBtn = findViewById(R.id.assistantQueryButton);
        assistantQueryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void init(){
        try {
            DataManager.instance().downloadCSV(this);
        }catch (Exception e) {
            Log.d(TAG,"err : "+e.getMessage());
        }
    }

}