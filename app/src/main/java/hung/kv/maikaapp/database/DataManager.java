package hung.kv.maikaapp.database;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class DataManager {
    private static final String TAG = DataManager.class.getName();
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static DataManager m_instance = null;
    DownloadManager manager = null;
    private static final String DOWNLOAD_PATH = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            + "/maika.data";
    private static final String DOWNLOAD_FILE = "Maika.csv";

    private DataManager(){

    }
    public static DataManager instance() {
        if (m_instance == null) {
            m_instance = new DataManager();
        }

        return m_instance;
    }
    public boolean downloadCSV(Context context){
        String file_url = "https://docs.google.com/spreadsheets/d/1Lj6eRqe3jnR9OOCfsmNbpJDHE30m_Sf8NnD7QQCPkbU/export?gid=0&format=csv";
        try {
            downloadFile(context,file_url,DOWNLOAD_FILE);
        }catch (Exception e){
            Log.e(TAG,"exception "+e.getMessage());
        }

        return true;
    }

    public void downloadFile(Context context,String url, String outputFile) {
        DownloadManager.Request dmr = new DownloadManager.Request(Uri.parse(url));

        dmr.setTitle(outputFile);
        dmr.setDescription("Some descrition about file"); //optional
        dmr.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, outputFile);
        dmr.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        dmr.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(dmr);
    }



}
