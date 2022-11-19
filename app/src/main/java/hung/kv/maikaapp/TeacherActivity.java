package hung.kv.maikaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import hung.kv.maikaapp.database.DataManager;
import hung.kv.maikaapp.database.Task;
import hung.kv.maikaapp.database.Teacher;
import hung.kv.maikaapp.voicehandle.UserType;

public class TeacherActivity extends MaikaActivity {
    private static final String TAG = TeacherActivity.class.getName();
    Button tkbBtn,lctBtn,tthsBtn;
    LinearLayout mainLayout,tkbLayout,lctLayout;
    TableLayout tkbTable,lctTable;
    TextView tenCB,chucVu;

    Teacher teacher = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacheractivity);

        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        teacher = (Teacher) LoginActivity.db.isValidAccount(username,password);

        initAssistant(teacher.getName(), UserType.TEACHER);

        initView();
    }

    private void initView() {
        findViewById(R.id.back_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeacherActivity.this.onBackPressed();
            }
        });
        findViewById(R.id.back_tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeacherActivity.this.onBackPressed();
            }
        });
        mainLayout = findViewById(R.id.main_layout);
        tkbLayout = findViewById(R.id.tkb_gv_layout);
        lctLayout = findViewById(R.id.lct_gv_layout);

        tkbBtn = findViewById(R.id.tkb_gv_btn);
        tkbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initTKB();
                mainLayout.setVisibility(View.GONE);
                tkbLayout.setVisibility(View.VISIBLE);
                lctLayout.setVisibility(View.GONE);
            }
        });
        lctBtn = findViewById(R.id.lich_cong_tac_btn);
        lctBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initLCT();
                mainLayout.setVisibility(View.GONE);
                tkbLayout.setVisibility(View.GONE);
                lctLayout.setVisibility(View.VISIBLE);
            }
        });
        tthsBtn = findViewById(R.id.hs_tt_btn);
        tenCB = findViewById(R.id.ten_can_bo_tv);
        tenCB.setText(teacher.getName());
        chucVu = findViewById(R.id.chuc_vu_tv);
        chucVu.setText(teacher.getChucVu());

        tkbTable = findViewById(R.id.tkb_gv_table);
        lctTable = findViewById(R.id.lct_gv_table);

    }

    private void initTKB() {
        tkbTable.removeAllViews();

//        TableRow newRow = new TableRow(this);
//        newRow.setBackgroundColor(getColor(R.color.purple_200));
//        TextView view = new TextView(this);
//        view.setText("hello");
//        view.setBackgroundColor(getColor(R.color.teal_200));
//        view.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
//        view.setTextColor(getColor(R.color.black));
//        view.setGravity(Gravity.CENTER);
//        newRow.addView(view,new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f));
//
//        tkbTable.addView(newRow,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        int padding = (int) getResources().getDimension(R.dimen.small_login_padding);
        int margrin = (int) Utils.dp2px(this,1);
        Log.d(TAG,"padding "+padding);
        for(int i=2;i<=7;i++){
            for(int j=1;j<=5;j++){
                TableRow newRow = new TableRow(this);
                if (j==3){
                    TextView thu = new TextView(this);
                    thu.setText(i+"");
                    thu.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv3));
                    thu.setTextColor(getColor(R.color.black));
                    thu.setGravity(Gravity.CENTER);
                    thu.setPadding(padding,0,padding,0);
                    newRow.addView(thu,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,1f));
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) thu.getLayoutParams();
                    p.setMargins(margrin,margrin,margrin,margrin);
                    thu.requestLayout();
                }else {
                    TextView thu = new TextView(this);
                    thu.setText("");
                    thu.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv3));
                    thu.setTextColor(getResources().getColor(R.color.black));
                    thu.setGravity(Gravity.CENTER);
                    thu.setPadding(padding,0,padding,0);
                    newRow.addView(thu,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,1f));
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) thu.getLayoutParams();
                    p.setMargins(margrin,margrin,margrin,margrin);
                    thu.requestLayout();
                }

                TextView tiet = new TextView(this);
                tiet.setText(j+"");
                tiet.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                tiet.setTextColor(getResources().getColor(R.color.black));
                tiet.setGravity(Gravity.CENTER);
                tiet.setPadding(padding,0,padding,0);
                newRow.addView(tiet,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,1f));
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tiet.getLayoutParams();

                p.setMargins(margrin,margrin,margrin,margrin);
                tiet.requestLayout();

                for (int k=0;k<teacher.getTasks().size();k++){
                    Task task = teacher.getTasks().get(k);
                    if (task.getPlace().length() != 4 && !task.getPlace().isEmpty()){
                        if (k == teacher.getTasks().size()-1){
                            TextView mon = new TextView(this);
                            mon.setText("");
                            mon.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                            mon.setTextColor(getResources().getColor(R.color.black));
                            mon.setGravity(Gravity.CENTER);
                            mon.setPadding(padding,0,padding,0);
                            newRow.addView(mon,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,6f));
                            ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams) mon.getLayoutParams();
                            p2.setMargins(margrin,margrin,margrin,margrin);
                            mon.requestLayout();
                            Log.d(TAG,"add empty");

                            TextView lop = new TextView(this);
                            lop.setText("");
                            lop.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                            lop.setTextColor(getResources().getColor(R.color.black));
                            lop.setGravity(Gravity.CENTER);
                            lop.setPadding(padding,0,padding,0);
                            newRow.addView(lop,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,3f));
                            ViewGroup.MarginLayoutParams p3 = (ViewGroup.MarginLayoutParams) lop.getLayoutParams();
                            p3.setMargins(margrin,margrin,margrin,margrin);
                            lop.requestLayout();
                        }
                        continue;
                    }
                    Log.d(TAG,"Period start "+task.getPeriodStart()+" j "+j);
                    if (DataManager.GetDayInWeek(task.getFrom()) == i && task.getPeriodStart() == j){
                        TextView mon = new TextView(this);
                        mon.setText(task.getName());
                        mon.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                        mon.setTextColor(getResources().getColor(R.color.black));
                        mon.setGravity(Gravity.CENTER);
                        mon.setPadding(padding,0,padding,0);
                        newRow.addView(mon,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,6f));
                        ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams) mon.getLayoutParams();
                        p2.setMargins(margrin,margrin,margrin,margrin);
                        mon.requestLayout();
                        Log.d(TAG,"add subject");

                        TextView lop = new TextView(this);
                        lop.setText(task.getPlace());
                        lop.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                        lop.setTextColor(getResources().getColor(R.color.black));
                        lop.setGravity(Gravity.CENTER);
                        lop.setPadding(padding,0,padding,0);
                        newRow.addView(lop,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,3f));
                        ViewGroup.MarginLayoutParams p3 = (ViewGroup.MarginLayoutParams) lop.getLayoutParams();
                        p3.setMargins(margrin,margrin,margrin,margrin);
                        lop.requestLayout();
                        break;
                    }

                    if (k == teacher.getTasks().size()-1){
                        TextView mon = new TextView(this);
                        mon.setText("");
                        mon.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                        mon.setTextColor(getResources().getColor(R.color.black));
                        mon.setGravity(Gravity.CENTER);
                        mon.setPadding(padding,0,padding,0);
                        newRow.addView(mon,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,6f));
                        ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams) mon.getLayoutParams();
                        p2.setMargins(margrin,margrin,margrin,margrin);
                        mon.requestLayout();
                        Log.d(TAG,"add empty");

                        TextView lop = new TextView(this);
                        lop.setText("");
                        lop.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                        lop.setTextColor(getResources().getColor(R.color.black));
                        lop.setGravity(Gravity.CENTER);
                        lop.setPadding(padding,0,padding,0);
                        newRow.addView(lop,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,3f));
                        ViewGroup.MarginLayoutParams p3 = (ViewGroup.MarginLayoutParams) lop.getLayoutParams();
                        p3.setMargins(margrin,margrin,margrin,margrin);
                        lop.requestLayout();
                    }
                }
                tkbTable.addView(newRow,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0,1f));
            }
            Log.d(TAG,"add row to table");
        }
    }

    private void initLCT() {
        lctTable.removeAllViews();

//        TableRow newRow = new TableRow(this);
//        newRow.setBackgroundColor(getColor(R.color.purple_200));
//        TextView view = new TextView(this);
//        view.setText("hello");
//        view.setBackgroundColor(getColor(R.color.teal_200));
//        view.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
//        view.setTextColor(getColor(R.color.black));
//        view.setGravity(Gravity.CENTER);
//        newRow.addView(view,new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f));
//
//        tkbTable.addView(newRow,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        int padding = (int) getResources().getDimension(R.dimen.small_login_padding);
        int margrin = (int) Utils.dp2px(this,1);
        Log.d(TAG,"padding "+padding);
        for(int i=2;i<=7;i++){
            for(int j=0;j<=1;j++){
                TableRow newRow = new TableRow(this);
                if (j==0){
                    TextView thu = new TextView(this);
                    thu.setText(i+"");
                    thu.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv3));
                    thu.setTextColor(getColor(R.color.black));
                    thu.setGravity(Gravity.CENTER);
                    thu.setPadding(padding,0,padding,0);
                    newRow.addView(thu,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,1f));
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) thu.getLayoutParams();
                    p.setMargins(margrin,margrin,margrin,margrin);
                    thu.requestLayout();
                }else {
                    TextView thu = new TextView(this);
                    thu.setText("");
                    thu.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv3));
                    thu.setTextColor(getResources().getColor(R.color.black));
                    thu.setGravity(Gravity.CENTER);
                    thu.setPadding(padding,0,padding,0);
                    newRow.addView(thu,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,1f));
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) thu.getLayoutParams();
                    p.setMargins(margrin,margrin,margrin,margrin);
                    thu.requestLayout();
                }

                for (int k=0;k<teacher.getTasks().size();k++){
                    Task task = teacher.getTasks().get(k);
                    Log.d(TAG,"place "+task.getPlace()+" size "+task.getPlace().length());
                    if (task.getPlace().length()==4 || task.getPlace().isEmpty()){
                        if (k == teacher.getTasks().size()-1){
                            TextView tiet = new TextView(this);
                            tiet.setText(j==0?"Sáng":"Chiều");
                            tiet.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                            tiet.setTextColor(getResources().getColor(R.color.black));
                            tiet.setGravity(Gravity.CENTER);
                            tiet.setPadding(padding,0,padding,0);
                            newRow.addView(tiet,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,1f));
                            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tiet.getLayoutParams();

                            p.setMargins(margrin,margrin,margrin,margrin);
                            tiet.requestLayout();

                            TextView mon = new TextView(this);
                            mon.setText("");
                            mon.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                            mon.setTextColor(getResources().getColor(R.color.black));
                            mon.setGravity(Gravity.CENTER);
                            mon.setPadding(padding,0,padding,0);
                            newRow.addView(mon,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,4f));
                            ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams) mon.getLayoutParams();
                            p2.setMargins(margrin,margrin,margrin,margrin);
                            mon.requestLayout();
                            Log.d(TAG,"Add empty 305");

                            TextView lop = new TextView(this);
                            lop.setText("");
                            lop.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                            lop.setTextColor(getResources().getColor(R.color.black));
                            lop.setGravity(Gravity.CENTER);
                            lop.setPadding(padding,0,padding,0);
                            newRow.addView(lop,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,2f));
                            ViewGroup.MarginLayoutParams p3 = (ViewGroup.MarginLayoutParams) lop.getLayoutParams();
                            p3.setMargins(margrin,margrin,margrin,margrin);
                            lop.requestLayout();
                        }
                        continue;
                    }
                    Log.d(TAG,"Period start "+task.getPeriodStart()+" j "+j);
                    if (DataManager.GetDayInWeek(task.getFrom()) == i ){
                        if ((j==0 && task.getFrom().getHours() < 12)
                                || (j==1 && task.getFrom().getHours() >= 12)){
                            TextView tiet = new TextView(this);
                            tiet.setText(task.getFrom().getHours()+"h"+(task.getFrom().getMinutes()!=0?(task.getFrom().getMinutes()+"'"):""));
                            tiet.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                            tiet.setTextColor(getResources().getColor(R.color.black));
                            tiet.setGravity(Gravity.CENTER);
                            tiet.setPadding(padding,0,padding,0);
                            newRow.addView(tiet,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,1f));
                            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tiet.getLayoutParams();

                            p.setMargins(margrin,margrin,margrin,margrin);
                            tiet.requestLayout();

                            TextView mon = new TextView(this);
                            mon.setText(task.getName());
                            mon.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                            mon.setTextColor(getResources().getColor(R.color.black));
                            mon.setGravity(Gravity.CENTER);
                            mon.setPadding(padding,0,padding,0);
                            newRow.addView(mon,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,4f));
                            ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams) mon.getLayoutParams();
                            p2.setMargins(margrin,margrin,margrin,margrin);
                            mon.requestLayout();
                            Log.d(TAG,"add subject");

                            TextView lop = new TextView(this);
                            lop.setText(task.getPlace());
                            lop.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                            lop.setTextColor(getResources().getColor(R.color.black));
                            lop.setGravity(Gravity.CENTER);
                            lop.setPadding(padding,0,padding,0);
                            newRow.addView(lop,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,2f));
                            ViewGroup.MarginLayoutParams p3 = (ViewGroup.MarginLayoutParams) lop.getLayoutParams();
                            p3.setMargins(margrin,margrin,margrin,margrin);
                            lop.requestLayout();
                            break;
                        }
                    }

                    if (k == teacher.getTasks().size()-1){
                        TextView tiet = new TextView(this);
                        tiet.setText(j==0?"Sáng":"Chiều");
                        tiet.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                        tiet.setTextColor(getResources().getColor(R.color.black));
                        tiet.setGravity(Gravity.CENTER);
                        tiet.setPadding(padding,0,padding,0);
                        newRow.addView(tiet,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,1f));
                        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tiet.getLayoutParams();

                        p.setMargins(margrin,margrin,margrin,margrin);
                        tiet.requestLayout();

                        TextView mon = new TextView(this);
                        mon.setText("");
                        mon.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                        mon.setTextColor(getResources().getColor(R.color.black));
                        mon.setGravity(Gravity.CENTER);
                        mon.setPadding(padding,0,padding,0);
                        newRow.addView(mon,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,4f));
                        ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams) mon.getLayoutParams();
                        p2.setMargins(margrin,margrin,margrin,margrin);
                        mon.requestLayout();
                        Log.d(TAG,"add empty");

                        TextView lop = new TextView(this);
                        lop.setText("");
                        lop.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                        lop.setTextColor(getResources().getColor(R.color.black));
                        lop.setGravity(Gravity.CENTER);
                        lop.setPadding(padding,0,padding,0);
                        newRow.addView(lop,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,2f));
                        ViewGroup.MarginLayoutParams p3 = (ViewGroup.MarginLayoutParams) lop.getLayoutParams();
                        p3.setMargins(margrin,margrin,margrin,margrin);
                        lop.requestLayout();
                    }
                }
                lctTable.addView(newRow,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0,1f));
            }
            Log.d(TAG,"add row to table");
        }
    }

    @Override
    public void onBackPressed() {
        if (mainLayout.getVisibility() == View.VISIBLE){
            finish();
        }else {
            mainLayout.setVisibility(View.VISIBLE);
            tkbLayout.setVisibility(View.GONE);
            lctLayout.setVisibility(View.GONE);

        }
    }
}