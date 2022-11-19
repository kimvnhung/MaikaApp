package hung.kv.maikaapp;

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

import java.util.ArrayList;

import hung.kv.maikaapp.database.DataManager;
import hung.kv.maikaapp.database.Student;
import hung.kv.maikaapp.database.Task;
import hung.kv.maikaapp.voicehandle.UserType;

public class StudentActivity extends MaikaActivity {
    private final String TAG = StudentActivity.class.getName();

    Button tkbBtn,lhdBtn;
    LinearLayout mainLayout,tkb_layout,lhdLayout;
    TableLayout tkbTable,lhdTable;

    Student student ;
    TextView stName,stClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        student = (Student) LoginActivity.db.isValidAccount(username,password);
        Log.d(TAG,"student task count "+student.getTasks().size());

        initAssistant(student.getName(), UserType.STUDENT);

        initView();
    }

    private void initView() {
        findViewById(R.id.back_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentActivity.this.onBackPressed();
            }
        });
        findViewById(R.id.back_tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentActivity.this.onBackPressed();
            }
        });

        stName = findViewById(R.id.ten_hs_tv);
        stClass = findViewById(R.id.ten_lop_tv);

        if (student != null){
            stName.setText(student.getName());
            stClass.setText(student.getClassName());
        }

        tkbBtn = findViewById(R.id.tkb_hs_btn);
        tkbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initTKB();
                mainLayout.setVisibility(View.GONE);
                tkb_layout.setVisibility(View.VISIBLE);
                lhdLayout.setVisibility(View.GONE);
            }
        });
        lhdBtn = findViewById(R.id.lhd_btn);
        lhdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.GONE);
                tkb_layout.setVisibility(View.GONE);
                lhdLayout.setVisibility(View.VISIBLE);
            }
        });
        mainLayout = findViewById(R.id.main_layout);
        tkb_layout = findViewById(R.id.tkb_hs_layout);
        lhdLayout = findViewById(R.id.lct_hs_layout);
        tkbTable = findViewById(R.id.table_tkb);
        lhdTable = findViewById(R.id.table_lhd);
    }

    private void initTKB(){
        tkbTable.removeAllViews();

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

                for (int k=0;k<student.getTasks().size();k++){
                    Task task = student.getTasks().get(k);
                    Log.d(TAG,"Period start "+task.getPeriodStart()+" j "+j);
                    if (DataManager.GetDayInWeek(task.getFrom()) == i && task.getPeriodStart() == j){
                        TextView mon = new TextView(this);
                        mon.setText(task.getName());
                        mon.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                        mon.setTextColor(getResources().getColor(R.color.black));
                        mon.setGravity(Gravity.CENTER);
                        mon.setPadding(padding,0,padding,0);
                        newRow.addView(mon,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,3f));
                        ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams) mon.getLayoutParams();
                        p2.setMargins(margrin,margrin,margrin,margrin);
                        mon.requestLayout();
                        Log.d(TAG,"add subject");
                        break;
                    }

                    if (k == student.getTasks().size()-1){
                        TextView mon = new TextView(this);
                        mon.setText("");
                        mon.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.txt_size_lv2));
                        mon.setTextColor(getResources().getColor(R.color.black));
                        mon.setGravity(Gravity.CENTER);
                        mon.setPadding(padding,0,padding,0);
                        newRow.addView(mon,new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,3f));
                        ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams) mon.getLayoutParams();
                        p2.setMargins(margrin,margrin,margrin,margrin);
                        mon.requestLayout();
                        Log.d(TAG,"add empty");
                    }
                }
                tkbTable.addView(newRow,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0,1f));
            }
            Log.d(TAG,"add row to table");
        }
    }

    private void initLHD(){

    }

    @Override
    public void onBackPressed() {
        if (mainLayout.getVisibility() == View.VISIBLE){
            finish();
        }else {
            mainLayout.setVisibility(View.VISIBLE);
            lhdLayout.setVisibility(View.GONE);
            tkb_layout.setVisibility(View.GONE);
        }
    }
}