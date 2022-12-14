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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hung.kv.maikaapp.voicehandle.MaikaAssistant;
import hung.kv.maikaapp.voicehandle.UserType;

public class GuestActivity extends MaikaActivity implements MaikaAssistant.AssistanceControlListenner {

    Button hdddBtn,meetingBtn;
    LinearLayout mainLayout,hdddLayout,meetingLayout;
    Spinner startPlace,endPlace,cbSpinner;

    TextView descriptionTv,thongtinTv;
    ImageView startPoint,destinationPoint;

    int offsetTop = 0,offsetLeft = 0;
    ImageView chatBotIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        initAssistant("Bạn", UserType.GUEST,this);

        initView();
        offsetTop = (int) Utils.dp2px(this,40);
        offsetLeft = (int) Utils.dp2px(this,0);

        String destinationP = getIntent().getStringExtra("destination");
        if (!destinationP.isEmpty()){
            hdddBtn.callOnClick();
            int index = LoginActivity.db.getPlaceIndex(destinationP);
            if (index != -1){
                endPlace.setSelection(index);
                assistant.fakeResult("Chỉ đường tới khu "+destinationP);
            }
        }
//        initBotView();
    }

//    private void initBotView() {
//        chatBotIcon = findViewById(R.id.chat_bot_icon);
//    }

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

        startPoint = findViewById(R.id.start_point);
        destinationPoint = findViewById(R.id.destination_point);
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

        if (start.toLowerCase().contains("cổng trường")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) startPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_cong_truong_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_cong_truong_top)+offsetTop,params.rightMargin,params.bottomMargin);
            startPoint.setLayoutParams(params);
        }else if(start.toLowerCase().contains("đa năng")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) startPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_da_nang_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_da_nang_top)+offsetTop,params.rightMargin,params.bottomMargin);
            startPoint.setLayoutParams(params);
        }else if(start.toLowerCase().contains("2t p8 1")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) startPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p8_1_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p8_1_top)+offsetTop,params.rightMargin,params.bottomMargin);
            startPoint.setLayoutParams(params);
        }else if (start.toLowerCase().contains("2t p8 2")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) startPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p8_2_let)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p8_2_top)+offsetTop,params.rightMargin,params.bottomMargin);
            startPoint.setLayoutParams(params);
        }else if (start.toLowerCase().contains("hiệu bộ")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) startPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_hieu_bo_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_hieu_bo_top)+offsetTop,params.rightMargin,params.bottomMargin);
            startPoint.setLayoutParams(params);
        }else if(start.toLowerCase().contains("2t p6")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) startPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p6_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p6_top)+offsetTop,params.rightMargin,params.bottomMargin);
            startPoint.setLayoutParams(params);
        }else if (start.toLowerCase().contains("2t p4")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) startPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p4_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p4_top)+offsetTop,params.rightMargin,params.bottomMargin);
            startPoint.setLayoutParams(params);
        }

        if (end.toLowerCase().contains("cổng trường")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) destinationPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_cong_truong_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_cong_truong_top)+offsetTop,params.rightMargin,params.bottomMargin);
            destinationPoint.setLayoutParams(params);
        }else if(end.toLowerCase().contains("đa năng")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) destinationPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_da_nang_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_da_nang_top)+offsetTop,params.rightMargin,params.bottomMargin);
            destinationPoint.setLayoutParams(params);
        }else if(end.toLowerCase().contains("2t p8 1")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) destinationPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p8_1_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p8_1_top)+offsetTop,params.rightMargin,params.bottomMargin);
            destinationPoint.setLayoutParams(params);
        }else if (end.toLowerCase().contains("2t p8 2")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) destinationPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p8_2_let)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p8_2_top)+offsetTop,params.rightMargin,params.bottomMargin);
            destinationPoint.setLayoutParams(params);
        }else if (end.toLowerCase().contains("hiệu bộ")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) destinationPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_hieu_bo_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_hieu_bo_top)+offsetTop,params.rightMargin,params.bottomMargin);
            destinationPoint.setLayoutParams(params);
        }else if(end.toLowerCase().contains("2t p6")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) destinationPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p6_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p6_top)+offsetTop,params.rightMargin,params.bottomMargin);
            destinationPoint.setLayoutParams(params);
        }else if (end.toLowerCase().contains("2t p4")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) destinationPoint.getLayoutParams();
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p4_left)+offsetLeft,getResources().getDimensionPixelSize(R.dimen.location_nha_lop_hoc_2t_p4_top)+offsetTop,params.rightMargin,params.bottomMargin);
            destinationPoint.setLayoutParams(params);
        }

        startPoint.setVisibility(View.VISIBLE);
        destinationPoint.setVisibility(View.VISIBLE);
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

    @Override
    public void onLoggedIn(String username, String password) {

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onDetectingKeyword() {

    }

    @Override
    public void onDetectedKeyword() {

    }

    @Override
    public void onListening() {

    }

    @Override
    public void onSpeaking() {

    }

    @Override
    public void onOpenLoginLayout() {

    }

    @Override
    public void onPostSuggestion(ArrayList<String> sugestions) {

    }

    @Override
    public void onCallGuide(String destination) {
        int index = LoginActivity.db.getPlaceIndex(destination);
        if (index != -1){
            endPlace.setSelection(index);
        }
    }

    @Override
    public void onDetectedPositon(String position) {
        int index = LoginActivity.db.getPlaceIndex(position);
        if (index != -1){
            startPlace.setSelection(index);
        }
    }
}