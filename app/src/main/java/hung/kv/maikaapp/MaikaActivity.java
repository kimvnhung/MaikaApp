package hung.kv.maikaapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import hung.kv.maikaapp.voicehandle.MaikaAssistant;

public class MaikaActivity extends AppCompatActivity {
    protected MaikaAssistant assistant = null;

    protected void initAssistant(String user){
        assistant = new MaikaAssistant(this,user);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (assistant != null){
//            assistant.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (assistant != null){
            assistant.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (assistant != null){
            assistant.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (assistant != null){
//            assistant.start();
        }
    }
}
