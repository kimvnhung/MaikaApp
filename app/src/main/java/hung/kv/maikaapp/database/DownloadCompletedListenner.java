package hung.kv.maikaapp.database;

public interface DownloadCompletedListenner {
    void onDownloadCompleted(long downloadId,String path);
}