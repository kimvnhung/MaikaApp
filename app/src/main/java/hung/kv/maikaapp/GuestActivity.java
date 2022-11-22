package hung.kv.maikaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hung.kv.maikaapp.voicehandle.UserType;

public class GuestActivity extends MaikaActivity {

    Button hdddBtn,meetingBtn;
    LinearLayout mainLayout,hdddLayout,meetingLayout;
    Spinner startPlace,endPlace,cbSpinner;

    TextView descriptionTv,thongtinTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        initAssistant("Bạn", UserType.GUEST,null);

        initView();
    }

    private void initView() {
        hdddBtn = findViewById(R.id.hd_di_chuyen_btn);
        hdddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.GONE);
                hdddLayout.setVisibility(View.VISIBLE);
                meetingLayout.setVisibility(View.GONE);
            }
        });
        meetingBtn = findViewById(R.id.gap_mat_btn);
        meetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.GONE);
                meetingLayout.setVisibility(View.VISIBLE);
                hdddLayout.setVisibility(View.GONE);
            }
        });
        mainLayout = findViewById(R.id.main_layout);
        hdddLayout = findViewById(R.id.hd_duong_di_layout);
        meetingLayout = findViewById(R.id.gap_mat_layout);

        startPlace = findViewById(R.id.start_place);
        endPlace = findViewById(R.id.end_place);
        cbSpinner = findViewById(R.id.ds_can_bo);

        LoadCanBo();
        LoadLocation();

        descriptionTv = findViewById(R.id.desciption_tv);
        thongtinTv = findViewById(R.id.thong_tin_tv);
    }

    private void LoadLocation(){
        List<String> categories = LoginActivity.db.places;

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        startPlace.setAdapter(dataAdapter);
        startPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onSelectedPlaceChanged(categories.get(i), categories.get(endPlace.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        startPlace.setSelection(0);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        endPlace.setAdapter(dataAdapter);
        endPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onSelectedPlaceChanged(categories.get(startPlace.getSelectedItemPosition()),categories.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        endPlace.setSelection(0);
    }

    private void LoadCanBo(){
        List<String> categories = LoginActivity.db.getTeacherNames();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        cbSpinner.setAdapter(dataAdapter);
        cbSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onCBSelected(categories.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cbSpinner.setSelection(0);
    }

    private void onSelectedPlaceChanged(String start,String end){
        descriptionTv.setText(LoginActivity.db.getHDD(start,end));
    }

    private void onCBSelected(String cb){
        thongtinTv.setText(LoginActivity.db.getLCT(cb));
    }
    @Override
    public void onBackPressed() {
        if (mainLayout.getVisibility() == View.VISIBLE){
            finish();
        }else {
            mainLayout.setVisibility(View.VISIBLE);
            hdddLayout.setVisibility(View.GONE);
            meetingLayout.setVisibility(View.GONE);
        }
    }
}