package hung.kv.maikaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class TeacherActivity extends MaikaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacheractivity);

        initAssistant("Hùng");
    }
}