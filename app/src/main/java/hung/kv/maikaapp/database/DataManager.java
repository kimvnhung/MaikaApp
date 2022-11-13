package hung.kv.maikaapp.database;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class DataManager {
    private static final String TAG = DataManager.class.getName();
    private static DataManager m_instance = null;
    private static final String TKB_HS = "tkb_hs.csv";
    private static final String TKB_GV = "tkb_gv.csv";
    private static final String LICH_CONG_TAC = "tkb_truong.csv";
    private static final String MO_TA_TRUONG = "mo_ta_truong.csv";
    private boolean isUpdating = false;

    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Teacher> teachers = new ArrayList<>();

    private Context mContext;
    public DataManager(Context context) {
        this.mContext = context;
    }

    public void updateData(){
        isUpdating = true;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+TKB_HS);
        if (file.exists()){
            file.delete();
        }

        if (!updateTKBHS()) {
            Toast.makeText(mContext.getApplicationContext(),"Cập nhật thời khóa biểu HS lỗi!!!",Toast.LENGTH_SHORT).show();
        }

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+TKB_GV);
        if (file.exists()){
            file.delete();
        }
        if (!updateTKBGV()) {
            Toast.makeText(mContext.getApplicationContext(),"Cập nhật thời khóa biểu GV lỗi!!!",Toast.LENGTH_SHORT).show();
        }

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+LICH_CONG_TAC);
        if (file.exists()){
            file.delete();
        }
        if (!updateLCT()) {
            Toast.makeText(mContext.getApplicationContext(),"Cập nhật lịch công tác lỗi!!!",Toast.LENGTH_SHORT).show();
        }

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+MO_TA_TRUONG);
        if (file.exists()){
            file.delete();
        }
        if (!updateMotatruong()) {
            Toast.makeText(mContext.getApplicationContext(),"Cập nhật mô tả trường lỗi!!!",Toast.LENGTH_SHORT).show();
        }
        isUpdating = false;
    }

    private boolean updateTKBHS() {
        String file_url = "https://docs.google.com/spreadsheets/d/1Lj6eRqe3jnR9OOCfsmNbpJDHE30m_Sf8NnD7QQCPkbU/export?gid=0&format=csv";
        try {
            String filePath = downloadFile(mContext,file_url,TKB_HS);

            return true;
        }catch (Exception e){
            Log.e(TAG,"exception "+e.getMessage());
        }
        return false;
    }

    private boolean updateTKBGV() {
        String file_url = "https://docs.google.com/spreadsheets/d/1Lj6eRqe3jnR9OOCfsmNbpJDHE30m_Sf8NnD7QQCPkbU/export?gid=307757363&format=csv";
        try {
            downloadFile(mContext,file_url,TKB_GV);
            return true;
        }catch (Exception e){
            Log.e(TAG,"exception "+e.getMessage());
        }
        return false;
    }

    private boolean updateLCT() {
        String file_url = "https://docs.google.com/spreadsheets/d/1Lj6eRqe3jnR9OOCfsmNbpJDHE30m_Sf8NnD7QQCPkbU/export?gid=2063429296&format=csv";
        try {
            downloadFile(mContext,file_url,LICH_CONG_TAC);
            return true;
        }catch (Exception e){
            Log.e(TAG,"exception "+e.getMessage());
        }
        return false;
    }

    private boolean updateMotatruong() {
        String file_url = "https://docs.google.com/spreadsheets/d/1Lj6eRqe3jnR9OOCfsmNbpJDHE30m_Sf8NnD7QQCPkbU/export?gid=1919178051&format=csv";
        try {
            downloadFile(mContext,file_url,MO_TA_TRUONG);
            return true;
        }catch (Exception e){
            Log.e(TAG,"exception "+e.getMessage());
        }
        return false;
    }

    private String downloadFile(Context context,String url, String outputFile) {
        DownloadManager.Request dmr = new DownloadManager.Request(Uri.parse(url));

        dmr.setTitle(outputFile);
        dmr.setDescription("Some descrition about file"); //optional
        dmr.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, outputFile);
        dmr.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        dmr.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(dmr);
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+ File.separator+outputFile).getAbsolutePath();
    }



}
