package hung.kv.maikaapp;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import hung.kv.maikaapp.voicehandle.MaikaAssistant;
import hung.kv.maikaapp.voicehandle.UserType;

public class MaikaActivity extends AppCompatActivity {
    protected MaikaAssistant assistant = null;

    protected void initAssistant(String user, UserType type, MaikaAssistant.AssistanceControlListenner listenner){
        Log.d("","initAssistant");
        assistant = new MaikaAssistant(this,user,type, listenner);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (assistant != null){
//            assistant.finish();

        }
    }
}
