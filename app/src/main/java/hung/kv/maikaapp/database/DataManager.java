package hung.kv.maikaapp.database;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class DataManager implements DownloadCompletedListenner {
    private static final String TAG = DataManager.class.getName();
    private static DataManager m_instance = null;
    private static final String TKB_HS = "tkb_hs.csv";
    private static long TKB_HS_DOWNLOADID = 0;
    private static final String TKB_GV = "tkb_gv.csv";
    private static long TKB_GV_DOWNLOADID = 0;
    private static final String LICH_CONG_TAC = "tkb_truong.csv";
    private static long LICH_CONG_TAC_DOWNLOADID = 0;
    private static final String MO_TA_TRUONG = "mo_ta_truong.csv";
    private static long MO_TA_TRUONG_DOWNLOADID = 0;
    private boolean isUpdating = true;
    DownloadManager manager = null;
    DownloadReceiver downloadReceiver = null;

    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Teacher> teachers = new ArrayList<>();
    public ArrayList<String> places = new ArrayList<>();
    String[][] hdd = null;
    Date from,to;

    private Context mContext;
    public DataManager(Context context) {
        this.mContext = context;

        manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        downloadReceiver = new DownloadReceiver(this);
        mContext.registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public String getHDD(String placeA, String placeB){
        int idxA = places.indexOf(placeA);
        int idxB = places.indexOf(placeB);
        if (idxA >= 0 && idxB >= 0 && hdd != null){
            if (idxA < hdd.length && idxB <hdd[0].length){
                return hdd[idxA][idxB];
            }
        }
        return "";
    }

    public void updateData(){
        isUpdating = true;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+TKB_HS);
        if (file.exists()){
            file.delete();
        }

        if (!updateTKBHS()) {
            showToast("Cập nhật thời khóa biểu HS lỗi!!!");
        }

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+TKB_GV);
        if (file.exists()){
            file.delete();
        }
        if (!updateTKBGV()) {
            showToast("Cập nhật thời khóa biểu GV lỗi!!!");
        }

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+LICH_CONG_TAC);
        if (file.exists()){
            file.delete();
        }
        if (!updateLCT()) {
            showToast("Cập nhật lịch công tác lỗi!!!");
        }

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+MO_TA_TRUONG);
        if (file.exists()){
            file.delete();
        }
        if (!updateMotatruong()) {
            showToast("Cập nhật mô tả trường lỗi!!!");
        }
        isUpdating = false;
    }

    private boolean updateTKBHS() {
        String file_url = "https://docs.google.com/spreadsheets/d/1Lj6eRqe3jnR9OOCfsmNbpJDHE30m_Sf8NnD7QQCPkbU/export?gid=0&format=csv";
        try {
            TKB_HS_DOWNLOADID = downloadFile(file_url,TKB_HS);
            return true;
        }catch (Exception e){
            Log.e(TAG,"exception "+e.getMessage());
        }
        return false;
    }

    private void showToast(String content) {
        try{
            ((AppCompatActivity)mContext).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.e(TAG,"error : "+e.getMessage());
        }
    }

    private boolean updateTKBGV() {
        String file_url = "https://docs.google.com/spreadsheets/d/1Lj6eRqe3jnR9OOCfsmNbpJDHE30m_Sf8NnD7QQCPkbU/export?gid=307757363&format=csv";
        try {
            TKB_GV_DOWNLOADID = downloadFile(file_url,TKB_GV);
            return true;
        }catch (Exception e){
            Log.e(TAG,"exception "+e.getMessage());
        }
        return false;
    }

    private boolean updateLCT() {
        String file_url = "https://docs.google.com/spreadsheets/d/1Lj6eRqe3jnR9OOCfsmNbpJDHE30m_Sf8NnD7QQCPkbU/export?gid=2063429296&format=csv";
        try {
            LICH_CONG_TAC_DOWNLOADID = downloadFile(file_url,LICH_CONG_TAC);
            return true;
        }catch (Exception e){
            Log.e(TAG,"exception "+e.getMessage());
        }
        return false;
    }

    private boolean updateMotatruong() {
        String file_url = "https://docs.google.com/spreadsheets/d/1Lj6eRqe3jnR9OOCfsmNbpJDHE30m_Sf8NnD7QQCPkbU/export?gid=1919178051&format=csv";
        try {
            MO_TA_TRUONG_DOWNLOADID = downloadFile(file_url,MO_TA_TRUONG);
            return true;
        }catch (Exception e){
            Log.e(TAG,"exception "+e.getMessage());
        }
        return false;
    }

    private long downloadFile(String url, String outputFile) {
        DownloadManager.Request dmr = new DownloadManager.Request(Uri.parse(url));

        dmr.setTitle(outputFile);
        dmr.setDescription("Some descrition about file"); //optional
        dmr.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, outputFile);
        dmr.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        dmr.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        return manager.enqueue(dmr);
    }

    @Override
    public void onDownloadCompleted(long downloadId, String path) {
        if (downloadId == TKB_HS_DOWNLOADID){
            try {
                File csvfile = new File(path);
                CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
                String[] nextLine;
                int row = 0;
                ArrayList<String> classNames = new ArrayList<>();
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
//                    String result = "";
//                    for(int i=0;i<nextLine.length;i++){
//                        result += nextLine[i]+" | ";
//                    }
//                    Log.d(TAG, (row)+" : "+result);
                    if (row == 1){
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            from = format.parse(nextLine[1]);
                            to = format.parse(nextLine[2]);
                        } catch (ParseException e) {
                            Log.e(TAG,"err : "+e.getMessage());
                        }
                    }else if (row == 3) {
                        for (int i=0;i<nextLine.length;i++){
                            if (i>=2){
                                classNames.add(nextLine[i]);
                            }
                        }

                        for (int i=0;i<classNames.size();i++){
                            students.add(new Student("anonymous",getAgeFromClass(classNames.get(i)),classNames.get(i)));
                        }
                    }else if(row >= 4 && row <= 33){
                        for (int i=0;i<students.size();i++){
                            int period = 0;
                            try{
                                period = Integer.parseInt(nextLine[1]);
//                                Log.d(TAG,"period "+period);
                            }catch (Exception e){
                                Log.e(TAG,"error : "+e.getMessage());
                            }
                            Task task = new Task(nextLine[2].split("-")[0],"trường học",getTimeFromDayAndPeriod((row-4)/5+2,period),getTimeToDayAndPeriod((row-4)/5+2,period));
                            students.get(i).getTasks().add(task);
                        }
                    }else if (row >= 37){
                        if (!nextLine[0].isEmpty()){
                            Student student = new Student(nextLine[1],getAgeFromClass(nextLine[2]),nextLine[2],nextLine[3],nextLine[4]);
                            for (int i=0;i<students.size();i++){
                                if (students.get(i).getClassName().equals(student.getClassName()) && students.get(i).getName().equals("anonymous")){
                                    student.setTasks(students.get(i).getTasks());
                                    break;
                                }
                            }
                            students.add(student);
                        }
                    }
                    row++;
                }

//                for (int i=0;i<students.size();i++){
//                    Log.d(TAG,students.get(i).toString());
//                }
            } catch (Exception e) {
                Log.e(TAG,"error : "+e.getMessage());
                showToast("The specified file was not found");
            }
        }
        if (downloadId == TKB_GV_DOWNLOADID){
            try {
                File csvfile = new File(path);
                CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
                String[] nextLine;
                int row = 0;
                ArrayList<String> teacherNames = new ArrayList<>();
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
                    String result = "";
                    for(int i=0;i<nextLine.length;i++){
                        result += nextLine[i]+" | ";
                    }
                    Log.d(TAG, (row)+" : "+result);
                    if (row == 1){
//                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//                        try {
//                            from = format.parse(nextLine[1]);
//                            to = format.parse(nextLine[2]);
//                        } catch (ParseException e) {
//                            Log.e(TAG,"err : "+e.getMessage());
//                        }
                        Log.d(TAG,"from "+nextLine[1]+" to "+nextLine[2]);
                    }else if (row == 3) {
                        for (int i=0;i<nextLine.length;i++){
                            if (i>=2){
                                teacherNames.add(nextLine[i]);
                            }
                        }

                        for (int i=0;i<teacherNames.size();i++){
                            teachers.add(new Teacher(teacherNames.get(i),getRandomAge(),new ArrayList<>()));
                        }
                    }else if(row >= 4 && row <= 33){
                        for (int i=0;i<students.size();i++){
                            int period = 0;
                            try{
                                period = Integer.parseInt(nextLine[1]);
                            }catch (Exception e){
                                Log.e(TAG,"error : "+e.getMessage());
                            }
                            String[] spiled = nextLine[2].split("-");
                            Task task = new Task(spiled[0],spiled.length>=2?spiled[1]:"",getTimeFromDayAndPeriod((row-4)/5+2,period),getTimeToDayAndPeriod((row-4)/5+2,period));
                            teachers.get(i).getTasks().add(task);
                        }
                    }else if (row >= 37){
                        if (!nextLine[0].isEmpty()){
                            for (int i=0;i<teachers.size();i++){
                                if (teachers.get(i).getName().equals(nextLine[1])){
                                    teachers.get(i).setUsername(nextLine[2]);
                                    teachers.get(i).setPassword(nextLine[3]);
                                    break;
                                }
                            }
                        }
                    }
                    row++;
                }
            } catch (Exception e) {
                Log.e(TAG,"error : "+e.getMessage());
                showToast("The specified file was not found");
            }
        }
        if (downloadId == LICH_CONG_TAC_DOWNLOADID){
            try {
                File csvfile = new File(path);
                CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
                String[] nextLine;
                int row = 0;
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
                    String result = "";
                    for(int i=0;i<nextLine.length;i++){
                        result += nextLine[i]+" | ";
                    }
                    Log.d(TAG, (row)+" : "+result);
                    if (row >= 4 && row <= 15){
                        if (!nextLine[2].isEmpty()){
                            Task newTask = new Task(nextLine[2],nextLine[4],getTimeFromDayAndLCT((row-4)/2+2,nextLine[3]),getTimeToDayAndLCT((row-4)/2+2,nextLine[1]));
                            for (int i=0;i<teachers.size();i++){
                                if (teachers.get(i).getName().equals(nextLine[5])){
                                    teachers.get(i).getTasks().add(newTask);
                                }
                                if (i == teachers.size()-1){
                                    teachers.add(new Teacher(nextLine[5],getRandomAge(),new ArrayList<>()));
                                    teachers.get(i+1).getTasks().add(newTask);
                                    break;
                                }
                            }
                        }
                    }
                    row++;
                }
            } catch (Exception e) {
                Log.e(TAG,"error : "+e.getMessage());
                showToast("The specified file was not found");
            }
        }
        if (downloadId == MO_TA_TRUONG_DOWNLOADID){
            try {
                File csvfile = new File(path);
                CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
                String[] nextLine;
                int row = 0;
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
                    String result = "";
                    for(int i=0;i<nextLine.length;i++){
                        result += nextLine[i]+" | ";
                    }
                    Log.d(TAG, (row)+" : "+result);
                    if (row == 1){
                        int size = nextLine.length;
                        for (int i=2;i<size;i++){
                            if (nextLine[i].isEmpty()){
                                break;
                            }
                            places.add(nextLine[i]);
                        }
                    }else if (row >= 2){
                        if (hdd == null){
                            hdd = new String[places.size()][places.size()];
                        }
                        for (int i=0;i<places.size();i++){
                            hdd[row-2][i] = nextLine[2+i];
                        }
                    }
                    row++;
                }
            } catch (Exception e) {
                Log.e(TAG,"error : "+e.getMessage());
                showToast("The specified file was not found");
            }
        }
    }

    private int getRandomAge() {
        return new Random().nextInt(65-23)+23;
    }

    private static final long ONE_DAY = 86400000;
    private static final long ONE_HOUR = 3600000;
    private static final long ONE_MINUTE = 60000;

    private Date getTimeFromDayAndPeriod(int dayInWeek, int period) {
        if (from != null){
            Date result = new Date(from.getTime());
//            Log.d(TAG,"date before "+result.getDate());
//            Log.d(TAG,"day in week "+dayInWeek+" "+GetDayInWeek(from));
            result.setTime(result.getTime()+(dayInWeek-GetDayInWeek(from))*ONE_DAY);
            result.setTime(result.getTime()+(7+(period-1))*ONE_HOUR);
//            Log.d(TAG,"date after "+result.getDate());
            return result;
        }
        return null;
    }

    private Date getTimeToDayAndPeriod(int dayInWeek, int period) {
        if (from != null){
            Date result = new Date(from.getTime());
            result.setTime(result.getTime()+(dayInWeek-GetDayInWeek(from))*ONE_DAY);
            result.setTime(result.getTime()+(7+(period-1))*ONE_HOUR);
            result.setTime(result.getTime()+45*ONE_MINUTE);
            return result;
        }
        return null;
    }

    public static int GetDayInWeek(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    private Date getTimeFromDayAndLCT(int dayInWeek, String hours) {
        if (from != null){
            Date result = new Date(from.getTime());
            result.setTime(result.getTime()+(dayInWeek-GetDayInWeek(from))*ONE_DAY);
            String[] hoursSplited = hours.split("h");
            int hour = 7;
            int minutes = 0;
            try {
                hour = Integer.parseInt(hoursSplited[0]);
                minutes = Integer.parseInt(hoursSplited[1]);
            }catch (Exception e){
                Log.e(TAG,"error : "+e.getMessage());
            }
            result.setTime(result.getTime()+hour*ONE_HOUR);
            result.setTime(result.getTime()+minutes*ONE_MINUTE);
            return result;
        }
        return null;
    }

    private Date getTimeToDayAndLCT(int dayInWeek, String buoi) {
        if (from != null){
            Date result = new Date(from.getTime());
            result.setTime(result.getTime()+(dayInWeek-GetDayInWeek(from))*ONE_DAY);
            int hour = 7;
            int minutes = 0;
            try {
                hour = buoi.equals("Sáng")?12:17;
                minutes = buoi.equals("Sáng")?0:30;
            }catch (Exception e){
                Log.e(TAG,"error : "+e.getMessage());
            }
            result.setTime(result.getTime()+hour*ONE_HOUR);
            result.setTime(result.getTime()+minutes*ONE_MINUTE);
            return result;
        }
        return null;
    }

    private int getAgeFromClass(String className) {
        if (className.length() > 0){
            if (className.charAt(0) == '6'){
                return 11;
            }else if (className.charAt(0) == '7'){
                return 12;
            }else if (className.charAt(0) == '8'){
                return 13;
            }else if (className.charAt(0) == '9'){
                return 14;
            }
        }
        return 0;
    }

    public boolean isUpdating() {
        return isUpdating;
    }

    class DownloadReceiver extends BroadcastReceiver {
        private DownloadCompletedListenner listenner = null;
        public DownloadReceiver(DownloadCompletedListenner listenner) {
            this.listenner = listenner;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (downloadId == -1)
                return;

            // query download status
            Cursor cursor = manager.query(new DownloadManager.Query().setFilterById(downloadId));
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = index==-1?index:cursor.getInt(index);
                if(status == DownloadManager.STATUS_SUCCESSFUL){

                    // download is successful
                    index = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                    String uri = index==-1?"":cursor.getString(index);
                    if (listenner != null){
                        listenner.onDownloadCompleted(downloadId,Uri.parse(uri).getPath());
                    }
                }
                else {
                    // download is assumed cancelled
                }
            }
            else {
                // download is assumed cancelled
            }
        }


    };

    public ArrayList<String> getTeacherNames() {
        ArrayList<String> result = new ArrayList<>();
        for (int i=0;i<teachers.size();i++){
            result.add(teachers.get(i).getName());
        }
        return result;
    }

    public SchoolPerson isValidAccount(String username,String password){
        for (int i=0;i<students.size();i++){
            if (students.get(i).getUsername().equals(username) && students.get(i).getPassword().equals(password)){
                return students.get(i);
            }
        }

        for (int i=0; i<teachers.size(); i++){
            if (teachers.get(i).getUsername().equals(username) && teachers.get(i).getPassword().equals(password)){
                return teachers.get(i);
            }
        }

        return null;
    }

    public String getLCT(String cb){
        for (int i=0;i<teachers.size();i++){
            if (teachers.get(i).getName().equals(cb)){
                Teacher teacher = teachers.get(i);
                for(int j=0;j<teacher.getTasks().size();j++){
                    if (teacher.getTasks().get(j).isProcessing()){
                        return "Cán bộ "+teacher.getName()+" đang thực hiện công tác "+teacher.getTasks().get(j).getName()+" tại "+teacher.getTasks().get(j).getPlace();
                    }
                }
            }
        }
        return "Cán bộ "+cb+" hiện không có lịch công tác, quý khách có thể gặp mặt!";
    }

}
